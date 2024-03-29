# WARNING
# Do not edit this file manually. Your changes will be overwritten with next FlowPDF update.
# WARNING

package FlowPDF::Context;

=head1 NAME

FlowPDF::Context

=head1 AUTHOR

CloudBees

=head1 DESCRIPTION

FlowPDF::Context is a class that represents current running context.

This class allows user to access procedure parameters, config values and define a step result.

=head1 METHODS

=cut


use base qw/FlowPDF::BaseClass2/;
use FlowPDF::Types;
__PACKAGE__->defineClass({
    procedureName           => FlowPDF::Types::Scalar(),
    stepName                => FlowPDF::Types::Scalar(),
    runContext              => FlowPDF::Types::Scalar(),
    pluginObject            => FlowPDF::Types::Any(),
    ec                      => FlowPDF::Types::Reference('ElectricCommander'),
    currentStepParameters   => FlowPDF::Types::Reference('FlowPDF::StepParameters'),
    currentStepConfigValues => FlowPDF::Types::Reference('FlowPDF::Config'),
    currentProjectName      => FlowPDF::Types::Scalar(),
    hasConfigField          => FlowPDF::Types::Enum('1', '0'),
});

use strict;
use warnings;

use FlowPDF::Config;
use FlowPDF::StepParameters;
use FlowPDF::Parameter;
use FlowPDF::Credential;
use FlowPDF::StepResult;
use FlowPDF::Log;
use FlowPDF::Log::FW;
use FlowPDF::Client::REST;
use FlowPDF::Helpers qw/trim/;
use Try::Tiny;

# Exceptions
use FlowPDF::Exception::ConfigDoesNotExist;
use FlowPDF::Exception::EntityDoesNotExist;
use FlowPDF::Exception::RuntimeException;
use FlowPDF::Exception::UnexpectedEmptyValue;

use FlowPDF::Constants qw/
    DEBUG_LEVEL_PROPERTY
    HTTP_PROXY_URL_PROPERTY
    PROXY_CREDENTIAL_PROPERTY
    BASIC_AUTH_CREDENTIAL_PROPERTY
    AUTH_SCHEME_PROPERTY
    AUTH_SCHEME_VALUE_FOR_BASIC_AUTH
/;

use Carp;
use Data::Dumper;
use ElectricCommander;

sub new {
    my ($class, @params) = @_;

    my $self = $class->SUPER::new(@params);
    my $configValues = undef;

    $configValues = $self->getConfigValues();
    if ($configValues) {
        my $debugLevel = $configValues->getParameter(DEBUG_LEVEL_PROPERTY);
        if ($debugLevel) {
            FlowPDF::Log::setLogLevel($debugLevel->getValue());
            fwLogDebug("Debug level is set to ", $debugLevel->getValue());
        }
    }
    unless ($self->getEc()) {
        $self->setEc(ElectricCommander->new());
    }

    $self->setRunContext($self->buildRunContext());
    $self->setCurrentProjectName($self->retrieveCurrentProjectName());
    return $self;
}


=head2 bailOut($params)

=head3 Description

An advanced version of bailOut from L<FlowPDF::Helpers>::bailOut.
This version has connections with CloudBees Flow and has following benefits:

=over 4

=item Sets a Pipeline Summary.

=item Sets a JobStep Summary.

=item Sets outcome to Error.

=item Adds diagnostic messages to Diagnostic tab of the job.

=item Add suggestion if provided.

=back

=head3 Parameters

=over 4

=item (Required)(HASH ref) A hash reference with message and suggestion fields. Message is mandatory, suggestion is optional

=cut

=back

=head3 Returns

=over 4

=item None

=back

=head3 Exceptions

=over 4

=item None

=back

=head3 Usage

This function is being used through context object. Also, note, that this function created it's own L<FlowPDF::StepResult> object.
It means, that if this function is being used, no other L<FlowPDF::StepResult> objects will be auto-applied.

This function uses bailOut function from L<FlowPDF::Helpers> package and causes an exception,
 that could not be handled and your step will exit immedieately with code 1.

%%%LANG=perl%%%

# this function will call an exit 1.
$context->bailOut({
    message    => "File not found",
    suggestion => 'Please, check that you are using correct resource.'
});

%%%LANG%%%

=cut

sub bailOut {
    my ($self, $params) = @_;

    if (!ref $params) {
        my $message = $params;
        $params = {message => $message};
    }

    if (!$params->{message} && !$params->{suggestion}) {
        exit 1;
    }

    my $stepResult = $self->newStepResult();
    my $message = $params->{message};

    if ($self->getRunContext() eq 'pipeline') {
        $stepResult->setPipelineSummary("BAILED OUT: ", $message);
    }

    $stepResult->setJobStepOutcome('error');
    $stepResult->setJobStepSummary($message);
    logErrorDiag("BAILED OUT: $message");
    if ($params->{suggestion}) {
        logInfoDiag($params->{suggestion});
    }
    $stepResult->apply();
    FlowPDF::Helpers::bailOut($message);
}

=head2 getRuntimeParameters()

=head3 Description

A simplified accessor for the step parameters and config values.
This function returns a regular perl HASH ref from  parameters and config values.

Credentials from 'credential' parameter will be present in this hashref as 'user' and password.
Credentials, that have name like 'proxy_credential' will be mapped to 'proxy_user' and 'proxy_password' parameters.

=head3 Parameters

=over 4

=item None

=back

=head3 Returns

=over 4

=item (HASH ref) A merged parameters from step parameters and config values.

=back

=head3 Usage

For example, you have 'query' parameter and 'location' parameter in your procedure form.xml.
In your configuration you have 'credential', 'proxy_credential', 'contentType' and 'userAgent'.
In that case you can get runtime parameters like:

%%%LANG=perl%%%

    my $simpleParams = $context->getRuntimeParameters();

%%%LANG%%%

Now, $simpleParams is:

%%%LANG=perl%%%

    {
        # Values from config
        user => 'admin',
        password => '12345',
        proxy_user => 'proxy',
        proxy_password => 'qwerty',
        contentType => 'application/json',
        userAgent => 'Mozilla',
        # values from step parameters
        location => 'California',
        query => 'SELECT * FROM commander.plugins'
    }

%%%LANG%%%

=cut

sub getRuntimeParameters {
    my ($self) = @_;

    my $p = $self->getStepParameters();
    my $c = undef;
    $c = $self->getConfigValues();

    # eval {
    #     $c = $self->getConfigValues();
    #     1;
    # } or do {
    #     if ($self->getHasConfigField()) {
    #         FlowPDF::Helpers::bailOut("Can't get config: $@");
    #     }
    # };

    my $retval = {};
    my $stepParametersAsHashref = {};
    my $configValuesAsHashref = {};

    if ($p) {
        $stepParametersAsHashref = $p->asHashref();
    }
    if ($c) {
        $configValuesAsHashref = $c->asHashref();
    }
    $retval = {
        %$stepParametersAsHashref,
        %$configValuesAsHashref
    };

    return $retval;
}


=head2 getStepParameters()

=head3 Description

Returns a L<FlowPDF::StepParameters> object to be used as accessor for current step parameters.
This method does not require parameters.

=head3 Parameters

=over 4

=item None

=back

=head3 Returns

=over 4

=item (L<FlowPDF::StepParameters>) Parameters for the current step

=back

=head3 Usage

%%%LANG=perl%%%

    my $params = $context->getStepParameters();
    # this method returns a L<FlowPDF::Parameter> object, or undef, if no parameter with that name has been found.
    my $param = $params->getParameter('myStepParameter');
    if ($param) {
        print "Param value is:", $param->getValue(), "\n";
    }

%%%LANG%%%

=cut

sub getStepParameters {
    my ($context) = @_;

    if (my $retval = $context->getCurrentStepParameters()) {
        return $retval;
    }

    my $stepParametersHash = $context->getCurrentStepParametersAsHashref();
    my $parametersList = [];
    my $parameters = {};
    for my $k (keys %$stepParametersHash) {
        if ($context->getPluginObject()->isConfigField($k)) {
            $context->setHasConfigField('1');
        }
        push @{$parametersList}, $k;
        my $p;
        if (!ref $stepParametersHash->{$k}) {
            $p = FlowPDF::Parameter->new({
                name  => $k,
                value => $stepParametersHash->{$k}
            });
        }
        else {
            # it is a hash reference, so it is credential
            $p = FlowPDF::Credential->new({
                credentialName => $k,
                # TODO: Change it to something more reliable later.
                # Currently we have support of default credentials only.
                credentialType => 'default',
                userName => $stepParametersHash->{$k}->{userName},
                secretValue => $stepParametersHash->{$k}->{password},
            });
        }
        $parameters->{$k} = $p;
    }

    my $stepParameters = FlowPDF::StepParameters->new({
        parametersList => $parametersList,
        parameters => $parameters
    });

    # During create configuration we have step parameters instead of config values.
    # It happens because CreateConfiguration procedure is special.
    # All other procedures, if they have a config, requesting config values and
    # FlowPDF goes there and gets FlowPDF::Config object out of them.
    # But CreateConfiguration is the procedure that actually creates the config.
    # It means that config values are parameters for this procedure.
    # This procedure will create config out of them.
    # So, if we are in the situation when procedure is "magic" and step name is also "magic"
    # we will set config values as parameters and parameters as config values.
    if ($context->isCheckConnectionInCreateConfigurationContext()) {
        # Creating new step parameters
        my $newStepParameters = FlowPDF::StepParameters->new({});
        # This is **not** a clone. In perl we're assigning a reference,
        # so configValues is the same variable as stepParameters.
        # it is done to make it more readable.
        my $configValues = $stepParameters;
        # Blessing params to config values.
        bless $configValues, 'FlowPDF::Config';
        $context->populateDefaultConfigValues($configValues);
        $context->setCurrentStepConfigValues($configValues);
        $context->setCurrentStepParameters($newStepParameters);
        return $stepParameters;
    }
    $context->setCurrentStepParameters($stepParameters);
    return $stepParameters;
}


=head2 getConfigValues()

=head3 Description

This method returns a L<FlowPDF::Config> object that represents plugin configuration. This method does not require parameters.

=head3 Parameters

=over 4

=item (Optional) (HASH ref) parameters: configName: Optional configuration to get by name ignoring default autoresolve logic.

=back

=head3 Returns

=over 4

=item (L<FlowPDF::Config>) Plugin configuration for current run context

=back

=head3 Usage

%%%LANG=perl%%%

    my $configValues = $context->getConfigValues();
    my $cred = $configValues->getParameter('credential');
    if ($cred) {
        print "Secret value is: ", $cred->getSecretValue(), "\n";
    }
    # or get other config of the current plugin:
    my $configValues = $context->getConfigValues({configName => 'myOtherConfig'});

%%%LANG%%%

=cut

sub getConfigValues {
    my ($context, $params) = @_;

    my $configName = undef;
    my $autoDetectConfig = 1;

    # Get config name. Otherwise the function will determine automatically
    # where to get the config and will return it.
    if ($params->{configName}) {
        $configName = $params->{configName};
        $autoDetectConfig = 0;
        fwLogTrace("Optional configName => $configName has been provided to getConfigValues call.");
    }

    # TODO: Implement caching for custom configs if required.
    if ($autoDetectConfig && $context->getCurrentStepConfigValues()) {
        return $context->getCurrentStepConfigValues();
    }

    # This case is special. During CreateConfiguration we don't have config itself.
    # In this special context config values are yet stored in step parameters.
    # After checkConnection is successful these parameters are stored in configuration.
    if ($context->isCheckConnectionInCreateConfigurationContext()) {
        # This function call sets current step parameters and current step config values.
        # After that we can return config values which are already set.
        my $stepParameters = $context->getStepParameters();
        my $configValues = $context->getCurrentStepConfigValues();
        $context->populateDefaultConfigValues($configValues);
        return $configValues;
    }
    my $stepParameters = $context->getStepParameters();
    my $po = $context->getPluginObject();
    logTrace("Plugin Object: ", Dumper $po);
    my $configLocations = $po->getConfigLocations();
    my $configFields    = $po->getConfigFields();

    my $configField = undef;
    if ($autoDetectConfig) {
        for my $field (@$configFields) {
            if ($stepParameters->isParameterExists($field)) {
                $configField = $field;
                last;
            }
        }
        if ($autoDetectConfig && !$configField && $context->getHasConfigField()) {
            FlowPDF::Exception::EntityDoesNotExist->new(
                "No config field detected in current step parameters"
            )->throw();
        }
    }
    my $configHash = undef;

    if (!$autoDetectConfig || $context->getHasConfigField()) {
        my $configNameToGet = undef;
        if (defined $configName) {
            $configNameToGet = $configName;
        }
        else {
            $configNameToGet = $stepParameters->getParameter($configField)->getValue();
        }

        if ($configNameToGet =~ m|^/|) {
            # New configuration object
            my $plugin_project_name = sprintf(
                '%s-%s',
                $po->getPluginName(),
                $po->getPluginVersion()
            );

            $configHash = $context->_readPluginConfiguration($configNameToGet, $po, $plugin_project_name);
        }
        else {
            for my $location (@$configLocations) {
                my $tempConfig = $context->retrieveConfigByNameAndLocation(
                    $configNameToGet,
                    $location
                );

                if ($tempConfig) {
                    $configHash = $tempConfig;
                    last;
                }
            }
        }
    }
    # TODO: Improve this error message.
    # This exception is being thrown in 1 of 2 scenarios:
    #   1. When current step has config field and config does not exist.
    #   2. When optional config name is provided and config does not exist.
    if ((!$autoDetectConfig || $context->getHasConfigField()) && !$configHash) {
        FlowPDF::Exception::ConfigDoesNotExist->new({
            configName => $stepParameters->getParameter($configField)->getValue()
        })->throw();
    }
    my $keys = [];
    my $configValuesHash = {};
    $context->populateDefaultConfigValues($configHash);

    for my $k (keys %$configHash) {
        push @$keys, $k;

        my $tempRow = $configHash->{$k};
        # TODO: Refactor this a bit, move my $value to this line
        if (!ref $tempRow) {
            my $value = FlowPDF::Parameter->new({
                name => $k,
                value => $configHash->{$k}
            });
            $configValuesHash->{$k} = $value;
        }
        else {
            my $value = FlowPDF::Credential->new({
                credentialName => $k,
                # TODO: Change it to something more reliable later.
                credentialType => 'default',
                userName => $configHash->{$k}->{userName},
                secretValue => $configHash->{$k}->{password},
            });
            $configValuesHash->{$k} = $value;
        }
    }

    my $retval = FlowPDF::Config->new({
        parametersList => $keys,
        parameters => $configValuesHash
    });

    # We're caching config values if and only if
    # the default logic is being executed.
    if ($autoDetectConfig) {
        $context->setCurrentStepConfigValues($retval);
    }
    return $retval;
}

sub retrieveConfigByNameAndLocation {
    my ($self, $configName, $configLocation) = @_;

    my $po = $self->getPluginObject();
    my $plugin_project_name = sprintf(
        '%s-%s',
        $po->getPluginName(),
        $po->getPluginVersion()
    );
    # my $ec = $self->getEc();
    # Retrieving a places where plugin configs could be stored. They will be queued from first to last.
    my $config_locations = $po->getConfigLocations();
    my $config_fields = $po->getConfigFields();
    my $config_property_sheet = sprintf("/projects/%s/%s/%s", $plugin_project_name, $configLocation, $configName);
    fwLogDebug("Config property sheet: $config_property_sheet");
    my $property_sheet_id = eval { $self->getEc->getProperty($config_property_sheet)->findvalue('//propertySheetId')->string_value };
    if ($@) {
        return undef;
    }

    my $properties = $self->getEc->getProperties({propertySheetId => $property_sheet_id});
    my $retval = {};
    for my $node ( $properties->findnodes('//property')) {
        my $value = $node->findvalue('value')->string_value;
        my $name = $node->findvalue('propertyName')->string_value;
        # if ($self->isCredential($node)) {
        #     my $credential = $self->getCredentialFromConfig($configName, $node);
        #     $retval->{$name}->{userName} = $credential->{userName};
        #     $retval->{$name}->{password} = $credential->{password};
        #     if ($credential->{password}) {
        #         FlowPDF::Log->setMaskPatterns($credential->{password});
        #     }
        # }
        ## Deprecating this if branch
        if ($name =~ m/_?credential$/s) {
            fwLogTrace("Getting inside of credential section. Name: '$name', value: '$value'");
            # here we're doing a trick. We know, that credential in our config will be always named credential or
            # %keyword%_credential. Resulting credential is being stored by plugins as %cofingName%_%credentialField%.
            # So, for example, let's say that we have a config named config1, so,
            # credential, as exception, will be stored as config1, proxy_credential field will be stored as config1_proxy_credential
            # following logic implements this concept.
            my $credentialName = $configName;
            if ($name =~ m/(.*?_credential)$/m) {
                $credentialName = $configName . '_' . $1;
            }

            # adding support of external credentials.
            if ($value =~ m|^\/|s) {
                fwLogInfo("Value of credential field is starts from /. Using external credentials logic...");
                $credentialName = $value;
            }
            my $credentials = $self->getEc->getFullCredential($credentialName);
            my $user_name = $credentials->findvalue('//userName')->string_value;
            my $password = $credentials->findvalue('//password')->string_value;
            $retval->{$name}->{userName} = $user_name;
            $retval->{$name}->{password} = $password;
            if ($password) {
                FlowPDF::Log->setMaskPatterns($password);
            }
        }

        ## End of if branch deprecation
        else {
            if (!defined $value || $value eq '') {
                next;
            }
            $retval->{$name} = $value;
        }

    }

    fwLogDebug("Retval", Dumper $retval);
    return $retval;
}


# TODO: add pod for getConfigValuesAsHashref method
# TODO: add getConfigValuesAsHashref to list of public APIs
# TODO: think about getConfigValues to be present in public APIs list.
sub getConfigValuesAsHashref {
    my ($self) = @_;

    my $c = undef;
    $c = $self->getConfigValues();
    my $cKeys;
    my $retval = {};
    # eval {
    #     $c = $self->getConfigValues();
    #     1;
    # } or do {
    #     if ($self->getHasConfigField()) {
    #         FlowPDF::Helpers::bailOut("Can't get config: $@");
    #     }
    # };

    if ($c) {
        $retval = $c->asHashref();
    }
    return $retval;
}


=head2 newStepResult()

=head3 Description

This method returns an L<FlowPDF::StepResult> object, which is being used to work with procedure or pipeline stage output.

=head3 Parameters

=over 4

=item None

=back

=head3 Returns

=over 4

=item (L<FlowPDF::StepResult>) Object for manipulation with pipeline/procedure results.

=back

=head3 Usage

%%%LANG=perl%%%

    my $stepResult = $context->newStepResult();
    ...;
    $stepResult->apply();

%%%LANG%%%

=cut

sub newStepResult {
    my ($self) = @_;

    return FlowPDF::StepResult->new({
        context => $self,
        actions => [],
        cache   => {}
    });
}

# private function for now
sub buildRunContext {
    my ($self) = @_;

    my $ec = $self->getEc();
    my $context = 'pipeline';
    my $flowRuntimeId = '';

    eval {
        $flowRuntimeId = $ec->getProperty('/myFlowRuntimeState/id')->findvalue('//value')->string_value;
    };
    return $context if $flowRuntimeId;

    eval {
        $flowRuntimeId = $ec->getProperty('/myFlowRuntime/id')->findvalue('/value')->string_value();
    };
    return $context if $flowRuntimeId;

    eval {
        $flowRuntimeId = $ec->getProperty('/myPipelineStageRuntime/id')->findvalue('/value')->string_value();
    };
    return $context if $flowRuntimeId;

    $context = 'schedule';
    my $scheduleName = '';

    eval {
        $scheduleName = $self->getCurrentScheduleName();
        1;
    } or do {
        logError("error occured: $@");
    };

    if ($scheduleName) {
        return $context;
    }
    $context = 'procedure';
    return $context;
}

# private
sub getCurrentScheduleName {
    my ($self, $jobId) = @_;

    $jobId ||= $ENV{COMMANDER_JOBID};

    my $scheduleName = '';
    eval {
        my $result = $self->getEc()->getJobDetails($jobId);
        $scheduleName = $result->findvalue('//scheduleName')->string_value();
        if ($scheduleName) {
            # $self->logger()->info('Schedule found: ', $scheduleName);
            fwLogDebug("Schedule found: $scheduleName");
        };
        1;
    } or do {
        # $self->logger()->error($@);
        logError("Error: $@");
    };

    return $scheduleName;
}

# private
sub isParameterRequired {
    my ($self, $param) = @_;

    if (!$param) {
        FlowPDF::Helpers::bailOut("Parameter name is mandatory for isParameterRequired method");
    }
    my $procedureName = $self->getProcedureName();
    my $projectName   = $self->getPluginObject()->getPluginProjectName();
    my $ec            = $self->getEc();

    my $formalParameter = $ec->getFormalParameter({
        projectName         => $projectName,
        procedureName       => $procedureName,
        formalParameterName => $param
    });

    my $required = $formalParameter->findvalue('//required')->string_value();

    return $required;
}
# private
sub readActualParameter {
    my ($self, $param) = @_;

    my $ec = $self->getEc();
    my $retval;
    my $xpath;


    my @subs = ();
    push @subs, sub {
        my $jobId = $ec->getProperty('/myJob/id')->findvalue('//value')->string_value;
        my $xpath = $ec->getActualParameter({
            jobId => $jobId,
            actualParameterName => $param
        });
        return $xpath;
    };

    push @subs, sub {
        my $jobStepId = $ec->getProperty('/myJobStep/id')->findvalue('//value')->string_value;
        my $xpath = $ec->getActualParameter({
            jobStepId => $jobStepId,
            actualParameterName => $param,
        });
        return $xpath;
    };

    push @subs, sub {
        my $jobStepId = $ec->getProperty('/myJobStep/parent/id')->findvalue('//value')->string_value;
        my $xpath = $ec->getActualParameter({
            jobStepId => $jobStepId,
            actualParameterName => $param,
        });
        return $xpath;
    };


    push @subs, sub {
        my $jobStepId = $ec->getProperty('/myJobStep/parent/parent/id')->findvalue('//value')->string_value;
        my $xpath = $ec->getActualParameter({
            jobStepId => $jobStepId,
            actualParameterName => $param,
        });
        return $xpath;
    };


    for my $sub (@subs) {
        my $xpath = eval { $sub->() };
        if (!$@ && $xpath && $xpath->exists('//actualParameterName')) {
            my $val = $xpath->findvalue('//value')->string_value;
            if ($self->isParameterRequired($param)) {
                if (!defined $val || $val eq '') {
                    my $procedureName = $self->getProcedureName();
                    FlowPDF::Helpers::bailOut "Parameter '$param' of procedure '$procedureName' is marked as required, but it does not have a value. Aborting with fatal error.";
                }
            }
            return $val;
        }

    }
    die qq{Failed to get actual parameter $param};
}


# private
sub get_param {
    my ($self, $param) = @_;

    my $retval;
    eval {
        $retval = $self->readActualParameter($param);
        logInfo(qq{Got parameter "$param" with value "$retval"});
        1;
    } or do {
        logError("Error '$@' was occured while getting property: $param");
        $retval = undef;
    };
    return $retval;
}


# private
sub getCurrentStepParametersAsHashref {
    my ($self) = @_;

    my $params = {};

    my $procedure_name = $self->getEc()->getProperty('/myProcedure/name')->findvalue('//value')->string_value;
    my $po = $self->getPluginObject();
    my $xpath = $self->getEc()->getFormalParameters({
        projectName => sprintf('%s-%s', $po->getPluginName(), $po->getPluginVersion()),
        procedureName => $procedure_name
    });

    for my $param ($xpath->findnodes('//formalParameter')) {
        my $name = $param->findvalue('formalParameterName')->string_value;
        my $value = $self->get_param($name);

        my $name_in_list = $name;
        if ($param->findvalue('type')->string_value() eq 'credential') {

            my $cred = undef;
            # error handling for credentials:
            # TODO: Implement universal way of working with credentials
            try {
                $cred = $self->readCredential({
                    credentialName => $value,
                    parameterName => $name
                });
                # $cred = $self->getEc()->getFullCredential($value);
                # my $error_code = $cred->findvalue('//error/code')->string_value();
                # if ($error_code && $self->isParameterRequired($name)) {
                #     FlowPDF::Exception::RuntimeException->new("Can't get credential $name: $error_code")->throw();
                # }
            } catch {
                my ($e) = @_;
                if ($self->isParameterRequired($name)) {
                    if (ref $e =~ m/^FlowPDF::Exception/s) {
                        $e->throw();
                    }
                    else {
                        croak($e);
                    }
                }
                logDebug("Can't get optional credential $name: ", $e);
            };

            if (!$cred){
                next;
            }

            my $username = $cred->{userName};
            my $password = $cred->{password};
            $params->{$name_in_list}->{userName} = $username;
            $params->{$name_in_list}->{password} = $password;

            # setting mask pattern
            if ($password) {
                FlowPDF::Log->setMaskPatterns($password);
            }
        }
        else {
            $value = trim($value);
            if (!defined $value || $value eq '') {
                next;
            }
            $params->{$name_in_list} = $value;
        }
    }
    logInfo "\n";
    return $params;
}


=head2 newRESTClient($creationParams)

=head3 Description

Creates an L<FlowPDF::Client::REST> object, applying components and other useful mechanisms to it during creation.

For now, this method supports following components and tools:

=over 4

=item L<FlowPDF::Component::Proxy>

Proxy can be automatically be enabled. To do that you need to make sure that following parameters are present in your configuration:

=over 8

=item credential with the proxy_credential name.

=item regular parameter with httpProxyUrl name

=back

If your configuration has all fields above, proxy component will be applied silently,
and you can be sure, that all requests that you're doing through L<FlowPDF::Client::REST> methods already have proxy enabled.

Also, note that if you have debugLevel parameter in your configuration, and it will be set to debug,
debug mode for FlowPDF::ComponentProxy will be enabled by default.

=item Basic Authorization

Basic authorization can be automatically applied to all your rest requests. To do that you need to make sure that following parameters are present in your plugin configuration:

=over 8

=item authScheme parameter has value "basic"

=item credential with the basic_credential name

=back

=back

=head3 Parameters

=over 4

=item (Optional) (HASHREF) FlowPDF::Client::REST Object creation params.

=back

=head3 Returns

=over 4

=item L<FlowPDF::Client::REST>

=back

=head3 Usage

%%%LANG=perl%%%

    my $rest = $context->newRestClient();
    my $req = $rest->newRequest(GET => 'https://electric-cloud.com');
    my $response = $rest->doRequest($req);
    print $response->decoded_content();

%%%LANG%%%

=cut

sub newRESTClient {
    my ($context, $params) = @_;

    my $creationParams = {};
    my $configValues = undef;
    $configValues = $context->getConfigValues();
    # eval {
    #     $configValues = $context->getConfigValues();
    #     1;
    # } or do {
    #     if ($context->getHasConfigField()) {
    #         FlowPDF::Helpers::bailOut("Can't get config: $@");
    #     }
    # };
    if ($configValues) {
        # handling the proxy here
        my $proxyUrl = $configValues->getParameter(HTTP_PROXY_URL_PROPERTY);
        fwLogDebug("ProxyURL Parameter is " . Dumper $proxyUrl);
        if ($proxyUrl) {
            fwLogDebug("proxyUrl parameter has been found in configuration, using proxy ", $proxyUrl->getValue());
            # setting a proxy URL;
            $creationParams->{proxy}->{debug} = FlowPDF::Log::getLogLevel();
            $creationParams->{proxy}->{url} = $proxyUrl->getValue();

            if (my $proxyCredential = $configValues->getParameter(PROXY_CREDENTIAL_PROPERTY)) {
                $creationParams->{proxy}->{username} = $proxyCredential->getUserName();
                $creationParams->{proxy}->{password} = $proxyCredential->getSecretValue();
            }
        }
        # handling basic auth here.
        my $authScheme = $configValues->getParameter(AUTH_SCHEME_PROPERTY);
        if ($authScheme) {
            fwLogDebug("Hadling $authScheme authorization from config");
            if ($authScheme->getValue() eq AUTH_SCHEME_VALUE_FOR_BASIC_AUTH) {
                fwLogDebug("Auth scheme 'basic' has been detected");
                my $basicCred = $configValues->getParameter(BASIC_AUTH_CREDENTIAL_PROPERTY);
                if ($basicCred) {
                    my $user = $basicCred->getUserName();
                    my $password = $basicCred->getSecretValue();
                    $creationParams->{auth} = {
                        type     => 'basic',
                        username => $user || '',
                        password => $password || ''
                    };
                    fwLogDebug("Parameters for basic auth are: " . Dumper $creationParams->{auth});
                }
            }
            else {
                fwLogDebug("Auth Scheme $authScheme is not implemented yet");
            }
        }
    }
    fwLogDebug("REST client creation parameters are: ", Dumper $creationParams);

    if ($params->{oauth}) {
        $creationParams->{auth} = $params->{oauth};
        $creationParams->{auth}->{type} = 'oauth';
    }
    my $retval = FlowPDF::Client::REST->new($creationParams);
    return $retval;
}

# private
sub retrieveCurrentProjectName {
    my ($self, $jobId) = @_;

    if (!$jobId) {
        $jobId = $ENV{COMMANDER_JOBID};
    }

    my $projectName = '';
    eval {
        my $result = $self->getEc()->getJobDetails($jobId);
        $projectName = $result->findvalue('//job/projectName')->string_value();
        1;
    } or do {
        FlowPDF::Helpers::bailOut "Can't retrieve project for job $jobId";
    };

    return $projectName;
}

# private
sub isCheckConnectionInCreateConfigurationContext {
    my ($self) = @_;

    my $procedureName = $self->getProcedureName();
    my $stepName = $self->getStepName();
    if ($stepName eq 'checkConnection' && ($procedureName eq 'CreateConfiguration' || $procedureName eq 'TestConfiguration') ) {
        return 1;
    }
    return 0;
}

# private
sub populateDefaultConfigValues {
    my ($self, $hashref) = @_;

    my $defaultConfigValues = $self->getPluginObject()->getDefaultConfigValues();
    if (!$defaultConfigValues) {
        $defaultConfigValues = {};
    }

    for my $cv (keys %$defaultConfigValues) {
        if (defined $hashref->{$cv}) {
            logWarning("The config field '$cv' that was set to default value in plugin code is present in configuration. Default value from plugin code is ignored.");
            next;
        }
        if ($cv =~ m/_credential$/s) {
            if (!defined $defaultConfigValues->{$cv}->{userName} && !defined $defaultConfigValues->{$cv}->{password}) {
                logWarning("Missing userName and password for the default credential");
                next;
            }
        }
        $hashref->{$cv} = $defaultConfigValues->{$cv};
    }

    return $self;
}


# private
sub getCredentialFromConfig {
    # This function accepts the credential parameter as XPath node and returns the values for the cred.
    my ($self, $configName, $parameterNode) = @_;

    # in this function we're doing a trick. We know, that credential in our config will be always named credential or
    # %keyword%_credential. Resulting credential is being stored by plugins as %cofingName%_%credentialField%.
    # So, for example, let's say that we have a config named config1, so,
    # credential, as exception, will be stored as config1, proxy_credential field will be stored as config1_proxy_credential
    # following logic implements this concept.

    # TODO: Implement better validation of parameterNode
    unless (ref $parameterNode) {
        FlowPDF::Exception::RuntimeException->new(
            "Expected XML::XPath::node reference for getCredentialFromConfig"
        )->throw();
    }

    my $value = $parameterNode->findvalue('value')->string_value();
    my $name = $parameterNode->findvalue('propertyName')->string_value();

    my $whatToGet = $configName;
    if ($value =~ m|^\/|s) {
        $whatToGet = $value;
    }
    elsif ($name =~ m/(.*?_credential)$/m) {
        $whatToGet = $configName . '_' . $1;
    }
    return $self->readCredential({credentialName => $whatToGet});
}

# private
sub readCredential {
    my ($self, $opts) = @_;

    if (!ref $opts || ref $opts ne 'HASH') {
        FlowPDF::Exception::RuntimeException->new("Opts are expected to be the hash reference.")->throw();
    }
    my $credentialName = $opts->{credentialName} if exists $opts->{credentialName};
    my $parameterName = $opts->{parameterName} if exists $opts->{parameterName};

    if (!defined $credentialName) {
        FlowPDF::Exception::RuntimeException->new(
            "credentialName doesn't exist in the function call parameters."
        )->throw();
    }
    my $credentialObject = $self->getEc()->getFullCredential($credentialName);

    my $errorCode = $credentialObject->findvalue('//error/code')->string_value();
    if ($errorCode && defined $parameterName && $self->isParameterRequired($parameterName)) {
        my $whatToShow = $parameterName || $credentialName;
        FlowPDF::Exception::RuntimeException->new("Can't get credential $whatToShow: $errorCode")->throw();
    }
    my $userName = $credentialObject->findvalue('//userName')->string_value();
    my $password = $credentialObject->findvalue('//password')->string_value();

    my $rv = {
        userName => $userName,
        password => $password
    };

    return $rv;
}

# private
sub isCredential {
    my ($self, $node) = @_;

    my $name = $node->findvalue('propertyName')->string_value;

    if ($name =~ m/_?credential$/s) {
        return 1;
    }
    return 0;
}

# private
# handling of the new pluginConfiguration object
sub _readPluginConfiguration {
    my ($self, $configName, $po, $pluginProjectName) = @_;

    my (undef, undef, $configProjectName, undef, $cfgName) = split(/\// => $configName);

    my $cfg;
    eval {
        $cfg = $self->getEc()->getPluginConfiguration({
            projectName => $configProjectName,
            pluginConfigurationName => $cfgName
        });
        1
    } or do {
        my $err = $@;
        if ($err =~ /NoSuchPluginConfiguration/) {
            FlowPDF::Exception::UnexpectedEmptyValue->new("Plugin configuration '$cfgName' does not exist in the project '$configProjectName'")->throw();
        }
        else {
            die $err;
        }
    };

    # todo procedure does not exist
    my $formalParameters = $self->getEc()->getFormalParameters({
        projectName => $pluginProjectName,
        procedureName => 'ConfigurationParametersHolder'
    });

    my $fields = {};
    for my $f ($cfg->findnodes('//fields/parameterDetail')) {
        my $name = $f->findvalue('parameterName')->string_value();
        my $value = $f->findvalue('parameterValue')->string_value();
        if ($value =~ /\$\[/) {
            $value = $self->getEc()->expandString({value => $value, jobId => $ENV{COMMANDER_JOBID}})->findvalue('//value')->string_value();
        }
        $fields->{$name} = $value;
    }

    my $credentials = {};
    for my $cred ($cfg->findnodes('//credentialMappings/parameterDetail')) {
        my $name = $cred->findvalue('parameterName')->string_value();
        my $value = $cred->findvalue('parameterValue')->string_value();
        $credentials->{$name} = $value;
    }

    for my $formalParameter ($formalParameters->findnodes('//formalParameter')) {
        my $name = $formalParameter->findvalue('formalParameterName')->string_value();
        my $type = $formalParameter->findvalue('type')->string_value();
        my $required = $formalParameter->findvalue('required')->string_value();

        if ($type eq 'credential') {
            my $path = $credentials->{$name};
            unless($path) {
                if ($required eq '1' || $required eq 'true') {
                    FlowPDF::Exception::UnexpectedEmptyValue->new("Credential $name is required but not provided by the configuration")->throw();
                }
                else {
                    next;
                }
            }
            my $cred = $self->getEc()->getFullCredential($path);
            $fields->{$name} = {
                userName => $cred->findvalue('//userName')->string_value(),
                password => $cred->findvalue('//password')->string_value()
            };

            if ($fields->{$name}->{password}) {
                FlowPDF::Log->setMaskPatterns($fields->{$name}->{password});
            }
        }

        if ($required eq '1' || $required eq 'true') {
            unless($fields->{$name}) {
                FlowPDF::Exception::UnexpectedEmptyValue->new("Parameter $name is required but not provided by the configuration")->throw();
            }
        }
    }

    return $fields;
}

1;

package WebSphere::WebSphere;

=head1 NAME

C<WebSphere::WebSphere> - WebSphere configuration routines

=head1 DESCRIPTION

Wrapper for wsadmin utility.

=head1 COPYRIGHT

Copyright (c) 2016 Electric Cloud, Inc.
All rights reserved

=cut

use strict;
no strict "subs";
use ElectricCommander;
use ElectricCommander::PropDB;
use JSON;
use Carp;
use Data::Dumper;
use WebSphere::Util;
use File::Copy qw/cp/;

$| = 1;

{
    my $tec = ElectricCommander->new();
    my $log_property_path;
    eval {
        $log_property_path = $tec->getProperty('/plugins/EC-WebSphere/project/ec_debug_logToProperty')->findvalue('//value')->string_value();
    };
    if ($log_property_path) {
        ElectricCommander::PropMod::loadPerlCodeFromProperty($tec,"/myProject/modules/WebSphere/LogTrapper.pm");

        WebSphere::LogTrapper::open_handle();
        tie *STDOUT, "WebSphere::LogTrapper", (
            print =>
                sub {
                    my $value = '';
                    eval {
                        $value = $tec->getProperty($log_property_path)->findvalue('//value')->string_value();
                    };
                    $value .= join '', @_;
                    $tec->setProperty($log_property_path, $value);
                    print @_;
                    # print map { uc } @_;
                }
            );
    }

};

=head1 METHODS

=head2 C<new>

Constructs a new L<WebSphere::WebSphere> object.

=over

2>&1

=item C<$ec>

Reference to L<ElectricCommander> object.

=item C<$configuration>

String with name of plugin configuration

=item C<$wsadminPath>

Absolute path to wsadmin utility

=back

Returns new instance of L<WebSphere::WebSphere> object.

=cut

sub new {
    my ($class, $ec, $configurationName, $wsadminPath, $opts) = @_;

    my $self = {
        ec => $ec,
        # wsadminPath => $wsadminPath
    };
    bless $self, $class;
    my $log = EC::Plugin::Logger->new(0);
    $self->{_log} = $log;

    # Getting step parameters
    my $step_params = $self->get_step_parameters();
    if (!$configurationName) {                             # this is for compatibility with previous procedures.
        $configurationName = $step_params->{configname} || $step_params->{configName};
    }
    my $configuration = $self->_getConfiguration($configurationName);
    unless ($configuration) {
        $self->bail_out("Configuration '$configurationName' doesn't exist");
    }
    my $debug = $configuration->{debug};
    if ($debug) {
        $self->{_log}->{level} = $debug;
    }
    $wsadminPath ||= $configuration->{wsadminabspath};
    if (!$opts->{disabled_wsadmin_check}) {
        unless ($wsadminPath) {
            $self->bail_out("Missing wsadmin absolute path parameter. Please, check your configuration: $configurationName");
        }

        if (-e $wsadminPath && -d $wsadminPath) {
            $self->bail_out("wsadmin script exists at $wsadminPath, but it is a directory. Absolute path to wsadmin script expected. Please, check your environment");
        }
        elsif (!-e $wsadminPath) {
            my $context = $self->getRunContext();
            my $msg = sprintf(
                'wsadmin script was not found at %s: file does not exist.
Please, make sure that this path is correct and %s is running on proper resource.
Current resource is %s',
                $wsadminPath, $context, $self->get_current_resource_name()
            );
            $self->bail_out($msg);
        }
    }
    $self->{wsadminPath} = $wsadminPath;

    if (!$configuration) {
        exit 1;
    }

    if (!defined $debug) {
        $debug = 0;
    }


    $self->{configuration} = $configuration;
    return $configuration ? $self : undef;
}


# while we usually using hashrefs for key-value pairs, here we should use an arrayref
# because since perl 5.18 keys order in hash is not defined. Currently we have 5.8.8 or 5.8.9
# in these perl versions key order is always the same.
# But after upgrade this behaviour could case a bunch of issues that are very hard to debug.
# Arguments structure will be: [[key, ' ' ,value], [key, ' ', value], [key, ' ', value]]
sub gen_command_line {
    my ($self, $shell, $arguments, $params) = @_;

    # params parameter is currently not in use, but it is reserver for future usage.
    my $retval = '';
    if ($shell) {
        $retval .= $self->quote_cmd_arg($shell);
    }
    for my $arg (@$arguments) {
        if (scalar(@$arg) != 3) {
            croak "Expected 3 values in arguments item\n";
        }
        my ($value, $item);
        if (!$arg->[1] && !$arg->[2]) {
            # it is just a flag
            $item = qq| $arg->[0] |;
        }
        else {
            $value = $self->quote_cmd_arg($arg->[2]);
            $item = qq| $arg->[0]$arg->[1]$value |;
        }

        $retval .= $item;
    }

    return $retval;
}


sub quote_cmd_arg {
    my ($self, $string) = @_;

    if (!defined $string) {
        croak "Nothing to quote\n";
    }
    if ($self->is_win()) {
        # current OS is windows, so we can't use single quotes.
        # we have to use double quotes.
        $string =~ s|"|\\"|gs;
        $string = qq|"$string"|;
    }
    else {
        $string =~ s|'|\\'|;
        $string = qq|'$string'|;
    }
    return $string;
}


# step shell is a, basically, shell, which executes logic.
# In websphere we have mostly 2 kinds of procedures.
# 1. procedures that are based on wsadmin script
# 2. procedures that are based on start/stop scripts, like startNode.sh or stopNode.sh
# Example of params args that could be used for this function as input:
# my $params = {
#     jython_script => {
#         path => '/propety/path',
#         template => {one => 'two', three => 'four'}
#     },
#     args_cb => sub {
#         my ($self) = @_;
#         return [['-one', ' ', 'two']];
#     },
#     success_cb => sub {
#         my ($self) = @_;
#     },
#     error_cb => sub {
#         my ($self) = @_;
#     },
#     procedure_result_cb => sub {
#         my ($self) = @_;
#     }
# };

sub run_step {
    my ($self, $step_shell, $params) = @_;

    # 1. Get and check shell.
    if (!$step_shell) {
        die "Shell is mandatory\n";
    }
    if (! -e $step_shell) {
        die "Shell $step_shell does not exist\n";
    }
    # 2. Get step parameters
    # TODO: Improve get_step_parameters to get credentials from params
    my $step_params = $self->get_step_parameters();
    # $self->{step_params} = $step_params;
    # 3. Get config values. The could be accessed by get_config_values
    # 4. Set template properties.
    my $cmd_line;
    if ($params->{jython_script}) {
        if (!$params->{jython_script}->{path}) {
            die "Missing jythons script path\n";
        }
        my $file = $params->{jython_script}->{path};
        my $script = $self->ec()->getProperty("/myProject/wsadmin_scripts/$file")->getNodeText('//value');
        my $logger = $self->log();
        $logger->debug("WSAdmin script:");
        $logger->debug('' . $script);
        $logger->debug("== End of WSAdmin script ==");
        $file = $self->write_jython_script(
            $file, {},
            augment_filename_with_random_numbers => 1
        );


        $cmd_line = $self->_create_runfile($file, ());
        # TODO: add template logic when required.
        # work with $params->{jython_script}->{template};
    }
    else {
        # 5. build system command
        my $command_args = [];
        if ($params->{args_cb} && ref $params->{args_cb}) {
            $command_args = $params->{args_cb}->($self, $params);
        }
        $cmd_line = $self->gen_command_line($step_shell, $command_args);
    }
    my $safe_cmd_line = $self->_mask_password($cmd_line);
    $self->log()->info("Generated command line: $safe_cmd_line");
    # 6. Execute command line.
    # TODO: Replace it with syscall
    my $cmd_res = `$cmd_line 2>&1`;
    $self->{cmd_res} = $cmd_res;
    $self->log()->info("Execution result:\n$cmd_res");
    # 7. Work with exit code.
    my $code = $?;
    $code = $code >> 8;

    # Here goes logic for windows issues when non-wsadmin scripts can't forward exit code.
    if ($self->is_win()) {
        if (!$code && $self->looks_like_error($self->cmd_res())) {
            $self->log()->debug("Detected an error on windows. Changing code to 1.");
            $code = 1;
        }
    }
    # End of windows spike
    # Exit code is 0
    # 8. Handle output status
    $self->{procedure_result} = {
        outcome => {
            target => $params->{target}->{outcome} || 'myCall',
            result => '',
        },
        procedure => {
            target => $params->{target}->{procedure} || 'myCall',
            msg => ''
        },
        pipeline => {
            target => $params->{target}->{pipeline} || 'Step Execution Result:',
            msg => '',
        }
    };

    my $parsed_procedure_result = $self->parseProcedureLog($self->cmd_res());
    # set properties if any
    for my $prop (@{$parsed_procedure_result->{properties}}) {
        # TODO: Add error handling if property was not set for some reason.
        $self->ec()->setProperty($prop->{path}, $prop->{value});
    }
    my $success = 1;
    if ($code) {
        $success = 0;
    }
    if ($parsed_procedure_result->{outcome}) {
        if ($parsed_procedure_result->{outcome} =~ m/^(?:success|warning)$/s) {
            $success = 1;
        }
        else {
            $success = 0;
        }
    }

    $self->{parsed_procedure_result} = $parsed_procedure_result;
    $self->log()->debug("Success value: $success");
    $self->log()->debug("Parsed procedure result:", Dumper $parsed_procedure_result);
    # step execution has been successful
    if ($success) {
        $self->{procedure_result}->{outcome}->{result} = 'success';
        if ($params->{success_cb} && $params->{success_cb} eq 'CODE') {
            $params->{success_cb}->($self);
        }
        else {
            # If no summary, default message that has been provided will be used.
            my $ppr = $parsed_procedure_result;
            $self->{procedure_result}->{procedure}->{msg} = $params->{target}->{success_summary};
            my $pmsg = \$self->{procedure_result}->{procedure}->{msg};
            if (@{$ppr->{summary}} or @{$ppr->{success_summary}}) {
                $$pmsg = '';
                for my $l (@{$ppr->{success_summary}}, @{$ppr->{summary}}) {
                    $$pmsg .= $l . "\n";
                }
            }
            if (@{$ppr->{warning}}) {
                if ($$pmsg !~ m/\s$/) {
                    $$pmsg .= "\n";
                }
                $self->{procedure_result}->{outcome}->{result} = 'warning';
                for my $l (@{$ppr->{warning}}) {
                    $$pmsg .= sprintf('WARNING: %s%s', $l, "\n");
                }
            }
            if ($params->{success_cb2} && ref $params->{success_cb2} eq 'CODE') {
                $params->{success_cb2}->($self);
            }
            $self->{procedure_result}->{pipeline}->{msg} = $self->{procedure_result}->{procedure}->{msg};
        }
    }
    # Now we're handling errors
    else {
        $self->{procedure_result}->{outcome}->{result} = 'error';
        if ($params->{error_cb} && $params->{error_cb} eq 'CODE') {
            $params->{error_cb}->($self);
        }
        else {
            my $ppr = $parsed_procedure_result;
            $self->{procedure_result}->{procedure}->{msg} = $params->{target}->{error_summary};
            my $pmsg = \$self->{procedure_result}->{procedure}->{msg};
            if (@{$ppr->{summary}} or @{$ppr->{failure_summary}}) {
                $$pmsg = '';
                for my $l (@{$ppr->{failure_summary}}, @{$ppr->{summary}}) {
                    $$pmsg .= $l . "\n";
                }
            }
            if (@{$ppr->{exception}}) {
                if ($$pmsg !~ m/\s$/) {
                    $$pmsg .= "\n";
                }
                for my $l (@{$ppr->{exception}}) {
                    $$pmsg .= sprintf('Exception: %s%s', $l, "\n");
                }
            }
            if (@{$ppr->{error}}) {
                if ($$pmsg !~ m/\s$/) {
                    $$pmsg .= "\n";
                }
                for my $l (@{$ppr->{error}}) {
                    $$pmsg .= sprintf('Error: %s%s', $l, "\n");
                }
            }
            if ($params->{error_cb2} && ref $params->{error_cb2} eq 'CODE') {
                $params->{error_cb2}->($self);
            }
            $self->{procedure_result}->{pipeline}->{msg} = $self->{procedure_result}->{procedure}->{msg};
        }
    }
    # Execute callback on these parameters.
    $self->{procedure_result}->{pipeline} = [$self->{procedure_result}->{pipeline}];
    if ($params->{procedure_result_cb} && ref $params->{procedure_result_cb} eq 'CODE') {
        $params->{procedure_result_cb}->($self);
    }
    my $exit = $self->setResult(%{$self->{procedure_result}});

    $exit->();
}

sub attachLogFile {
    my ($self, $jobStepId, $file) = @_;

    my $retval = undef;
    my $text = $file;
    $file = $ENV{COMMANDER_WORKSPACE} . '/' . $file;
    if (!-e $file) {
        $self->log()->info("File $file does not exist");
        return $retval;
    }
    if (!-f $file) {
        $self->log()->info("File $file is not a regular file");
        return $retval;
    }
    $self->ec()->setProperty('/myJob/artifactsDirectory', '');
    my $propertyPath = sprintf('jobSteps/%s/%s/', $jobStepId, $text);
    $self->ec()->setProperty(
        '/myJob/report-urls/' . $text,
        $propertyPath
    );
    $retval = $self->getLinkForReport4Pipeline('/commander/' . $propertyPath, $text);

    return $retval;
}


sub looks_like_error {
    my ($self, $log) = @_;

    if ($log =~ m/[\w\d]{8}E:/ms) {
        return 1;
    }
    return 0;
}

sub getLinkForReport4Pipeline {
    my ($self, $address, $text) = @_;

    my $html = q|<html><body><a target = "_BLANK" href="%s">%s</a></body></html>|;
    return sprintf($html, $address, $text);
}

sub gen_initial_args {
    my ($self, $opts) = @_;

    my $args = [];
    my $cfg = $self->config_values();
    if (!$opts->{nopasswd} && $cfg->{user}) {
        push @$args, ['-user', ' ', $cfg->{user}]
    }
    if (!$opts->{nopasswd} && $cfg->{password}) {
        push @$args, ['-password', ' ', $cfg->{password}];
    }
    if ($opts->{trace}) {
        if ($cfg->{debug} >= 2) {
            push @$args, ['-trace', '', ''];
        }
    }
    unless ($opts->{noconntype}) {
        if ($cfg->{conntype}) {
            push @$args, ['-conntype', ' ', $cfg->{conntype}];
        }
    }
    return $args;
}


sub config_values {
    my ($self) = @_;

    return $self->{configuration};
}


sub step_params {
    my ($self) = @_;

    if (!$self->{step_params}) {
        $self->{step_params} = $self->get_step_parameters();
    }
    return $self->{step_params};
}


sub cmd_res {
    my ($self) = @_;

    return $self->{cmd_res};
}


sub procedure_result {
    my ($self) = @_;

    return $self->{procedure_result};
}


sub get_param {
    my ($self, $param) = @_;

    my $ec = $self->ec();
    my $retval;
    eval {
        $retval = $ec->getProperty($param)->findvalue('//value') . '';
        1;
    } or do {
        $self->log()->debug("Error '$@' was occured while getting property: $param");
        $retval = undef;
    };
    return $retval;
}

sub get_current_resource_name {
    my ($self) = @_;

    my $ec = $self->ec();
    my $resource_name = $ec->getProperty('/myResource/name')->findvalue('//value')->string_value();
    return $resource_name;
}


sub get_step_parameters {
    my ($self) = @_;

    if ($self->{step_params} && %{$self->{step_params}}) {
        return $self->{step_params};
    }
    my $params = {};
    my $procedure_name = $self->ec->getProperty('/myProcedure/name')->findvalue('//value')->string_value;
    my $xpath = $self->ec->getFormalParameters({projectName => '@PLUGIN_NAME@', procedureName => $procedure_name});
    for my $param ($xpath->findnodes('//formalParameter')) {
        my $name = $param->findvalue('formalParameterName')->string_value;
        my $value = $self->get_param($name);

        my $name_in_list = $name;
        # $name_in_list =~ s/ecp_websphere_//;
        if ($param->findvalue('type')->string_value eq 'credential') {
            my $cred = $self->ec->getFullCredential($value);
            my $username = $cred->findvalue('//userName')->string_value;
            my $password = $cred->findvalue('//password')->string_value;

            $params->{$name_in_list . 'Username'} = $username;
            $params->{$name_in_list . 'Password'} = $password;
        }
        else {
            $params->{$name_in_list} = $value;
            $self->log()->info(qq{Got parameter "$name" with value "$value"\n});
        }
    }
    $self->{step_params} = $params;
    return $params;
}

# sub escape_string {
#     my ($self, $string) = @_;

#     if ($self->is_win()) {
#         return $self->_escape_string_win32($string);
#     }
#     return $self->_escape_string_regular($string);
# }

# sub _escape_string_win32 {
#     my ($self, $string) = @_;

#     # TODO: Add escaping logic here
#     return $string;
# }

# sub _escape_string_regular {
#     my ($self, $string) = @_;

#     # TODO: Add escaping logic here
#     return $string;
# }

sub is_win {
    if ($^O eq 'MSWin32') {
        return 1;
    }
    return 0;
}


sub setTemplateProperties {
    my ($self, %props) = @_;

    my $tp = $self->{_template_path} || '/myJobStep/tmpl';

    for my $k (keys %props) {
        $self->ec()->setProperty($tp . '/' . $k => $props{$k});
    }
    return 1;
}
sub getWSAdminPath {
    my ($self) = @_;

    return $self->{wsadminPath};
}

sub extractWebSphereExceptions {
    my ($self, $text) = @_;

    # my @res = $text =~ m/[A-Z]{4}[\d]{4}E:\s(.*?)\n\n/gms;
    my @res = grep {$_} split '([A-Z]{4}[\d]{4}E:\s)', $text;
    if (wantarray()) {
        return @res;
    }
    return join "\n", @res;
}


sub extractMagicValuesFromProcedureLog {
    my ($self, $log) = @_;

    my $retval = {
        info            => [],
        warning         => [],
        error           => [],
        summary         => [],
        exception       => [],
        outcome         => [],
        forward         => [],
        properties      => [],
        success_summary => [],
        failure_summary => []
    };
    # extract properties
    while ($log =~ /\[OUT\]\[SETPROPERTY\]\[NAME\](.+?)\[NAME\]\[VALUE\](.+?)\[VALUE\]\[SETPROPERTY\]\[OUT\]/gms) {
        if ($1 && $2) {
            push @{$retval->{properties}}, {path => $1, value => $2};
        }
    }
    # extract error
    for my $t (qw/WARNING ERROR INFO SUMMARY EXCEPTION OUTCOME FORWARD SUCCESS_SUMMARY FAILURE_SUMMARY/) {
        while ($log =~ m|\[OUT\]\[$t\]:\s(.*?)\s:\[$t\]\[OUT\]|gms) {
            push @{$retval->{lc($t)}}, $1;
        }
    }
    if (@{$retval->{exception}}) {
        # my @res = $text =~ m/[A-Z]{4}[\d]{4}E:\s(.*?)\n\n/gms;
        @{$retval->{exception}} = map {
            s/.*?([A-Z]{4}[\d]{4}E:\s*)/\1/ms;
            $_;
        } @{$retval->{exception}};
    }
    if (@{$retval->{outcome}}) {
        $retval->{outcome} = $retval->{outcome}->[-1];
    }
    else {
        $retval->{outcome} = '';
    }
    return $retval;
}


sub parseProcedureLog {
    my ($self, $log) = @_;

    my $retval = {};
    # 1. Extracting "magic" values from log:
    $retval = $self->extractMagicValuesFromProcedureLog($log);

    return $retval;
}


sub extractLogPath {
    my ($self, $text) = @_;

    my $file = '';
    if ($text =~ m/being\slogged\sin\sfile\s+(.+?)$/ms) {
        $file = $1;
    }
    return $file;
}

# sub attachLogFile {
#     my ($self) = @_;
# }


sub getParamsRenderer {
    my ($self, %params) = @_;

    my $r = WebSphere::Params->new(%params);
    return $r;
}


sub ec {
    my ($self) = @_;

    return $self->{ec};
}


sub log {
    my ($self) = @_;

    unless ($self->{_log}) {
        $self->bail_out("Logger is uninitialized");
    }
    return $self->{_log};
}


sub getRunContext {
    my ($self) = @_;

    my $ec = $self->ec();
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
        $scheduleName = $self->getScheduleName();
        1;
    } or do {
        print "error occured: $@\n";
    };

    if ($scheduleName) {
        return $context;
    }
    $context = 'procedure';
    return $context;
}

sub getProjectName {
    my ($self, $jobId) = @_;

    $jobId ||= $ENV{COMMANDER_JOBID};

    my $projectName = '';
    eval {
        my $result = $self->{ec}->getJobDetails($jobId);
        $projectName = $result->findvalue('//job/projectName')->string_value();
        # $projectName = $result->findvalue('//projectName')->string_value();
        1;
    } or do {
        print "error occured: $@\n";
    };

    return $projectName;
}

sub getScheduleName {
    my ($self, $jobId) = @_;

    $jobId ||= $ENV{COMMANDER_JOBID};

    my $scheduleName = '';
    eval {
        my $result = $self->{ec}->getJobDetails($jobId);
        $scheduleName = $result->findvalue('//scheduleName')->string_value();
        if ($scheduleName) {
            $self->{logger}->info('Schedule found: ', $scheduleName);
        };
        1;
    } or do {
        $self->{logger}->error($@);
    };

    return $scheduleName;
}

# this function is works like setSummary from other plugins, but it also sets props for pipelines.
# pipeline and procedure should be a hash reference or reference to array with hash references.
sub setResult {
    my ($self, %params) = @_;

    my ($outcome, $procedure, $pipeline) = ($params{outcome}, $params{procedure}, $params{pipeline});
    if (!ref $outcome) {
        croak("HASH or ARRAY reference is expected as outcome parameter");
    }
    $outcome = [$outcome] if ref $outcome eq 'HASH';
    if (!ref $procedure) {
        croak("HASH or ARRAY reference expected as procedure parameter");
    }
    $procedure = [$procedure] if ref $procedure eq 'HASH';
    if (!ref $pipeline) {
        croak("pipeline parameter should be a HASH or ARRAY reference");
    }

    # if pipeline has been passed as hash reference, we will create array reference to iterate through it.
    $pipeline = [$pipeline] if ref $pipeline eq 'HASH';

    my $ec = $self->ec();

    # time to get context.
    my $context = $self->getRunContext();
    # setting outcome
    my $exit_code = 0;
    for my $o (@$outcome) {
        if ($o->{result} !~ m/^(?:error|success|warning)$/s) {
            croak("Expected error, success or warning, got: $o->{result}");
        }
        if ($o->{target} !~ m/^(?:myJobStep|myJob|myCall)$/s) {
            croak("target should one of: myJobStep, myJob, myCall. Got: $o->{target}");
        }
        my $pp = '/' . $o->{target} . '/outcome';
        if ($o->{result} eq 'error') {
            $exit_code = 1;
        }
        $self->log()->debug("Setting outcome property: '$pp' => '$o->{result}'");
        $ec->setProperty($pp => $o->{result});
    }
    # pipeline
    if ($context eq 'pipeline') {
        for my $p (@$pipeline) {
            if (!$p->{target} || !exists $p->{msg}) {
                croak("target and msg are mandatory");
            }
            my $pp = '/myPipelineStageRuntime/ec_summary/' . $p->{target};
            $self->log()->debug("Setting pipeline property: '$pp' => '$p->{msg}'");
            $ec->setProperty($pp =>  $p->{msg});
        }
    }
    # procedure, schedule and application process are also procedure context.
    # else {
    for my $p (@$procedure) {
        if (!$p->{target} || !exists $p->{msg}) {
            croak("target and msg are mandatory");
        }
        if ($p->{target} !~ m/^(?:myJobStep|myJob|myCall)$/s) {
            croak("target should one of: myJobStep, myJob, myCall. Got: $p->{target}");
        }
        my $pp = '/' . $p->{target} . '/summary';
        $self->log()->debug("Setting procedure property: '$pp' => '$p->{msg}'");
        $ec->setProperty($pp => $p->{msg});
    }
    #}
    return sub {
        # print "Will execute exit $exit_code\n";
        $self->log()->debug("setResult function will execute exit $exit_code");
        exit $exit_code;
    };
}


sub parseScopeToObject {
    my ($self, $scope) = @_;

    my $split_regex = ',';
    my $split_regex2 = '=';

    if ($scope =~ m|^/.*?/$|) {
        $split_regex = '/';
        $split_regex2 = ':';
    }

    my @elems = split $split_regex, $scope;
    @elems = grep {$_ ne ''} @elems;
    my $retval = {};
    for my $elem (@elems) {
        rtrim($elem);
        my @kv = split $split_regex2, $elem;
        rtrim($kv[0]);
        rtrim($kv[1]);

        $retval->{$kv[0]} = $kv[1];
        # $retval .= "$kv[0]:$kv[1]/";
    }
    return $retval;
}


sub generatePythonStructure {
    my ($self, $struct) = @_;

    my $retval = '';
    if (!ref $struct) {
        return $struct;
    }
    elsif (ref $struct eq 'ARRAY') {
        $retval = generatePythonList($struct);
    }
    elsif (ref $struct eq 'HASH') {
        $retval = generatePythonDict($struct);
    }

}

sub generatePythonList {
    my ($arrayref) = @_;

    my $retval = '';
    for my $elem (@$arrayref) {
        if (ref $elem) {
            if (ref $elem eq 'ARRAY') {
                $elem = generatePythonList($elem);
            }
            elsif (ref $elem eq 'HASH') {
                $elem = generatePythonDict($elem);
            }
        }
        $retval .= qq|$elem,|;
    }
    $retval =~ s/,$//gs;
    return '[' . $retval . ']';
}


sub generatePythonDict {
    my ($hashref) = @_;

    my $retval = '';
    for my $k (keys %$hashref) {
        my $v = $hashref->{$k};
        if (ref $v) {
            if (ref $v eq 'ARRAY') {
                $v = generatePythonList($v);
            }
            elsif (ref $v eq 'HASH') {
                $v = generatePythonDict($v);
            }
        }
        else {
            $v = qq|'$v'|;
        }
        $retval .= qq|'$k':$v,|;
    }
    $retval =~ s/,$//gs;
    return '{' . $retval . '}';
}


sub parseScope {
    my ($self, $scope) = @_;

    if ($scope =~ m|^/.*?/$|) {
        return $scope;
    }

    my @elems = split ',', $scope;

    my $retval = '/';
    for my $elem (@elems) {
        WebSphere::Util::rtrim($elem);
        my @kv = split '=', $elem;
        WebSphere::Util::rtrim($kv[0]);
        WebSphere::Util::rtrim($kv[1]);

        $retval .= "$kv[0]:$kv[1]/";
    }
    return $retval;
}



=head2 C<new>

Execute python script in wsadmin, and parse output from command.
Python script must output data in json.

=over

=item C<$file>

String with path to python script

=back

Returns hash containing two hashes:
 messages - hash of wsadmin messages, { messageid, description }
 json - json output of script

=cut

sub wsadmin {
    my ( $self, $file ) = @_;
    my $shellcmd = $self->_create_runfile($file);

    print 'Run command: ' . $self->_mask_password($shellcmd) . "\n";
    return $self->_parse_wsadmin_output(`$shellcmd 2>&1`);
}

sub _parse_wsadmin_output {
    my ( $self, @output ) = @_;

    my @messages = ();
    my $json     = undef;

    print "Command output:\n" . join( "", @output ) . "\n";

    for my $line (@output) {
        if ( $line =~ /^(WAS.*?): (.*)$/ ) {
            push( @messages, { $1 => $2 } );
        }
        elsif ( $line =~ /^\{.*\}$/ ) {
            $json = decode_json($line);
        }
    }

    return {
        messages => \@messages,
        json     => $json
    };
}

sub _mask_password {
    my ( $self, $command ) = @_;

    $command =~ s/-password (.)$self->{configuration}->{password}(.)/-password $1*****$2/g;
    return $command;
}

sub _create_runfile {
    my ( $self, $runfile, @args ) = @_;
    my $configuration = $self->{configuration};

    my $options = { lang => 'jython' };

    if ( $configuration->{conntype} ) {
        $options->{conntype} = $configuration->{conntype};
    }

    if ( $configuration->{websphere_url} && $options->{conntype} eq 'IPC' ) {
        $options->{ipchost} = $configuration->{websphere_url};
    }
    else {
        $options->{host} = $configuration->{websphere_url};
    }

    if ( $configuration->{websphere_port} ) {
        $options->{port} = $configuration->{websphere_port};
    }

    if ( $configuration->{user} ) {
        $options->{user} = $configuration->{user};
    }

    if ( $configuration->{password} ) {
        if ( $^O eq 'MSWin32' ) {
            $options->{password} = "\"$configuration->{password}\"";
        }
        else {
            $options->{password} = "'$configuration->{password}'";
        }
    }

    my $options_string = "";
    for my $optionName ( keys %$options ) {
        $options_string .= " -$optionName $options->{$optionName}";
    }

    if(length(@args) > 0) {
        $options_string .= " ".join(" ", @args);
    }

    return qq{"$self->{wsadminPath}"$options_string-f "$runfile"};
}

sub _getConfiguration {
    my ( $self, $configurationName ) = @_;
    my $ec = $self->{ec};

    my $configurations =
      new ElectricCommander::PropDB( $ec, '/myProject/websphere_cfgs' );
    my %configuration = $configurations->getRow($configurationName);

    # Check if configuration exists
    unless ( keys(%configuration) ) {
        my $error_string = "Configuration '$configurationName' doesn't exist";
        print "Error: $error_string\n";
        $self->setSummary("$error_string");
        return undef;
    }

    # Get user/password out of credential
    my $xpath = $ec->getFullCredential( $configuration{credential} );
    $configuration{user}              = $xpath->findvalue("//userName");
    $configuration{password}          = $xpath->findvalue("//password");
    $configuration{configurationName} = $configurationName;

    return \%configuration;
}

=head2 write_jython_script

Retrieves python script from properties and writes it into the specified $filename.

    $websphere->write_jython_script(
        'path_to_file.py', {},
        augment_filename_with_random_numbers => 1
    );

If augment_filename_with_random_numbers was provided, method will augment filename by random numbers sequence.
For example:

test.py => test_524808719411718.py

=cut

sub write_jython_script {
    my ( $self, $filename, $tmpl_params, %opts ) = @_;

    my $ec = $self->{ec};
    my $script;
    if ($opts{script}) {
        $script = $opts{script};
    }
    else {
        $script = $ec->getProperty("/myProject/wsadmin_scripts/$filename")->getNodeText('//value');
    }

    unless ($script) {
        die "No script content found in /myProject/wsadmin_scripts/$filename property and was not provided in parameters.";
    }

    if ($opts{augment_filename_with_random_numbers}) {
        my $rnd = gen_random_numbers( 42 );
        $rnd = '_' . $rnd;
        $filename =~ s/(\.[\w]+?)$/$rnd$1/s;
    }
    open ( my $fh, '>', $filename ) or die "Cannot open file $filename: $!";
    print $fh $script;
    close $fh;

    return $filename;
}

sub new_simple {
    my ( $class, $ec ) = @_;

    if ( !$ec ) {
        $ec = ElectricCommander->new();
    }
    my $self = {
        ec => $ec
    };
    bless $self, $class;
    return $self;
}

=head2

STATIC METHOD

Returns random numbers sequence by modifier.

=cut

sub gen_random_numbers {
    my ($mod) = @_;

    my $rand = rand($mod);
    $rand =~ s/\.//s;
    return $rand;
}

=head2

Sets summary property to provided one.

    $ws->setSummary("Error occured");

=cut

sub setSummary {
    my ($self, @msg) = @_;

    my $msg = join '', @msg;
    return unless $msg;
    $self->{ec}->setProperty('/myCall/summary', $msg);
    return 1;
}


sub bail_out {
    my ($self, @msg) = @_;

    my $msg = join('', @msg);

    my $result_params = {
        outcome => {
            target => 'myCall',
            result => 'error',
        },
        procedure => {
            target => 'myCall',
            msg => $msg
        },
        pipeline => {
            target => 'Bailed Out:',
            msg => $msg,
        }
    };
    $self->log()->error("Fatal: $msg\n");
    my $exit = $self->setResult(%$result_params);
    $exit->();
}

### Functions for new interface.

sub args_cb_stop_deployment_manager {
    my ($self, $function_params) = @_;
    my $args = $self->gen_initial_args({trace => 1});
    my $step_params = $self->step_params();
    if ($step_params->{wasDeploymentManagerProfile}) {
        push @$args, ['-profileName', ' ', $step_params->{wasDeploymentManagerProfile}];
    }
    if (!$step_params->{wasLogFileLocation}) {
        $step_params->{wasLogFileLocation} = $ENV{COMMANDER_WORKSPACE} . '/stopServer.log';
        $self->log()->info("Log File Location has been set to $step_params->{wasLogFileLocation}");
        $self->{local_logfile} = 1;
    }
    if ($step_params->{wasLogFileLocation}) {
        push @$args, ['-logfile', ' ', $step_params->{wasLogFileLocation}];
    }
    if ($step_params->{wasTimeout}) {
        push @$args, ['-timeout', ' ', $step_params->{wasTimeout}];
    }
    if ($step_params->{wasAdditionalParameters}) {
        push @$args, [$step_params->{wasAdditionalParameters}, '',''];
    }
    return $args;
}

sub procedure_result_cb_stop_deployment_manager {
    my ($self) = @_;

    my $jobStepId = $self->{jobStepId};
    unless ($self->{local_logfile}) {
        my $log_path = $self->extractLogPath($self->cmd_res());
        cp($log_path, $ENV{COMMANDER_WORKSPACE} . '/stopServer.log');
    }
    my $html = $self->attachLogFile($jobStepId, 'stopServer.log');
    $self->log()->trace("HTML parameter for pipeline context is set to $html");
    if ($html) {
        push @{$self->{procedure_result}->{pipeline}}, {
            target => 'Stop Deployment Manager Log:',
            msg => $html
        };
    }
}

## Start deployment manager callbacks
sub args_cb_start_deployment_manager {
    my ($self, $function_params) = @_;
    my $args = $self->gen_initial_args({trace => 1, nopasswd => 1, noconntype => 1});
    my $step_params = $self->step_params();
    if ($step_params->{wasDeploymentManagerProfile}) {
        push @$args, ['-profileName', ' ', $step_params->{wasDeploymentManagerProfile}];
    }
    if (!$step_params->{wasLogFileLocation}) {
        $step_params->{wasLogFileLocation} = $ENV{COMMANDER_WORKSPACE} . '/startServer.log';
        $self->log()->info("Log File Location has been set to $step_params->{wasLogFileLocation}");
        $self->{local_logfile} = 1;
    }
    if ($step_params->{wasLogFileLocation}) {
        push @$args, ['-logfile', ' ', $step_params->{wasLogFileLocation}];
    }
    if ($step_params->{wasTimeout}) {
        push @$args, ['-timeout', ' ', $step_params->{wasTimeout}];
    }
    if ($step_params->{wasAdditionalParameters}) {
        push @$args, [$step_params->{wasAdditionalParameters}, '',''];
    }
    return $args;
}

sub procedure_result_cb_start_deployment_manager {
    my ($self) = @_;

    my $jobStepId = $self->{jobStepId};
    unless ($self->{local_logfile}) {
        my $log_path = $self->extractLogPath($self->cmd_res());
        cp($log_path, $ENV{COMMANDER_WORKSPACE} . '/startServer.log');
    }
    my $html = $self->attachLogFile($jobStepId, 'startServer.log');
    $self->log()->trace("HTML parameter for pipeline context is set to $html");
    if ($html) {
        push @{$self->{procedure_result}->{pipeline}}, {
            target => 'Start Deployment Manager Log:',
            msg => $html
        };
    }
}


# StopNode
sub args_cb_stop_node {
    my ($self, $function_params) = @_;
    my $args = $self->gen_initial_args({trace => 1});
    my $step_params = $self->step_params();
    if ($step_params->{wasNodeProfile}) {
        push @$args, ['-profileName', ' ', $step_params->{wasNodeProfile}];
    }
    if (!$step_params->{wasLogFileLocation}) {
        $step_params->{wasLogFileLocation} = $ENV{COMMANDER_WORKSPACE} . '/stopServer.log';
        $self->log()->info("Log File Location has been set to $step_params->{wasLogFileLocation}");
        $self->{local_logfile} = 1;
    }
    if ($step_params->{wasLogFileLocation}) {
        push @$args, ['-logfile', ' ', $step_params->{wasLogFileLocation}];
    }
    if ($step_params->{wasTimeout}) {
        push @$args, ['-timeout', ' ', $step_params->{wasTimeout}];
    }
    if ($step_params->{wasAdditionalParameters}) {
        push @$args, [$step_params->{wasAdditionalParameters}, '',''];
    }
    if ($step_params->{wasStopNodePolicy}) {
        my $snp = $step_params->{wasStopNodePolicy};
        if ($snp eq 'save_node_state') {
            push @$args, ['-saveNodeState', '', '']
        }
        elsif ($snp eq 'stop_application_servers') {
            push @$args, ['-stopservers', '', '']
        }
    }
    return $args;
}

sub procedure_result_cb_stop_node {
    my ($self) = @_;

    my $jobStepId = $self->{jobStepId};
    unless ($self->{local_logfile}) {
        my $log_path = $self->extractLogPath($self->cmd_res());
        cp($log_path, $ENV{COMMANDER_WORKSPACE} . '/stopServer.log');
    }
    my $html = $self->attachLogFile($jobStepId, 'stopServer.log');
    $self->log()->trace("HTML parameter for pipeline context is set to $html");
    if ($html) {
        push @{$self->{procedure_result}->{pipeline}}, {
            target => 'Stop Node Log:',
            msg => $html
        };
    }
}

## StartNode callbacks
sub args_cb_start_node {
    my ($self, $function_params) = @_;
    my $args = $self->gen_initial_args({trace => 1, nopasswd => 1, noconntype => 1});
    my $step_params = $self->step_params();
    if ($step_params->{wasNodeProfile}) {
        push @$args, ['-profileName', ' ', $step_params->{wasNodeProfile}];
    }
    if (!$step_params->{wasLogFileLocation}) {
        $step_params->{wasLogFileLocation} = $ENV{COMMANDER_WORKSPACE} . '/startServer.log';
        $self->log()->info("Log File Location has been set to $step_params->{wasLogFileLocation}");
        $self->{local_logfile} = 1;
    }
    if ($step_params->{wasLogFileLocation}) {
        push @$args, ['-logfile', ' ', $step_params->{wasLogFileLocation}];
    }
    if ($step_params->{wasTimeout}) {
        push @$args, ['-timeout', ' ', $step_params->{wasTimeout}];
    }
    if ($step_params->{wasAdditionalParameters}) {
        push @$args, [$step_params->{wasAdditionalParameters}, '',''];
    }
    return $args;
}

sub procedure_result_cb_start_node {
    my ($self) = @_;

    my $jobStepId = $self->{jobStepId};
    unless ($self->{local_logfile}) {
        my $log_path = $self->extractLogPath($self->cmd_res());
        cp($log_path, $ENV{COMMANDER_WORKSPACE} . '/startServer.log');
    }
    my $html = $self->attachLogFile($jobStepId, 'startServer.log');
    $self->log()->trace("HTML parameter for pipeline context is set to $html");
    if ($html) {
        push @{$self->{procedure_result}->{pipeline}}, {
            target => 'Start Node Log:',
            msg => $html
        };
    }
}

## internal package, required for logging ####################################
package EC::Plugin::Logger;

use strict;
use warnings;
use Data::Dumper;

use constant {
    ERROR => -1,
    INFO => 0,
    DEBUG => 1,
    TRACE => 2,
};

sub new {
    my ($class, $level) = @_;
    $level ||= 0;
    my $self = {level => $level};
    return bless $self,$class;
}

sub warning {
    my ($self, @messages) = @_;

    $self->log(INFO, 'WARNING: ', @messages);
}

sub info {
    my ($self, @messages) = @_;
    $self->log(INFO, @messages);
}

sub debug {
    my ($self, @messages) = @_;
    $self->log(DEBUG, '[DEBUG]', @messages);
}

sub error {
    my ($self, @messages) = @_;
    $self->log(ERROR, '[ERROR]', @messages);
}

sub trace {
    my ($self, @messages) = @_;
    $self->log(TRACE, '[TRACE]', @messages);
}

sub log {
    my ($self, $level, @messages) = @_;

    binmode STDOUT, ':encoding(UTF-8)';

    return if $level > $self->{level};
    my @lines = ();
    for my $message (@messages) {
        unless(defined $message) {
            $message = 'undef';
        }
        if (ref $message) {
            print Dumper($message);
        }
        else {
            print "$message";
        }
    }
    print "\n";
    return 1;
}

package WebSphere::Params;
use strict;
use warnings;

use Carp;

sub new {
    my ($class, %params) = @_;

    my $self = {};
    for my $k (keys %params) {
        next unless defined $params{$k};
        $self->{$k} = $params{$k};
    }
    bless $self, $class;
    return $self;
}

sub render {
    my ($self) = @_;

    my $string = '';
    my $t = '-%s \\"%s\\" ';
    my $t2 = '-%s %s ';
    for my $k (keys %$self) {
        if ($k =~ m/\s/) {
            croak "Parameter name should not have any spaces characters";
        }
        if (ref $self->{$k} && ref $self->{$k} eq 'HASH') {
            next;
        }
        ## TODO: add escape here
        if ($k eq 'customProperties') {
            $string .= sprintf($t2, $k, $self->{$k});
        }
        else {
            $string .= sprintf($t, $k, $self->{$k});
        }
    }
    return $string;
}

1;

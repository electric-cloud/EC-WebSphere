## Create or update JMS Queue
use warnings;
use strict;
use JSON;
use Data::Dumper;

use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::WebSphere;
use WebSphere::Util;


$| = 1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

my $opts = {
    # Required parameters handling:
    configname                          => '$[configname]',
    factoryScope                        => '$[factoryScope]',
    factoryAdministrativeName           => '$[factoryAdministrativeName]',
    factoryType                         => '$[factoryType]',
    jndiName                            => '$[jndiName]',

    # Non-required parameters:
    factoryAdministrativeDescription    => '$[factoryAdministrativeDescription]',
    clientChannelDefinitionUrl          => '$[clientChannelDefinitionUrl]',
    clientChannelDefinitionQueueManager => '$[clientChannelDefinitionQueueManager]',
    additionalOptions                   => '$[additionalOptions]',
};

print Dumper $opts;
my $ec = ElectricCommander->new();
$ec->abortOnError(0);
my $websphere = WebSphere::WebSphere->new($ec, $opts->{configname}, '');

my $parsedFactoryScope = $websphere->parseScope($opts->{factoryScope});
if ($opts->{factoryType} !~ m/^(?:CF|TCF|QCF)$/s) {
    $websphere->bail_out("Connection factory type should be one of: CF, TF, QCF. Found: $opts->{factoryType}");
}

my $r = $websphere->getParamsRenderer(
    name                => $opts->{factoryAdministrativeName},
    jndiName            => $opts->{jndiName},
    destinationJndiName => $opts->{destinationJndiName},
    destinationType     => $opts->{destinationJndiType},
    type                => $opts->{factoryType}
);

if ($opts->{factoryAdministrativeDescription}) {
    $r->{description} = $opts->{factoryAdministrativeDescription};
}

if ($opts->{clientChannelDefinitionUrl}) {
    $r->{ccdtUrl} = $opts->{clientChannelDefinitionUrl};
}
if ($opts->{clientChannelDefinitionQueueManager}) {
    $r->{ccdtQmgrName} = $opts->{clientChannelDefinitionQueueManager};
}
my $wasApi = 'WMQ_ConnectionFactory';

my $params = $r->render();

delete $r->{type};
my $edit_params = $r->render();
$edit_params .= ' ' . $opts->{additionalOptions};
$params .= ' ' . $opts->{additionalOptions};

$websphere->setTemplateProperties(
    requestParameters => $params,
    editParameters    => $edit_params,
    wasApi            => $wasApi,
    factoryScope      => $parsedFactoryScope,
    factoryType       => $opts->{factoryType},
);

# TODO:
# Check edit case, looks like type parameter should be omitted in case of edit.
my $logger = $websphere->log();
my $file = 'create_or_update_wmq_jms_connection_factory.py';
my $script = $ec->getProperty("/myProject/wsadmin_scripts/$file")->getNodeText('//value');
$file = $websphere->write_jython_script(
    $file, {},
    augment_filename_with_random_numbers => 1
);

my $shellcmd = $websphere->_create_runfile($file, ());
my $escaped_shellcmd = $websphere->_mask_password($shellcmd);
$logger->info('WSAdmin command line: ', $escaped_shellcmd);

$logger->debug("WSAdmin script:");
$logger->debug('' . $script);
$logger->debug("== End of WSAdmin script ==");
my %props = ();

$props{CreateOrUpdateJMSQueueLine} = $escaped_shellcmd;
my $cmd_res = `$shellcmd 2>&1`;
$logger->info($cmd_res);

my $code = $? >> 8;

my $result_params = {
    outcome => {
        target => 'myCall',
        result => '',
    },
    procedure => {
        target => 'myCall',
        msg => ''
    },
    pipeline => {
        target => 'Create Or Update WMQ JMS Resource Result:',
        msg => '',
    }
};

# success
my $operation_mode = 'create';
$operation_mode = $1 if $cmd_res =~ m/Operation\smode:\s(.*?)$/ms;
if ($code == SUCCESS) {
    my $message = sprintf 'Successfully created or updated %s: %s', $wasApi, $opts->{factoryAdministrativeName};
    $message = $1 if $cmd_res =~ m/Status:\sOK,\sMessage:\s(.*?)$/ms;
    $result_params->{outcome}->{result} = 'success';
    $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} = $message;
}
else {
    my $error = $websphere->extractWebSphereExceptions($cmd_res);
    if ($error) {
        $error = "Error occured during $operation_mode of $opts->{factoryAdministrativeName}:\n$error";
    }
    else {
        $error = "Unexpected error occured.";
    }
    $result_params->{outcome}->{result} = 'error';
    $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} = $error;
}

my $exit = $websphere->setResult(%$result_params);

$exit->();

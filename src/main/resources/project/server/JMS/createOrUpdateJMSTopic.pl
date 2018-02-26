## Create or update JMS Queue
use warnings;
use strict;
use JSON;

use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::WebSphere;
use WebSphere::Util;


$| = 1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

my $opts = {
    configname                     => '$[configname]',
    messagingSystemType            => '$[messagingSystemType]',
    topicScope                     => '$[topicScope]',
    topicAdministrativeName        => '$[topicAdministrativeName]',
    topicName                      => '$[topicName]',
    jndiName                       => '$[jndiName]',
    topicAdministrativeDescription => '$[topicAdministrativeDescription]',
    additionalOptions              => '$[additionalOptions]'
};

my $ec = ElectricCommander->new();
$ec->abortOnError(0);
my $websphere = WebSphere::WebSphere->new($ec, $opts->{configname}, '');

# handle params
my $r = $websphere->getParamsRenderer(
    name      => $opts->{topicAdministrativeName},
    topicName => $opts->{topicName},
    jndiName  => $opts->{jndiName},
);

if ($opts->{topicAdministrativeDescription}) {
    $r->{description} = $opts->{topicAdministrativeDescription};
}

my $wasApi = '_Topic';
if ($opts->{messagingSystemType} eq 'WMQ') {
    $wasApi = 'WMQ' . $wasApi;
}
elsif ($opts->{messagingSystemType} eq 'SIB') {
    # logic for SIB goes here
    $wasApi = 'SIB' . $wasApi;
}
else {
    $websphere->bail_out("Wrong Messaging System Type. Expected one of 'SIB', 'WMQ'. Got:$opts->{messagingSystemType}");
}

my $params = $r->render();

$params .= ' ' . $opts->{additionalOptions};

my $parsedTopicScope = $websphere->parseScope($opts->{topicScope});
$websphere->setTemplateProperties(
    requestParameters => $params,
    wasApi            => $wasApi,
    topicScope        => $parsedTopicScope,
);

my $logger = $websphere->log();
my $file = 'create_or_update_jms_topic.py';
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

$props{CreateOrUpdateJMSTopicLine} = $escaped_shellcmd;
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
        target => 'Create Or Update JMS Topic Result:',
        msg => '',
    }
};

# success
my $operation_mode = 'create';
$operation_mode = $1 if $cmd_res =~ m/Operation\smode:\s(.*?)$/ms;
if ($code == SUCCESS) {
    my $message = sprintf 'Successfully created or updated %s: %s for %s scope',
        $wasApi,
        $opts->{topicAdministrativeName},
        $parsedTopicScope;

    if ($cmd_res =~ m/Status:\sOK,\sMessage:\s(.*?)$/ms) {
        $message = $1;
        $message .= " for $parsedTopicScope scope";
    }
    $result_params->{outcome}->{result} = 'success';
    $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} = $message;
}
else {
    my $error = $websphere->extractWebSphereExceptions($cmd_res);
    if ($error) {
        $error = "Error occured during $operation_mode of $opts->{topicAdministrativeName}:\n$error";
    }
    else {
        $error = "Unexpected error occured.";
    }
    $result_params->{outcome}->{result} = 'error';
    $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} = $error;
}

my $exit = $websphere->setResult(%$result_params);

$exit->();

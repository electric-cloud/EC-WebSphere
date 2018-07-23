#
#  Copyright 2015 Electric Cloud, Inc.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#


# -------------------------------------------------------------------------
# Includes
# -------------------------------------------------------------------------
use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::WebSphere;
use WebSphere::Util;

use warnings;
use strict;
$| = 1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

my $config = '$[configname]';
my $server_list = '$[wasServersList]';
my $error_handling = '$[wasErrorHandling]';
my $wait_time = '$[wasWaitTime]';

rtrim(
    $config,
    $server_list,
    $error_handling,
    $wait_time
);

my $ec = ElectricCommander->new();
$ec->abortOnError(0);

my $websphere = WebSphere::WebSphere->new(
    $ec,
    $config,
);

if ($wait_time ne '' && $wait_time !~ m/^\d+$/s) {
    $websphere->bail_out("Wait time should be a positive integer, if present. Got: $wait_time");
}
if ($error_handling !~ m/^(?:FATAL|WARNING|SUCCESS)$/s) {
    $websphere->bail_out("Error handling should be one of: FATAL, WARNING, SUCCESS");
}

my $parsed_server_list = [];
my @result = split ',', $server_list;

for my $row (@result) {
    my @row = split '=', $row;

    if (scalar @row != 2) {
        $websphere->bail_out("Wrong server format. Expected: Node=server, got: $row");
    }
    rtrim ($row[0], $row[1]);
    push @$parsed_server_list, {
        Node => $row[0], Server => $row[1]
    };
}

$websphere->setTemplateProperties(
    # TODO: Think about python dict generation for list
    rawServersList    => $server_list,
    parsedServersList => $websphere->generatePythonStructure($parsed_server_list),
    waitTime          => $wait_time
);


my $logger = $websphere->log();
my $file = 'stop_middleware_server.py';
my $script = $ec->getProperty("/myProject/wsadmin_scripts/$file")->getNodeText('//value');



$file = $websphere->write_jython_script(
    $file, {},
    augment_filename_with_random_numbers => 1
);


my $shellcmd = $websphere->_create_runfile($file, ());
my $escaped_shellcmd = $websphere->_mask_password($shellcmd);

$logger->info('WSAdmin command line: ', $escaped_shellcmd);

$logger->debug("WSAdmin script:");
$logger->debug($script);
$logger->debug("== End of WSAdmin script ==");

my %props = ();

$props{stopMiddlewareServerLine} = $escaped_shellcmd;

# execute
my $cmd_res = `$shellcmd 2>&1`;

$logger->info($cmd_res);

my $code = $?;
$code >> 8;

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
        target => 'StopMiddlewareServer Result:',
        msg => '',
    }
};

$logger->info("Exit code: $code\n");

my @warnings = ();
if ($error_handling eq 'WARNING') {
    @warnings = $cmd_res =~ m/(WARNING:.*?)$/gms;
}
my $procedure_result = '';
if ($cmd_res =~ m/^Procedure result:(.*?)===.*?Done/gms) {
    $procedure_result = $1;
    rtrim($procedure_result);
}
if ($code == SUCCESS) {
    $result_params->{outcome}->{result} = 'success';
    $result_params->{procedure}->{msg} = "Stop Middleware Server results:\n$cmd_res";
    if (@warnings) {
        $result_params->{outcome}->{result} = 'warning';
        $result_params->{procedure}->{msg} .= "\nWarnings:\n" . join '', @warnings;
    }
    $result_params->{pipeline}->{msg} = $result_params->{procedure}->{msg};
}
else {
    my $error = $websphere->extractWebSphereExceptions($cmd_res);
    $result_params->{outcome}->{result} = 'error';
    $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} =
        sprintf("Failed to stop servers\n%s", $procedure_result);
}

my $exit = $websphere->setResult(%$result_params);

$exit->();


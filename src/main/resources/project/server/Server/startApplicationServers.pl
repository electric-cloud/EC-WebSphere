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
use warnings;
use strict;

use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::WebSphere;
use WebSphere::Util;
use Data::Dumper;

$| = 1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

my $config = '$[configname]';
my $server_list = '$[wasServersList]';
my $wait_time = '$[wasWaitTime]';

rtrim(
    $config,
    $server_list,
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


my $logger = $websphere->log();
my $file = 'start_application_servers.py';
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

$props{startApplicationServersLine} = $escaped_shellcmd;

# execute
my $cmd_res = `$shellcmd 2>&1`;

$logger->info($cmd_res);

my $code = $?;
$code = $code >> 8;

my $result_params = {
    outcome => {
        target => 'myJobStep',
        result => '',
    },
    procedure => {
        target => 'myCall',
        msg => ''
    },
    pipeline => {
        target => 'Start Application Servers Result:',
        msg => '',
    }
};

$logger->info("Exit code: $code\n");

my $procedure_logs = $websphere->parseProcedureLog($cmd_res);

if ($code == SUCCESS) {
    $result_params->{outcome}->{result} = 'success';
    $result_params->{procedure}->{msg} = "Application servers have been started:\n" . join "\n", @{$procedure_logs->{summary}};
    if (@{$procedure_logs->{warning}}) {
        my $warnings = join "\nWARNING: ", @{$procedure_logs->{warning}};
        $warnings = "WARNING: $warnings";
        $result_params->{procedure}->{msg} .= "\n$warnings";
        $result_params->{outcome}->{result} = 'warning';
    }
    $result_params->{pipeline}->{msg} = $result_params->{procedure}->{msg};
}
else {
    my $error = $websphere->extractWebSphereExceptions($cmd_res);
    $result_params->{outcome}->{result} = 'error';
    $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} =
        sprintf("Failed to start servers:\n%s",
                join("\n", (@{$procedure_logs->{summary}}, @{$procedure_logs->{error}}, @{$procedure_logs->{exception}})));
}

my $exit = $websphere->setResult(%$result_params);

$exit->();

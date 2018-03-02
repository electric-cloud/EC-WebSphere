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

my $wsadmin_abs_path = trim('$[wsadminabspath]');
my $config = '$[configname]';
my $appname = '$[appname]';
my $wait_time = '$[waitTimeForState]';
my $state_checked = '$[appStateChecked]';

rtrim(
    $wsadmin_abs_path,
    $appname,
    $wait_time,
    $state_checked
);

my $ec = ElectricCommander->new();
$ec->abortOnError(0);

my $websphere = WebSphere::WebSphere->new(
    $ec,
    $config,
    $wsadmin_abs_path
);

my $logger = $websphere->log();
my $file = 'check_app.py';
my $script = $ec->getProperty("/myProject/wsadmin_scripts/check_app.py")->getNodeText('//value');

$file = $websphere->write_jython_script(
    $file, {},
    augment_filename_with_random_numbers => 1
);

if ($wait_time eq '') {
    $wait_time = 0;
}
if (defined $wait_time && $wait_time !~ m/^\d+$/s) {
    $websphere->bail_out("Wait Time should be a positive integer.");
}

if (!is_valid_app_state($state_checked)) {
    $websphere->bail_out("$state_checked is not valid application state to be checked. Expected on of: EXISTS, NOT_EXISTS, READY, NOT_READY, RUNNING, NOT_RUNNING");
}

my $shellcmd = $websphere->_create_runfile($file, ());
my $escaped_shellcmd = $websphere->_mask_password($shellcmd);

$logger->info('WSAdmin command line: ', $escaped_shellcmd);

$logger->debug("WSAdmin script:");
$logger->debug($script);
$logger->debug("== End of WSAdmin script ==");

my %props = ();

$props{checkAppLine} = $escaped_shellcmd;

# execute
my $cmd_res = `$shellcmd 2>&1`;

my $app_state = 'NOT_EXISTS';
if ($cmd_res =~ m/Application\sState:\s'(.*?)'/ms) {
    $app_state = $1;
}
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
        target => 'CheckApp Result:',
        msg => '',
    }
};

my $matrix = {
    EXISTS      => 'is installed',
    NOT_EXISTS  => 'is not installed',
    READY       => 'is ready',
    NOT_READY   => 'is not ready',
    RUNNING     => 'is running',
    NOT_RUNNING => 'is not running',
};

if ($code == SUCCESS) {
    $result_params->{outcome}->{result} = 'success';
    $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} = "Application '$appname' $matrix->{$app_state}";
    if ($app_state ne $state_checked) {
        $result_params->{procedure}->{msg} .= " (application $matrix->{$state_checked})";
        $result_params->{pipeline}->{msg} = $result_params->{pipeline}->{msg};
    }
}
else {
    $result_params->{outcome}->{result} = 'error';
    $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} =
        "Application '$appname' $matrix->{$app_state}, expected: application $matrix->{$state_checked}";
}

my $exit = $websphere->setResult(%$result_params);

$exit->();

sub is_valid_app_state {
    my ($state) = @_;

    if ($state =~ m/^(?:EXISTS|NOT_EXISTS|READY|NOT_READY|RUNNING|NOT_RUNNING)$/s) {
        return 1;
    }
    return 0;
}

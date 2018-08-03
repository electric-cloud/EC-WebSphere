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
my $node_name = '$[wasNodeName]';
my $app_server_name = '$[wasAppServerName]';

rtrim(
    $config,
    $node_name,
    $app_server_name
);

my $ec = ElectricCommander->new();
$ec->abortOnError(0);

my $websphere = WebSphere::WebSphere->new(
    $ec,
    $config,
);

$websphere->setTemplateProperties(
    nodeName  => $node_name,
    appServerName => $app_server_name
);

my $logger = $websphere->log();
my $file = 'delete_application_server.py';
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

$props{deleteApplicationServerLine} = $escaped_shellcmd;

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
        target => 'Delete Application Server Result:',
        msg => '',
    }
};

$logger->info("Exit code: $code\n");
if ($code == SUCCESS) {
    $result_params->{outcome}->{result} = 'success';
    $result_params->{procedure}->{msg} = sprintf(
        'Application Server %s has been deleted on node %s.',
        $app_server_name, $node_name
    );
    $result_params->{pipeline}->{msg} = $result_params->{procedure}->{msg};
}
else {
    my $error = $websphere->extractWebSphereExceptions($cmd_res);
    $result_params->{outcome}->{result} = 'error';
    $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} =
        sprintf(
            'Failed to delete Application Server %s on node %s.' . "\n" . 'Error: %s',
            $app_server_name,
            $node_name,
            $error
        );
}

my $exit = $websphere->setResult(%$result_params);

$exit->();

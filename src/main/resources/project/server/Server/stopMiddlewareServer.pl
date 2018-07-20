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
my $server_list = '$[wasServerList]';
my $error_handling = '$[wasErrorHandling]';

rtrim(
    $config,
    $server_list,
    $error_handling
);

my $ec = ElectricCommander->new();
$ec->abortOnError(0);

my $websphere = WebSphere::WebSphere->new(
    $ec,
    $config,
);

$websphere->setTemplateProperties(
    # TODO: Think about python dict generation for list
    serverList  => $server_list
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

my $app_state = 'NOT_EXISTS';

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
        target => 'CreateApplicationServerTemplate Result:',
        msg => '',
    }
};

$logger->info("Exit code: $code\n");

# if ($code == SUCCESS) {
#     $result_params->{outcome}->{result} = 'success';
#     $result_params->{procedure}->{msg} = sprintf(
#         'Server Template %s has been deleted.',
#         $template_name
#     );
#     $result_params->{pipeline}->{msg} = $result_params->{procedure}->{msg};
# }
# else {
#     my $error = $websphere->extractWebSphereExceptions($cmd_res);
#     $result_params->{outcome}->{result} = 'error';
#     $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} =
#         sprintf(
#             'Failed to delete Server Template %s.\nError: %s',
#             $template_name,
#             $error
#         );
# }

my $exit = $websphere->setResult(%$result_params);

$exit->();


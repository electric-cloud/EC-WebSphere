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

use strict;
use warnings;
use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::WebSphere;
use WebSphere::Util;
use Data::Dumper;

### Input parameters
my $handling_logic = '$[wasProcedureHandlingLogic]';
my $entity_type    = '$[wasEntityType]';
my $entity_name    = '$[wasEntityName]';
my $entity_scope   = '$[wasEntityScope]';
### End of input parameters
my $base_message = sprintf(
    "Entity %s (type: %s) in scope (%s) ",
    $entity_name,
    $entity_type,
    $entity_scope
);
my $entity_does_not_exist_message = $base_message . 'does not exist.';
my $entity_exists_message = $base_message . 'exists';

my $success_message = $base_message . 'checked. See properties and output parameters for results.';
my $error_message = 'Failed to check ' . lcfirst($base_message) . ' for existence';

if ($handling_logic eq 'failIfExists') {
    ($success_message, $error_message) = ($entity_does_not_exist_message, $entity_exists_message);
}
elsif ($handling_logic eq 'failIfDoesNotExist') {
    ($error_message, $success_message) = ($entity_does_not_exist_message, $entity_exists_message);
}

my $ec = ElectricCommander->new();
$ec->abortOnError(0);

my $opts = {
    disabled_wsadmin_check => 0
};

my $websphere = WebSphere::WebSphere->new($ec, '', '', $opts);

$websphere->{jobStepId} = '$[jobStepId]';


my $step_params = {
    target => {
        pipeline => 'Check Entity For Existence Result:',
        success_summary => $success_message,
        error_summary   => $error_message
    },
    jython_script => {
        path => 'check_entity_for_existence.py',
    }
};

eval {
    $websphere->run_step($websphere->config_values()->{wsadminabspath}, $step_params);
    1;
} or do {
    my $exception = $@;
    rtrim($exception);
    $websphere->bail_out($exception);
};

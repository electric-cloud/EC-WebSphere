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


my $ec = ElectricCommander->new();
$ec->abortOnError(0);

my $opts = {
    disabled_wsadmin_check => 0
};

my $websphere = WebSphere::WebSphere->new($ec, '', '', $opts);

$websphere->{jobStepId} = '$[jobStepId]';


my $step_params = {
    target => {
        pipeline => 'Create Cluster Result:',
        success_summary => 'Cluster has been created.',
        error_summary   => 'Failed to create a cluster.'
    },
    jython_script => {
        path => 'create_cluster.py',
    },
};

eval {
    $websphere->run_step($websphere->config_values()->{wsadminabspath}, $step_params);
    1;
} or do {
    my $exception = $@;
    rtrim($exception);
    $websphere->bail_out($exception);
};

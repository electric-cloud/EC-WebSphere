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

# properties that are being used by this procedure:
# configname
# wasNodeName
# wasAppServerName
# wasTemplateName
# wasTemplateDescription

my $ec = ElectricCommander->new();
$ec->abortOnError(0);

my $opts = {
    disabled_wsadmin_check => 0
};

my $websphere = WebSphere::WebSphere->new($ec, '', '', $opts);

$websphere->{jobStepId} = '$[jobStepId]';


my $step_params = {
    target => {
        pipeline => 'Import Application Server Result:',
        success_summary => 'Application Server has been imported.',
        error_summary   => 'Failed to import an Application Server.'
    },
    error_cb => sub {
        my ($self) = @_;
        my $errors = $self->extractWebSphereExceptions($self->cmd_res);
        if ($errors) {
            $self->procedure_result()->{procedure}->{msg} .= ":\n$errors";
            $self->procedure_result()->{pipeline}->{msg} .= ":\n$errors";
        }
    },
    jython_script => {
        path => 'import_application_server.py',
    },
    # args_cb => \&WebSphere::WebSphere::args_cb_start_node,
    # procedure_result_cb => \&WebSphere::WebSphere::procedure_result_cb_start_node
};

eval {
    $websphere->run_step($websphere->config_values()->{wsadminabspath}, $step_params);
    1;
} or do {
    my $exception = $@;
    rtrim($exception);
    $websphere->bail_out($exception);
};

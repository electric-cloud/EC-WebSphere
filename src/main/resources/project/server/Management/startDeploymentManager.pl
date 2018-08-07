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
    disabled_wsadmin_check => 1
};
my $websphere = WebSphere::WebSphere->new($ec, '', '', $opts);
$websphere->{jobStepId} = '$[jobStepId]';


my $step_params = {
    target => {
        pipeline => 'Start Deployment Manager Result:',
        success_summary => 'Deployment Manager has been successfully started.',
        error_summary   => 'Failed to start Deployment Manager'
    },
    error_cb => sub {
        my ($self) = @_;
        my $errors = $self->extractWebSphereExceptions($self->cmd_res);
        if ($errors) {
            $self->procedure_result()->{procedure}->{msg} .= ":\n$errors";
            $self->procedure_result()->{pipeline}->{msg} .= ":\n$errors";
        }
    },
    args_cb => \&WebSphere::WebSphere::args_cb_start_deployment_manager,
    procedure_result_cb => \&WebSphere::WebSphere::procedure_result_cb_start_deployment_manager
};
eval {
    $websphere->run_step($websphere->get_step_parameters()->{wasStartManagerLocation}, $step_params);
    1;
} or do {
    my $exception = $@;
    rtrim($exception);
    $websphere->bail_out($exception);
};

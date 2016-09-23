# -------------------------------------------------------------------------
# Includes
# -------------------------------------------------------------------------
use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::Util;
use WebSphere::WebSphere;

use warnings;
use strict;
$| = 1;

use constant DEFAULT_TIMEOUT => 15;

#-------------------------------------------------------------------------
# Variables
#-------------------------------------------------------------------------

#get an EC object
my $ec = new ElectricCommander();
$ec->abortOnError(0);

main();

sub main {
    my @args = ();

    my $config_name = '$[configName]';
    my $websphere = WebSphere::WebSphere->new($ec, $config_name, '$[wsadminAbsPath]');
    die "No configuration found for config name $config_name" unless $websphere;

    my $python_filename = 'stop_cluster.py';
    $websphere->write_jython_script($python_filename);

    my $shellcmd = $websphere->_create_runfile( $python_filename, @args );
    my $escapedCmdLine = $websphere->_mask_password($shellcmd);

    # TODO log here
    print "WSAdmin command line:  $escapedCmdLine\n";
    my $props = {};
    $props->{'stopClusterLine'} = $escapedCmdLine;
    setProperties( $ec, $props );    #execute command
    print `$shellcmd 2>&1`;


    #evaluates if exit was successful to mark it as a success or fail the step
    if ( $? == SUCCESS ) {
        $ec->setProperty( "/myJobStep/outcome", 'success' );
    }
    else {
        $ec->setProperty( "/myJobStep/outcome", 'error' );
    }
}


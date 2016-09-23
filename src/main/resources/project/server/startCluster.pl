# -------------------------------------------------------------------------
# Includes
# -------------------------------------------------------------------------
use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::Util;

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

my $gWSAdminAbsPath = '$[wsadminAbsPath]';
my $gClusterName = '$[clusterName]';
my $gCellName  = '$[cellName]';
my $gConfigurationName = '$[configName]';
my $gTimeout = '$[clusterCommandTimeout]' || DEFAULT_TIMEOUT;

my %configuration = getConfiguration( $ec, $gConfigurationName );

main();

sub main {
    my @args = ();

    push( @args, qq|"$gWSAdminAbsPath"| );

    my $jython = get_jython_script($gCellName, $gClusterName, $gTimeout);

    my $jython_script_filename = 'startCluster.jython';

    open my $fh, ">$jython_script_filename" or die $!;
    print $fh $jython;
    close $fh;

    push( @args, '-f ' . $jython_script_filename );
    push( @args, '-lang ' . 'jython' );

    my $connectionType = $configuration{conntype};

    if ( $connectionType ne '' ) {
        push( @args, '-conntype ' . $connectionType );
    }

    if ( $configuration{'websphere_url'} ne '' ) {
        push( @args, '-host ' . $configuration{'websphere_url'} );
    }

    if ( $configuration{'websphere_port'} ne '' ) {
        push( @args, '-port ' . $configuration{'websphere_port'} );
    }

    if ( $configuration{'user'} ne '' ) {
        push( @args, '-user ' . $configuration{'user'} );
    }

    if ( $configuration{'password'} ne '' ) {
        push( @args, '-password ' . $configuration{'password'} );
    }

    my $cmdLine = createCommandLine( \@args );
    my $escapedCmdLine = maskPassword( $cmdLine, $configuration{'password'} );

    print "WSAdmin command line: $escapedCmdLine\n";

    #execute command
    system($cmdLine);

    #evaluates if exit was successful to mark it as a success or fail the step
    if ( $? == SUCCESS ) {
        $ec->setProperty( "/myJobStep/outcome", 'success' );
    }
    else {
        $ec->setProperty( "/myJobStep/outcome", 'error' );
    }
}


sub get_jython_script {
    my ($cell_name, $cluster_name, $timeout) = @_;

    my $newline = q{"\n"};
    my $jython = qq{
clusterName = '$cluster_name'
cellName = '$cell_name'

clusterMgr = AdminControl.completeObjectName('cell=' + cellName + ',type=ClusterMgr,*')
cluster = AdminControl.completeObjectName('cell=' + cellName + ',type=Cluster,name=' + clusterName + ',*')
AdminControl.invoke(cluster, 'start')

import time

def waitForClusterStatus( status, cluster, timeout = $timeout ):
    run_index = 0
    while( run_index < timeout ):
        run_index += 1
        time.sleep(1)
        clusterStatus = AdminControl.getAttribute(cluster, "state" )
        if clusterStatus == status:
            return 1
    return 0

def membersStatus(clusterName):
    clusterId = AdminConfig.getid('/ServerCluster:' + clusterName + '/')
    clusterList = AdminConfig.list('ClusterMember', clusterId)
    servers = clusterList.split()
    
    allStarted = 1

    for serverId in servers:
        serverName = AdminConfig.showAttribute(serverId, "memberName")
        server = AdminControl.completeObjectName("type=Server,name=" + serverName + ",*")
        if server == "":
            allStarted = 0
        else:
            state = AdminControl.getAttribute(server, "state")
            if state != "STARTED":
                allStarted = 0
                print "Server " + serverName + " is in " + state + " state "
               
    return allStarted

result = waitForClusterStatus( "websphere.cluster.running", cluster )

if result:
    print "Cluster started"
    sys.exit(0)
else:
    print "Cluster was not started, exited by timeout"
    membersStatus(clusterName)
    sys.exit(1)
};
    return $jython;
}
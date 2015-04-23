
=head1 NAME

createCluster.pl - a perl library to create cluster

=head1 SYNOPSIS


=head1 DESCRIPTION

A perl library that creates a new Application server cluster and optionally deploys
an application on it.

=head1 LICENSE

Copyright (c) 2014 Electric Cloud, Inc.
All rights reserved.

=head1 AUTHOR

    ---

=head2 METHODS

=cut

# -------------------------------------------------------------------------
# Includes
# -------------------------------------------------------------------------
use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::Util;

use warnings;
use strict;
$| = 1;

#-------------------------------------------------------------------------
# Variables
#-------------------------------------------------------------------------

#get an EC object
my $ec = new ElectricCommander();
$ec->abortOnError(0);

$::gWSAdminAbsPath =
  ( $ec->getProperty("wsadminabspath") )->findvalue("//value");
$::gClusterName = ( $ec->getProperty("clusterName") )->findvalue("//value");
$::gClusterMembers =
  ( $ec->getProperty("clusterMembers") )->findvalue("//value");
$::gDeployApp = ( $ec->getProperty("deployApp") )->findvalue("//value");
$::gAppName   = ( $ec->getProperty("appname") )->findvalue("//value");
$::gAppPath   = ( $ec->getProperty("apppath") )->findvalue("//value");
$::gCellName  = ( $ec->getProperty("cellname") )->findvalue("//value");
$::gConfigurationName =
  ( $ec->getProperty("configname") )->findvalue("//value");
$::gConnectionType =
  ( $ec->getProperty("connectiontype") )->findvalue("//value");

#-------------------------------------------------------------------------
# Main functions
#-------------------------------------------------------------------------

=over

=item B<myCmdr>

main - contains the whole process to be done by the plugin, it builds
       the command line, sets the properties and the working directory

B<Params:>

none

B<Returns:>

none

=back

=cut

sub main() {

    # create args array
    my @args = ();
    my %props;
    my %configuration;

    if ( $::gConfigurationName ne '' ) {
        %configuration = getConfiguration( $ec, $::gConfigurationName );
    }

    push( @args, '"' . $::gWSAdminAbsPath . '"' );

    my %NodeServerHash = constructNodeServerHash($::gClusterMembers);

    my $ScriptFile = 'import time' . "\n";
    $ScriptFile .=
        'result = AdminClusterManagement.createClusterWithoutMember(\''
      . $::gClusterName . '\')' . "\n"
      . 'print result';

    foreach my $node ( keys %NodeServerHash ) {
        $ScriptFile .= "\n"
          . 'result = AdminClusterManagement.createClusterMember("'
          . $::gClusterName . '", "'
          . $node . '", "'
          . $NodeServerHash{$node} . '")' . "\n"
          . 'print result';

    }

    if ($::gDeployApp) {

        if ( !$::gAppName ) {
            print "Error : Application name missing.";
            return;
        }
        elsif ( !$::gAppPath ) {
            print "Error : Application path missing.";
            return;
        }
        $ScriptFile .= "\n"
          . 'print \'Deploying an application '
          . $::gAppName . ' on '
          . $::gClusterName . '.\'' . "\n"
          . 'result = AdminApp.install(\''
          . $::gAppPath
          . '\',\'[-usedefaultbindings -contextroot /'
          . $::gAppName
          . ' -appname '
          . $::gAppName
          . ' -cluster '
          . $::gClusterName . ']\')' . "\n"
          . 'print result' . "\n"
          . 'AdminConfig.save()';

        foreach my $node ( keys %NodeServerHash ) {
            $ScriptFile .= "\n"
              . 'Sync1 = AdminControl.completeObjectName(\'type=NodeSync,node='
              . $node . ',*\')' . "\n"
              . 'AdminControl.invoke(Sync1, \'sync\')';

        }
    }

    $ScriptFile .=
      "\n\n" . 'print "\nStarting the cluster ' . $::gClusterName . '.\n"';
    $ScriptFile .= "\n"
      . 'cluster = AdminControl.completeObjectName(\'cell='
      . $::gCellName
      . ',type=Cluster,name='
      . $::gClusterName . ',*\')';
    $ScriptFile .= "\n" . 'print cluster';
    $ScriptFile .= "\n" . 'AdminControl.invoke(cluster, \'start\')';
    $ScriptFile .=
      "\n" . 'status = AdminControl.getAttribute(cluster, \'state\')';
    $ScriptFile .= "\n" . 'desiredStatus = \'websphere.cluster.running\'';
    $ScriptFile .= "\n" . 'print \'Cluster status = \' + status';
    $ScriptFile .= "\n" . 'while 1:';
    $ScriptFile .=
      "\n\t" . 'status = AdminControl.getAttribute(cluster, \'state\')';
    $ScriptFile .= "\n\t" . 'print \'Cluster status = \' + status';
    $ScriptFile .= "\n\t" . 'if status==desiredStatus:';
    $ScriptFile .= "\n\t\t" . 'break';
    $ScriptFile .= "\n\t" . 'else:';
    $ScriptFile .= "\n\t\t" . 'sleep(3)';

    ## Starting the cluster doesn't result it starting applications on indivisual members of the cluster.
    ## Restart each cluster member after application is deployed.

    $ScriptFile .= "\n"
      . 'print "\nRestarting every member of cluster after application deployment.\n"';
    $ScriptFile .= "\n" . 'AdminControl.invoke(cluster, \'rippleStart\')';

    $ScriptFile .=
      "\n" . 'status = AdminControl.getAttribute(cluster, \'state\')';
    $ScriptFile .= "\n" . 'desiredStatus = \'websphere.cluster.running\'';
    $ScriptFile .= "\n" . 'print \'Cluster status = \' + status';
    $ScriptFile .= "\n" . 'while 1:';
    $ScriptFile .=
      "\n\t" . 'status = AdminControl.getAttribute(cluster, \'state\')';
    $ScriptFile .= "\n\t" . 'print \'Cluster status = \' + status';
    $ScriptFile .= "\n\t" . 'if status==desiredStatus:';
    $ScriptFile .= "\n\t\t" . 'break';
    $ScriptFile .= "\n\t" . 'else:';
    $ScriptFile .= "\n\t\t" . 'sleep(3)';

    open( MYFILE, '>createCluster.jython' );

    print MYFILE "$ScriptFile";
    close(MYFILE);

    push( @args, '-f createCluster.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );
    push( @args, '-conntype ' . $::gConnectionType );

    #inject config...
    if (%configuration) {

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
    }

    my $cmdLine = createCommandLine( \@args );
    my $escapedCmdLine = maskPassword( $cmdLine, $configuration{'password'} );

    $props{'createJDBCLine'} = $escapedCmdLine;
    setProperties( $ec, \%props );

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

main();

=over

=item B<constructMetadataHash>

Constructs key:value pair of node:server hash.

B<Params:>

metadata - comma separated list of keys and values in the
             form of Node1=server1,Node2=server2

B<Returns:>

hash
C<[{"Node1" => "server1"}, {"Node2" => "server2"}]>

=back

=cut

sub constructNodeServerHash {
    my ($metadata) = @_;
    my $key;
    my $val;

    # Get each key:value pair
    my @pairs = split( ",", $metadata );

    # Convert array of key=values into hash
    my %metadata;
    foreach (@pairs) {
        ( $key, $val ) = split "=";
        $metadata{$key} = $val;
    }

    # return the resulting hash
    return %metadata;
}

1;

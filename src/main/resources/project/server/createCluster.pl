
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

my $gWSAdminAbsPath =
  ( $ec->getProperty("wsadminabspath") )->findvalue("//value");
my $gClusterName = ( $ec->getProperty("clusterName") )->findvalue("//value");
my $gClusterMembers =
  ( $ec->getProperty("clusterMembers") )->findvalue("//value");
my $gDeployApp = ( $ec->getProperty("deployApp") )->findvalue("//value");
my $gAppName   = ( $ec->getProperty("appname") )->findvalue("//value");
my $gAppPath   = ( $ec->getProperty("apppath") )->findvalue("//value");
my $gContextRoot = ( $ec->getProperty("contextRoot") )->findvalue("//value");
my $gCellName  = ( $ec->getProperty("cellname") )->findvalue("//value");
my $gConfigurationName =
  ( $ec->getProperty("configname") )->findvalue("//value");
my $gConnectionType =
  ( $ec->getProperty("connectionType") )->findvalue("//value");

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


    %configuration = getConfiguration( $ec, $gConfigurationName );


    push( @args, qq|"$gWSAdminAbsPath"| );

    my %NodeServerHash = constructNodeServerHash($gClusterMembers);

    my $ScriptFile = 'import time' . "\n";
    $ScriptFile .=
        'result = AdminClusterManagement.createClusterWithoutMember(\''
      . $gClusterName . '\')' . "\n"
      . 'print result';

    foreach my $node ( keys %NodeServerHash ) {
        $ScriptFile .= "\n"
          . 'result = AdminClusterManagement.createClusterMember("'
          . $gClusterName . '", "'
          . $node . '", "'
          . $NodeServerHash{$node} . '")' . "\n"
          . 'print result';

    }

    if ($gDeployApp) {

        if ( !$gAppName ) {
            print "Error : Application name missing.";
            return;
        }
        elsif ( !$gAppPath ) {
            print "Error : Application path missing.";
            return;
        }
        $ScriptFile .= "\n"
          . 'print \'Deploying an application '
          . $gAppName . ' on '
          . $gClusterName . '.\'' . "\n"
          . 'result = AdminApp.install(\''
          . $gAppPath
          . '\',\'[-usedefaultbindings -contextroot '
          . $gContextRoot
          . ' -appname '
          . $gAppName
          . ' -cluster '
          . $gClusterName . ']\')' . "\n"
          . 'print result' . "\n"
          . 'AdminConfig.save()';

    }

     foreach my $node ( keys %NodeServerHash ) {
        $ScriptFile .= "\n"
          . 'Sync1 = AdminControl.completeObjectName(\'type=NodeSync,node='
          . $node . ',*\')' . "\n"
          . 'AdminControl.invoke(Sync1, \'sync\')';

    }

    $ScriptFile .=
      "\n\n" . 'print "\nStarting the cluster ' . $gClusterName . '.\n"';
    $ScriptFile .= "\n"
      . 'cluster = AdminControl.completeObjectName(\'cell='
      . $gCellName
      . ',type=Cluster,name='
      . $gClusterName . ',*\')';
    $ScriptFile .= "\n" . 'print cluster';
    $ScriptFile .= "\n" . 'AdminControl.invoke(cluster, \'start\')';
    $ScriptFile .=
      "\n" . 'status = AdminControl.getAttribute(cluster, \'state\')';
    $ScriptFile .= "\n" . 'desiredStatus = \'websphere.cluster.running\'';
    $ScriptFile .= "\n" . 'print \'Cluster status = \' + status';
    $ScriptFile .= "\n" . 'waitTime = 3';
    $ScriptFile .= "\n" . 'while 1:';
    $ScriptFile .=
      "\n\t" . 'status = AdminControl.getAttribute(cluster, \'state\')';
    $ScriptFile .= "\n\t" . 'print \'Cluster status = \' + status';
    $ScriptFile .= "\n\t" . 'if status==desiredStatus:';
    $ScriptFile .= "\n\t\t" . 'break';
    $ScriptFile .= "\n\t" . 'else:';
    $ScriptFile .= "\n\t\t" . q(println 'Waiting for ' + `waitTime` + ' sec.');
    $ScriptFile .= "\n\t\t" . 'sleep(waitTime)';
    $ScriptFile .= "\n\t\t" . 'waitTime = waitTime + 3';

    open( MYFILE, '>createCluster.jython' );

    print MYFILE "$ScriptFile";
    close(MYFILE);

    push( @args, '-f createCluster.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );


    if ( $gConnectionType ne '' ) {
        push( @args, '-conntype ' . $gConnectionType );
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
    if (wantarray()) {
        return %metadata
    }
    else {
        return \%metadata
    }
}

1;

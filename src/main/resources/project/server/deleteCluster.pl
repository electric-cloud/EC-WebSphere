
=head1 NAME

deleteJDBCProvider.pl - a perl library to delete cluster

=head1 SYNOPSIS

=head1 DESCRIPTION

A perl library that deletes an existing Application server clusters.
It can optionally delete it forcefully.(By first stopping the cluster and then deletion)

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

my $ec = new ElectricCommander();
$ec->abortOnError(0);

$::gWSAdminAbsPath =
  ( $ec->getProperty("wsadminabspath") )->findvalue("//value");
$::gCellName    = ( $ec->getProperty("cellName") )->findvalue("//value");
$::gClusterName = ( $ec->getProperty("clusterName") )->findvalue("//value");
$::gDeleteForcefully =
  ( $ec->getProperty("deleteForcefully") )->findvalue("//value");
$::gConnectionType =
  ( $ec->getProperty("connectiontype") )->findvalue("//value");
$::gConfigurationName =
  ( $ec->getProperty("configname") )->findvalue("//value");

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

    my $ScriptFile = "import sys\n";

    if ($::gDeleteForcefully) {

        ## Validate the cell name
        $ScriptFile .= "cellname = AdminControl.getCell()\n";
        $ScriptFile .= "if cellname != \"" . $::gCellName . "\":\n";
        $ScriptFile .=
          "\tprint 'Error : Cell " . $::gCellName . " does not exist.'\n";
        $ScriptFile .= "\tsys.exit(1)\n";

        ## Validate cluster name
        $ScriptFile .= "result = AdminClusterManagement.checkIfClusterExists(\""
          . $::gClusterName . "\")\n";
        $ScriptFile .= "if result == \"false\":\n";
        $ScriptFile .=
          "\tprint 'Error : Cluster " . $::gClusterName . " does not exist.'\n";
        $ScriptFile .= "\tsys.exit(1)\n";

        $ScriptFile .= "\n"
          . 'cluster = AdminControl.completeObjectName(\'cell='
          . $::gCellName
          . ',type=Cluster,name='
          . $::gClusterName . ',*\')' . "\n";
        $ScriptFile .= 'print cluster' . "\n";
        $ScriptFile .= 'print \'Stopping cluster \'' . "\n";
        $ScriptFile .= 'AdminControl.invoke(cluster, \'stop\')' . "\n";
        $ScriptFile .=
          'status = AdminControl.getAttribute(cluster, \'state\')' . "\n";
        $ScriptFile .= 'desiredStatus = \'websphere.cluster.stopped\'' . "\n";
        $ScriptFile .= 'print \'Cluster status = \' + status' . "\n";
        $ScriptFile .= 'while 1:' . "\n";
        $ScriptFile .= "\t"
          . 'status = AdminControl.getAttribute(cluster, \'state\')' . "\n";
        $ScriptFile .= "\t" . 'print \'Cluster status = \' + status' . "\n";
        $ScriptFile .= "\t" . 'if status==desiredStatus:' . "\n";
        $ScriptFile .= "\t\t" . 'break' . "\n";
        $ScriptFile .= "\t" . 'else:' . "\n";
        $ScriptFile .= "\t\t" . 'sleep(3)' . "\n";
        $ScriptFile .= 'print \'Cluster stopped successfully\'' . "\n";

    }

    $ScriptFile .= 'result = AdminClusterManagement.deleteCluster("'
      . $::gClusterName . '")' . "\n";
    $ScriptFile .= 'print result' . "\n";

    open( MYFILE, '>>deleteCluster_script.jython' );

    print MYFILE "$ScriptFile";
    close(MYFILE);

    push( @args, '-f deleteCluster_script.jython' );
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

    $props{'deleteClusterLine'} = $escapedCmdLine;
    setProperties( $ec, \%props );

    print "WSAdmin command line: $escapedCmdLine\n";

    #execute command
    my $content = `$cmdLine`;

    #print log
    print "$content\n";

}

main();

1;

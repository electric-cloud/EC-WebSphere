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


=head1 NAME

deleteCluster.pl - a perl library to delete cluster

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
use WebSphere::WebSphere;
use warnings;
use strict;
$| = 1;

#-------------------------------------------------------------------------
# Variables
#-------------------------------------------------------------------------

my $ec = new ElectricCommander();
$ec->abortOnError(0);
my $websphere = WebSphere::WebSphere->new_simple($ec);

$::gWSAdminAbsPath =
  ( $ec->getProperty("wsadminabspath") )->findvalue("//value");
$::gClusterName = ( $ec->getProperty("clusterName") )->findvalue("//value");
$::gConfigurationName =
  ( $ec->getProperty("configname") )->findvalue("//value");

#-------------------------------------------------------------------------
# Main functions
#-------------------------------------------------------------------------

=over

=item B<main>

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
    else {
        die "Error : configuration not provided.";
    }

    push( @args, '"' . $::gWSAdminAbsPath . '"' );

    my $ScriptFile = "import sys\n";

    ## Validate cluster name
    $ScriptFile .= "result = AdminClusterManagement.checkIfClusterExists(\""
      . $::gClusterName . "\")\n";
    $ScriptFile .= "if result == \"false\":\n";
    $ScriptFile .=
      "\tprint 'Error : Cluster " . $::gClusterName . " does not exist.'\n";
    $ScriptFile .= "\tsys.exit(1)\n";

    $ScriptFile .= 'result = AdminClusterManagement.deleteCluster("'
      . $::gClusterName . '")' . "\n";
    $ScriptFile .= 'print result' . "\n";

    my $file = 'deleteCluster_script.jython';
    $file = $websphere->write_jython_script(
        $file, {},
        augment_filename_with_random_numbers => 1,
        script => $ScriptFile
    );
    push(@args, '-f ' . $file);

    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

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

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

listClusterMembers.pl - a perl library that lists all the members of the cluster

=head1 SYNOPSIS

=head1 DESCRIPTION

A perl library that lists down all the members of the cluster

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

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

$::gWSAdminAbsPath    = trim(q($[wsadminabspath]));
$::gConfigurationName = "$[configname]";
$::gClusterName       = trim(q($[clusterName]));

# -------------------------------------------------------------------------
# Main functions
# -------------------------------------------------------------------------

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
    my $ScriptFile = "\n";

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);

    ## Validate cluster name
    $ScriptFile .= "result = AdminClusterManagement.checkIfClusterExists(\""
      . $::gClusterName . "\")\n";
    $ScriptFile .= "if result == \"false\":\n";
    $ScriptFile .=
      "\tprint 'Error : Cluster " . $::gClusterName . " does not exist.'\n";
    $ScriptFile .= "\tsys.exit(1)\n";

    $ScriptFile .= 'result = AdminClusterManagement.listClusterMembers("'
      . $::gClusterName . '")';
    $ScriptFile .= "\n" . 'print result';


    %configuration = getConfiguration( $ec, $::gConfigurationName );


    push( @args, '"' . $::gWSAdminAbsPath . '"' );

    open( MYFILE, '>>listClusterMembers_script.jython' );

    print MYFILE "$ScriptFile";
    close(MYFILE);

    push( @args, '-f listClusterMembers_script.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    my $connectionType         = $configuration{conntype};

    if ( $connectionType && $connectionType ne '' ) {
        push( @args, '-conntype ' . $connectionType );
    }


    my $hostParamName;

    if ( $connectionType eq IPC_CONNECTION_TYPE ) {
        $hostParamName = '-ipchost';
    }
    else {
        $hostParamName = '-host';
    }

    if ( $configuration{'websphere_url'} ne '' ) {
        push( @args,
            $hostParamName . ' ' . $configuration{'websphere_url'} );
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

    $props{'listClusterMembersLine'} = $escapedCmdLine;
    setProperties( $ec, \%props );

    print "WSAdmin command line: $escapedCmdLine\n";

    #execute command
    my $content = `$cmdLine`;

    my @clusterMembers = $content =~ /Cluster member:\s(.+)/mg;

    # Convert array of cluster members into comma separated string
    my $clusterMembersList = '';
    foreach my $clusterMember (@clusterMembers) {

        if($clusterMembersList ne '') {
            $clusterMembersList .= ',';
        }
        $clusterMembersList .= $clusterMember;

    }
    $ec->setProperty( "/myJob/clusterMembers", $clusterMembersList );

    my @memberConfigurations = $content =~ /(\[.+\])/mg;
    $ec->setProperty( "/myJob/memberConfiguration", $memberConfigurations[0] );

    #print log
    print "$content\n";

    #evaluates if exit was successful to mark it as a success or fail the step
    if ( $? == SUCCESS ) {

        $ec->setProperty( "/myJobStep/outcome", 'success' );

    }
    else {
        $ec->setProperty( "/myJobStep/outcome", 'error' );
    }

}

main();

1;

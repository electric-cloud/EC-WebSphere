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

removeClusterMembers.pl - a perl library to deregister application server from a cluster.

=head1 SYNOPSIS

=head1 DESCRIPTION

a perl library to deregister application server from a cluster.

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

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

my $gWSAdminAbsPath    = trim(q($[wsadminabspath]));
my $gClusterMembers    = trim(q($[clusterMembers]));
my $gClusterName       = trim(q($[clusterName]));
my $gConfigurationName = "$[configname]";

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
    my $ScriptFile;

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);
    my $websphere = WebSphere::WebSphere->new_simple($ec);
    %configuration = getConfiguration( $ec, $gConfigurationName );

    my %NodeServerHash = constructNodeServerHash($gClusterMembers);

    ## Validate cluster name
    $ScriptFile .= "result = AdminClusterManagement.checkIfClusterExists(\""
      . $gClusterName . "\")\n";
    $ScriptFile .= "if result == \"false\":\n";
    $ScriptFile .=
      "\tprint 'Error : Cluster " . $gClusterName . " does not exist.'\n";
    $ScriptFile .= "\tsys.exit(1)\n";

    foreach my $node ( keys %NodeServerHash ) {
        $ScriptFile .= "\n"
          . 'result = AdminClusterManagement.deleteClusterMember("'
          . $gClusterName . '", "'
          . $node . '", "'
          . $NodeServerHash{$node} . '")' . "\n"
          . 'print result';

    }

    push( @args, '"' . $gWSAdminAbsPath . '"' );

    my $file = 'removeClusterMembers_script.jython';
    $file = $websphere->write_jython_script(
        $file, {},
        augment_filename_with_random_numbers => 1,
        script => $ScriptFile
    );
    push( @args, '-f ' . $file );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    my $connectionType    = $configuration{conntype};
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
        push( @args, $hostParamName . ' ' . $configuration{'websphere_url'} );
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

    $props{'removeClusterMembersLine'} = $escapedCmdLine;
    setProperties( $ec, \%props );

    print "WSAdmin command line: $escapedCmdLine\n";

    #execute command
    my $content = `$cmdLine`;

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
    if ( wantarray() ) {
        return %metadata;
    }
    else {
        return \%metadata;
    }
}

1;

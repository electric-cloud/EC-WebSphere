
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
$::gConnectionType    = trim(q($[connectiontype]));
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

    if ( $::gConnectionType && $::gConnectionType ne '' ) {
        push( @args, '-conntype ' . $::gConnectionType );
    }


    my $hostParamName;

    if ( $::gConnectionType eq IPC_CONNECTION_TYPE ) {
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

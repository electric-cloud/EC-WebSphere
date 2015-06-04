
=head1 NAME

createProfile.pl - a perl library to that creates a new webpshere profile.

=head1 SYNOPSIS

=head1 DESCRIPTION

a perl library to that creates profiles, which define runtime environments.
Using profiles instead of multiple product installations saves disk space and simplifies updating the product
because a single set of core product files is maintained.

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
use Switch;
use warnings;
use strict;
$| = 1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

my $ProfileName              = trim(q($[profileName]));
my $ProfilePath              = trim(q($[profilePath]));
my $ProfileTemplatePath      = trim(q($[profileTemplatePath]));
my $Host                     = trim(q($[host]));
my $CellName                 = trim(q($[cellName]));
my $NodeName                 = trim(q($[nodeName]));
my $ServerName               = trim(q($[serverName]));
my $AdminSecurityCredentials = trim(q($[adminSecurityCredentials]));
my $PortConfig               = trim(q($[portConfig]));
my $Ports                    = trim(q($[ports]));
my $ManageProfileScript      = trim(q($[manageProfileScript]));
my $AdditionalCommands       = "$[additionalcommands]";

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
    my $userName;
    my $password;

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);


    push( @args, '"' . $ManageProfileScript . '"' );

    push( @args, '-create' );

    # if target: add to command string
    if ( $ProfileName && $ProfileName ne '' ) {
        push( @args, '-profileName' );
        push( @args, $ProfileName );
    }

    if ( $ProfilePath && $ProfilePath ne '' ) {
        push( @args, '-profilePath' );
        push( @args, qq("$ProfilePath") );
    }

    if ( $ProfileTemplatePath && $ProfileTemplatePath ne '' ) {
        push( @args, '-templatePath' );
        push( @args, qq("$ProfileTemplatePath") );
    }

    if ( $Host && $Host ne '' ) {
        push( @args, '-hostName' );
        push( @args, qq("$Host") );
    }

    if ( $CellName && $CellName ne '' ) {
        push( @args, '-cellName' );
        push( @args, qq("$CellName") );
    }

    if ( $NodeName && $NodeName ne '' ) {
        push( @args, '-noadName' );
        push( @args, qq("$NodeName") );
    }

    if ( $ServerName && $ServerName ne '' ) {
        push( @args, '-serverName' );
        push( @args, $ServerName );
    }

    if ( $AdminSecurityCredentials && $AdminSecurityCredentials ne '' ) {
        push( @args, '-enableAdminSecurity true' );

        ## Get security credentials
        $userName = $ec->getFullCredential($AdminSecurityCredentials, {value => "password"})->findnodes("//userName")->string_value();
        $password = $ec->getFullCredential($AdminSecurityCredentials, {value => "userName"})->findnodes("//password")->string_value();

        push( @args, '-adminUserName' );
        push( @args, $userName );

        push( @args, '-adminPassword' );
        push( @args, $password );

    }

    if($PortConfig eq "startingPort") {
        push( @args, "-startingPort $Ports" );
    }elsif($PortConfig eq "portsFile"){
        push( @args, "-startingPort $Ports" );
    }elsif($PortConfig eq "defaultPorts") {
        push( @args, "-defaultPorts" );
    } else {
        if ( $Ports && $Ports ne '' ) {
            print 'Warning : Automatic port generation option is selected. Ignoring '. $Ports;
        }
    }


    if ( $AdditionalCommands && $AdditionalCommands ne '' ) {
        push( @args, $AdditionalCommands );
    }

    my $cmdLine = createCommandLine( \@args );
    $props{'startServerLine'} = $cmdLine;
    setProperties( $ec, \%props );

    print "Command line: $cmdLine\n";

    #execute command
    my $content = `$cmdLine`;

    #print log
    $content =~ s/-adminPassword $password/-adminPassword ****/;
    print "$content\n";

    #evaluates if exit was successful to mark it as a success or fail the step
    if ( $? == SUCCESS ) {

        $ec->setProperty( "/myJobStep/outcome", 'success' );
        $ec->setProperty("/myJob/ProfileName", $ProfileName);
        $ec->setProperty("/myJob/ProfilePath", $ProfilePath);
        $ec->setProperty("/myJob/HostName", $Host);
        $ec->setProperty("/myJob/CellName", $CellName);
        $ec->setProperty("/myJob/NodeName", $NodeName);
        $ec->setProperty("/myJob/ServerName", $ServerName);
    }
    else {
        $ec->setProperty( "/myJobStep/outcome", 'error' );
    }

}

main();

1;

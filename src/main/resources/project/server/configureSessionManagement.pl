
=head1 NAME

configureSessionManagement.pl - a perl library to that configures the session management for the deployed application.

=head1 SYNOPSIS

=head1 DESCRIPTION

A perl library that configures the session management for deployed application.

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

$::gAppName                 = trim(q($[appName]));
$::gWSAdminAbsPath          = trim(q($[wsadminAbsPath]));
$::gAppName                 = trim(q($[appName]));
$::gEnableCookie            = trim(q($[enableCookie]));
$::gEnableProtocolSwitching = trim(q($[enableProtocolSwitching]));
$::gEnableURLRewriting      = trim(q($[enableURLRewriting]));
$::gEnableSSLTracking       = trim(q($[enableSSLTracking]));
$::gEnableSerializedSession = trim(q($[enableSerializedSession]));
$::gAccessSessionOnTimeout  = trim(q($[accessSessionOnTimeout]));
$::gMaxWaitTime             = trim(q($[maxWaitTime]));
$::gSessionPersistMode      = trim(q($[sessionPersistMode]));
$::gMaxInMemorySessionCount = trim(q($[maxInMemorySessionCount]));
$::gAllowOverflow           = trim(q($[allowOverflow]));
$::gSessionEnable           = trim(q($[sessionEnable]));
$::gInvalidTimeout          = trim(q($[invalidTimeout]));
$::gConnectionType          = trim(q($[connectionType]));
$::gConfigurationName       = trim(q($[configName]));

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

    %configuration = getConfiguration( $ec, $::gConfigurationName );

    push( @args, '"' . $::gWSAdminAbsPath . '"' );

    if ( $::gAdditionalOptions && $::gAdditionalOptions ne '' ) {
        push( @args, $::gAdditionalOptions );
    }

    $ScriptFile =
      'AdminApplication.configureSessionManagementForAnApplication("'
      . $::gAppName . '",';

    if (   $::gEnableCookie
        && $::gEnableCookie ne ""
        && $::gEnableCookie ne "default" )
    {
        $ScriptFile .= '"' . $::gEnableCookie . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if (   $::gEnableProtocolSwitching
        && $::gEnableProtocolSwitching ne ""
        && $::gEnableProtocolSwitching ne "default" )
    {
        $ScriptFile .= '"' . $::gEnableProtocolSwitching . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if (   $::gEnableURLRewriting
        && $::gEnableURLRewriting ne ""
        && $::gEnableURLRewriting ne "default" )
    {
        $ScriptFile .= '"' . $::gEnableURLRewriting . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if (   $::gEnableSSLTracking
        && $::gEnableSSLTracking ne ""
        && $::gEnableSSLTracking ne "default" )
    {
        $ScriptFile .= '"' . $::gEnableSSLTracking . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if (   $::gEnableSerializedSession
        && $::gEnableSerializedSession ne ""
        && $::gEnableSerializedSession ne "default" )
    {
        $ScriptFile .= '"' . $::gEnableSerializedSession . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if ( $::gAccessSessionOnTimeout && $::gAccessSessionOnTimeout ne "" ) {
        $ScriptFile .= '"' . $::gAccessSessionOnTimeout . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if ( $::gMaxWaitTime && $::gMaxWaitTime ne "" ) {
        $ScriptFile .= '"' . $::gMaxWaitTime . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if (   $::gSessionPersistMode
        && $::gSessionPersistMode ne ""
        && $::gSessionPersistMode ne "default" )
    {
        $ScriptFile .= '"' . uc($::gSessionPersistMode) . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if (   $::gAllowOverflow
        && $::gAllowOverflow ne ""
        && $::gAllowOverflow ne "default" )
    {
        $ScriptFile .= '"' . $::gAllowOverflow . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if ( $::gMaxInMemorySessionCount && $::gMaxInMemorySessionCount ne "" ) {
        $ScriptFile .= '"' . $::gMaxInMemorySessionCount . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if ( $::gInvalidTimeout && $::gInvalidTimeout ne "" ) {
        $ScriptFile .= '"' . $::gInvalidTimeout . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if (   $::gSessionEnable
        && $::gSessionEnable ne ""
        && $::gSessionEnable ne "default" )
    {
        $ScriptFile .= '"' . $::gSessionEnable . '")';
    }
    else {
        $ScriptFile .= '"")';
    }

    $ScriptFile .= "\nAdminConfig.save()";

    open( MYFILE, '>configureSessionManagement_script.jython' );

    print MYFILE "$ScriptFile";
    close(MYFILE);

    push( @args, '-f configureSessionManagement_script.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    if ( $::gConnectionType && $::gConnectionType ne '' ) {
        push( @args, '-conntype ' . $::gConnectionType );
    }

    #inject config...
    my $hostParamName;

    if ( $::gConnectionType eq IPC_CONNECTION_TYPE ) {
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

    $props{'configSessionLine'} = $escapedCmdLine;
    setProperties( $ec, \%props );

    print "WSAdmin command line: $escapedCmdLine\n";

    #execute command
    my $content = `$cmdLine`;

    #print log
    print "$content\n";

    #evaluates if exit was successful to mark it as a success or fail the step
    if ( $? == SUCCESS ) {

        $ec->setProperty( "/myJobStep/outcome", 'success' );

        #set any additional error or warning conditions here
        #there may be cases that an error occurs and the exit code is 0.
        #we want to set to correct outcome for the running step
        if ( $content =~ m/WSVR0028I:/ ) {

            #license expired warning
            $ec->setProperty( "/myJobStep/outcome", 'warning' );
        }

    }
    else {
        $ec->setProperty( "/myJobStep/outcome", 'error' );
    }

}

main();

1;

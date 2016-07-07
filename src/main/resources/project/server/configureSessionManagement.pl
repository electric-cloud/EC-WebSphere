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
$::gInvalidTimeout          = trim(q($[invalidTimeout]));
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

    $ScriptFile =
      'AdminApplication.configureSessionManagementForAnApplication("'
      . $::gAppName . '",';

    if ( $::gEnableCookie )
    {
        $ScriptFile .= '"true",';
    }
    else {
        $ScriptFile .= '"false",';
    }

    if (   $::gEnableProtocolSwitching )
    {
        $ScriptFile .= '"true",';
    }
    else {
        $ScriptFile .= '"false",';
    }

    if (   $::gEnableURLRewriting )
    {
        $ScriptFile .= '"true",';
    }
    else {
        $ScriptFile .= '"false",';
    }

    if (   $::gEnableSSLTracking )
    {
        $ScriptFile .= '"true",';
    }
    else {
        $ScriptFile .= '"false",';
    }

    if (   $::gEnableSerializedSession )
    {
        $ScriptFile .= '"true",';
    }
    else {
        $ScriptFile .= '"false",';
    }

    if ( $::gAccessSessionOnTimeout ) {
        $ScriptFile .= '"true",';
    }
    else {
        $ScriptFile .= '"false",';
    }

    if ( $::gMaxWaitTime && $::gMaxWaitTime ne "" ) {
        $ScriptFile .= '"' . $::gMaxWaitTime . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if (   $::gSessionPersistMode
        && $::gSessionPersistMode ne "default" )
    {
        $ScriptFile .= '"' . uc($::gSessionPersistMode) . '",';
    }
    else {
        $ScriptFile .= '"NONE",';
    }

    if (   $::gAllowOverflow )
    {
        $ScriptFile .= '"true",';
    }
    else {
        $ScriptFile .= '"false",';
    }

    if ( $::gMaxInMemorySessionCount ) {
        $ScriptFile .= '"' . $::gMaxInMemorySessionCount . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    if ( $::gInvalidTimeout ) {
        $ScriptFile .= '"' . $::gInvalidTimeout . '",';
    }
    else {
        $ScriptFile .= '"",';
    }

    ## override the session management settings at the application level.
    $ScriptFile .= '"true")';


    $ScriptFile .= "\nAdminConfig.save()\n";

    # Obtain deployment manager MBean
    $ScriptFile .= "dm=AdminControl.queryNames('type=DeploymentManager,*')\n";

    # Synchronization of configuration changes is only required in network deployment.not in standalone server environment.
    $ScriptFile .= "if dm:\n"
               . "\tprint 'Synchronizing configuration repository with nodes. Please wait...'\n"
               . "\t" . 'nodes=AdminControl.invoke(dm, "syncActiveNodes", "true")' . "\n"
               . "\t" . "print 'The following nodes have been synchronized:'+str(nodes)\n"
               . "else:\n"
               . "\t" . "print 'Standalone server, no nodes to sync'\n";

    $ScriptFile .= "print 'Session management properties set successfully.'\n";

    open( MYFILE, '>configureSessionManagement_script.jython' );

    print MYFILE "$ScriptFile";
    close(MYFILE);

    push( @args, '-f configureSessionManagement_script.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    my $connectionType = $configuration{conntype};

    if ( $connectionType && $connectionType ne '' ) {
        push( @args, '-conntype ' . $connectionType );
    }

    #inject config...
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

    $props{'configSessionLine'} = $escapedCmdLine;
    setProperties( $ec, \%props );

    print "WSAdmin command line: $escapedCmdLine\n";

    #execute command
    my $content = `$cmdLine`;

    #print log
    print "$content\n";

    #evaluates if exit was successful to mark it as a success or fail the step
    if ( $? == SUCCESS ) {

        $ec->setProperty( "/myJobStep/result", 'success' );

        #set any additional error or warning conditions here
        #there may be cases that an error occurs and the exit code is 0.
        #we want to set to correct outcome for the running step
        if ( $content =~ m/WSVR0028I:/ ) {

            #license expired warning
            $ec->setProperty( "/myJobStep/result", 'warning' );
        }

    }
    else {
        $ec->setProperty( "/myJobStep/result", 'error' );
    }

}

main();

1;

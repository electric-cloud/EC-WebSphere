#co
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

configureEJBContainer.pl - a perl library to that configures the EJB container.

=head1 SYNOPSIS

=head1 DESCRIPTION

a perl library to that configures the EJB container.

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

my $gWSAdminAbsPath              = trim(q($[wsadminAbsPath]));
my $gCellName                    = trim(q($[cellName]));
my $gNodeName                    = trim(q($[nodeName]));
my $gServerName                  = trim(q($[serverName]));
my $gPassivationDirectory        = trim(q($[passivationDirectory]));
my $gInactivePoolCleanupInterval = trim(q($[inactivePoolCleanupInterval]));
my $gCacheSize                   = trim(q($[cacheSize]));
my $gCleanupInterval             = trim(q($[cleanupInterval]));
my $gEnableSFSBFailover          = trim(q($[enableSFSBFailover]));
my $gMessageBrokerDomainName     = trim(q($[messageBrokerDomainName]));
my $gDataReplicationMode         = trim(q($[dataReplicationMode]));
my $gConfigurationName           = '$[configname]';

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
    my $anotherOptionalParam = 0;

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);

    my $websphere = WebSphere::WebSphere->new_simple($ec);
    %configuration = getConfiguration( $ec, $gConfigurationName );

    push( @args, qq|"$gWSAdminAbsPath"| );

    $ScriptFile = "serv1 = AdminConfig.getid('";

    if ($gCellName) {

        $ScriptFile .= "/Cell:" . $gCellName;
    }

    if ($gNodeName) {

        $ScriptFile .= "/Node:" . $gNodeName;
    }

    ## ServerName is mandatory parameter, hence not checking it here.
    $ScriptFile .= "/Server:" . $gServerName . "/')\n";

    $ScriptFile .= "ejbc1 = AdminConfig.list('EJBContainer', serv1)\n";
    $ScriptFile .= "AdminConfig.modify(ejbc1, [";
    if ($gPassivationDirectory) {
        $ScriptFile .=
          "['passivationDirectory','" . $gPassivationDirectory . "']";
        $anotherOptionalParam = 1;
    }

    ## EJB container memory consumption feature.
    if ( $gCacheSize eq '' ) {
        ## Set default value
        $gCacheSize = '2053';
    }
    elsif ( $gCacheSize < 0 ) {
        die "Error : Cache size must be positive integer.";
    }

    if ( $gCleanupInterval eq '' ) {
        ## Set default value
        $gCleanupInterval = '3000';
    }
    elsif ( $gCleanupInterval > 2147483647 or $gCleanupInterval < 0 ) {
        die "Error : Valid range for clean up interval is 0 to 2147483647.";
    }

    if (
        $gInactivePoolCleanupInterval ne ''
        and (  $gInactivePoolCleanupInterval > 2147483647
            or $gInactivePoolCleanupInterval < 0 )
      )
    {
        die
"Error : Valid range for Inactive Pool Cleanup Interval is 0 to 2147483647.";
    }

    if ( $anotherOptionalParam == 1 ) {
        $ScriptFile .= ",";
    }
    $ScriptFile .=
        "['cacheSettings',[['cacheSize','"
      . $gCacheSize
      . "'],['cleanupInterval','"
      . $gCleanupInterval . "']]]";

    $anotherOptionalParam = 1;

    if ($gInactivePoolCleanupInterval) {
        if ( $anotherOptionalParam == 1 ) {
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['inactivePoolCleanupInterval','"
          . $gInactivePoolCleanupInterval . "']";
        $anotherOptionalParam = 1;
    }

    ## Stateful session bean failover feature.
    if ($gEnableSFSBFailover) {

        if ( $anotherOptionalParam == 1 ) {
            $ScriptFile .= ",";
        }
        $anotherOptionalParam = 1;

        # Check if user has specified replication domain.
        if ( $gMessageBrokerDomainName eq '' ) {
            die
"Error : Replication domain not provided.To enable stateful session bean failover, replication domain is must. ";
        }

  # Check if replication mode is specifed by user. Other wise set it to default.
        if ( $gDataReplicationMode eq '' ) {
            print
"Replication mode not specified. Defaulting to Both client and server mode.";
            $gDataReplicationMode = 'both';
        }

        $ScriptFile .=
"['enableSFSBFailover','true'],['drsSettings',[['dataReplicationMode','"
          . uc($gDataReplicationMode) . "'],"
          . "['ids',[]],"
          . "['messageBrokerDomainName','"
          . $gMessageBrokerDomainName . "'],"
          . "['overrideHostConnectionPoints',[]],"
          . "['properties',[]]]]])\n";

    }
    else {
        if ( $anotherOptionalParam == 1 ) {
            $ScriptFile .= ",";
        }
        $anotherOptionalParam = 1;
        $ScriptFile .= "['enableSFSBFailover','false']])\n";
    }

    $ScriptFile .= "AdminConfig.save()\n";
    $ScriptFile .= "print 'EJB container settings set successfully.'\n";
    $ScriptFile .= "print AdminConfig.showall(ejbc1)\n";

    my $file = 'configureEJBContainer_script.jython';
    $file = $websphere->write_jython_script(
        $file, {},
        augment_filename_with_random_numbers => 1,
        script => $ScriptFile
    );

    # open( MYFILE, '>configureEJBContainer_script.jython' );
    # print MYFILE "$ScriptFile";
    # close(MYFILE);

    push( @args, '-f ' . $file );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    my $hostParamName;
    my $connectionType = $configuration{'conntype'};

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

    if ( $connectionType ne '' ) {
            push( @args, '-conntype ' . $connectionType );
    }

    if ( $configuration{'user'} ne '' ) {
        push( @args, '-user ' . $configuration{'user'} );
    }

    if ( $configuration{'password'} ne '' ) {
        push( @args, '-password ' . $configuration{'password'} );
    }

    my $cmdLine = createCommandLine( \@args );
    my $escapedCmdLine = maskPassword( $cmdLine, $configuration{'password'} );

    $props{'configureEJBContainerLine'} = $escapedCmdLine;
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

1;


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
my $gConnectionType              = trim(q($[connectiontype]));
my $gConfigurationName           = "$[configname]";

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

    open( MYFILE, '>configureEJBContainer_script.jython' );

    print MYFILE "$ScriptFile";
    close(MYFILE);

    push( @args, '-f configureEJBContainer_script.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    my $hostParamName;

    if ( $gConnectionType eq IPC_CONNECTION_TYPE ) {
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

    if ( $gConnectionType ne '' ) {
            push( @args, '-conntype ' . $gConnectionType );
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

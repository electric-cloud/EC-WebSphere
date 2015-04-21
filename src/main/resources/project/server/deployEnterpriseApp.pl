
=head1 NAME

deployEnterpriseApp.pl - a perl library to deploy an enterprise application

=head1 SYNOPSIS

=head1 DESCRIPTION

A perl library that deploys an enterprise application on a cluster or standalone server

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

$::gConfigName             = trim(q($[configName]));
$::gWsadminAbsPath         = trim(q($[wsadminAbsPath]));
$::gConnectionType         = trim(q($[connectionType]));
$::gCommands               = trim(q($[commands]));
$::gClasspath              = trim(q($[classpath]));
$::gJavaParams             = trim(q($[javaparams]));
$::gAdditionalOptions      = trim(q($[additionalcommands]));
$::gAppName                = trim(q($[appName]));
$::gClusterList            = trim(q($[clusterList]));
$::gServerList             = trim(q($[serverList]));
$::gAppPath                = trim(q($[apppath]));
$::gPrecompileJSP          = trim(q($[precompileJSP]));
$::gInstallDir             = trim(q($[installDir]));
$::gDistributeApp          = trim(q($[distributeApp]));
$::gBinaryConfig           = trim(q($[binaryConfig]));
$::gDeployBeans            = trim(q($[deployBeans]));
$::gCreateMBeans           = trim(q($[createMBeans]));
$::gOverrideClassReloading = trim(q($[overrideClassReloading]));
$::gReloadInterval         = trim(q($[reloadInterval]));
$::gDeployWS               = trim(q($[deployWS]));
$::gValidateRefs           = trim(q($[validateRefs]));
$::gProcessEmbConfig       = trim(q($[processEmbConfig]));
$::gFilePermissions        = trim(q($[filePermissions]));
$::gCustomFilePermissions  = trim(q($[customFilePermissions]));
$::gBlaName                = trim(q($[blaName]));
$::gAutoResolveEJBRef      = trim(q($[autoResolveEJBRef]));
$::gDeployClientMod        = trim(q($[deployClientMod]));
$::gClientDeployMode       = trim(q($[clientDeployMode]));
$::gValidateSchema         = trim(q($[validateSchema]));
$::gMapModulesToServers    = trim(q($[MapModulesToServers]));
$::gAdditionalDeployParams = trim(q($[additionalDeployParams]));

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
    my %NodeServerHash = constructNodeServerHash($::gServerList);;
    my @clusterList = split( ",", $::gClusterList );
    my $cluster;

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);

    %configuration = getConfiguration( $ec, $::gConfigName );

    ## replace any whitespaces with underscores in application names.
    $::gAppName = join '_', split /\s+/,  $::gAppName;

    $ScriptFile = 'import time' . "\n";
    $ScriptFile .= "print 'Installing " . $::gAppName . " ....'\n";
    $ScriptFile .= "AdminApp.install('" . $::gAppPath . "','[";

    if ($::gPrecompileJSP) {
        $ScriptFile .= " -preCompileJSPs";
    }
    else {
        $ScriptFile .= " -nopreCompileJSPs";
    }

    if ($::gDistributeApp) {
        $ScriptFile .= " -distributeApp";
    }
    else {
        $ScriptFile .= " -nodistributeApp";
    }

    if ($::gInstallDir) {
        $ScriptFile .= " -installed.ear.destination " . $::gInstallDir;
    }

    if ($::gBinaryConfig) {
        $ScriptFile .= " -useMetaDataFromBinary";
    }
    else {
        $ScriptFile .= " -nouseMetaDataFromBinary";
    }

    if ($::gDeployBeans) {
        $ScriptFile .= " -deployejb";
    }
    else {
        $ScriptFile .= " -nodeployejb";
    }

    if ($::gCreateMBeans) {
        $ScriptFile .= " -createMBeansForResources";
    }
    else {
        $ScriptFile .= "  -nocreateMBeansForResources";
    }
    $ScriptFile .= " -appname " . $::gAppName;

    if ($::gOverrideClassReloading) {
        $ScriptFile .= " -reloadEnabled";
    }
    else {
        $ScriptFile .= "  -noreloadEnabled";
    }
    if ($::gReloadInterval) {
        $ScriptFile .= " -reloadInterval " . $::gReloadInterval;
    }

    if ($::gDeployWS) {
        $ScriptFile .= " -deployws";
    }
    else {
        $ScriptFile .= "  -nodeployws";
    }

    if ($::gValidateRefs) {

        $ScriptFile .= " -validateinstall" . $::gValidateRefs;

    }

    if ($::gProcessEmbConfig) {
        $ScriptFile .= " -processEmbeddedConfig";
    }
    else {
        $ScriptFile .= "  -noprocessEmbeddedConfig";
    }

    if ($::gAutoResolveEJBRef) {
        $ScriptFile .= " -useAutoLink";
    }
    else {
        $ScriptFile .= "  -nouseAutoLink";
    }

    if ($::gAutoResolveEJBRef) {
        $ScriptFile .= " -useAutoLink";
    }
    else {
        $ScriptFile .= "  -nouseAutoLink";
    }
    if ($::gCustomFilePermissions) {
        $ScriptFile .= " -filepermission " . $::gCustomFilePermissions;
    }
    elsif ($::gFilePermissions) {
        $ScriptFile .= "  -filepermission " . $::gFilePermissions;
    }
    else {
        $ScriptFile .=
          "  -filepermission .*\.dll=755#.*\.so=755#.*\.a=755#.*\.sl=755";
    }

    if ($::gBlaName) {
        $ScriptFile .= " -blaname " . $::gBlaName;
    }

    if ($::gProcessEmbConfig) {
        $ScriptFile .= " -processEmbeddedConfig";
    }
    else {
        $ScriptFile .= "  -noprocessEmbeddedConfig";
    }
    ## Client module deployment
    if ($::gDeployClientMod) {
        $ScriptFile .= " -enableClientModule";
        ## Set the mode of client deployment only if the client module deployment is enabled.
        if ($::gClientDeployMode) {
            $ScriptFile .= " -clientMode " . $::gClientDeployMode;
        }
        else {
            ## Set default mode if no mode is specified.
            $ScriptFile .= " -clientMode isolated";
        }
    }
    else {
        $ScriptFile .= " -noenableClientModule";
    }

    if ($::gValidateSchema) {
        $ScriptFile .= " -validateSchema";
    }
    else {
        $ScriptFile .= "  -novalidateSchema";
    }

    if ($::gMapModulesToServers) {
        $ScriptFile .=
          " -MapModulesToServers [" . $::gMapModulesToServers . "]";

    }
    if ($::gAdditionalDeployParams) {
        $ScriptFile .= " " . $::gAdditionalDeployParams;

    }

    $ScriptFile .= "]')\n";
    $ScriptFile .= "AdminConfig.save()\n";

    # Obtain deployment manager MBean
    $ScriptFile .= "dm=AdminControl.queryNames('type=DeploymentManager,*')\n";

    # Synchronization of configuration changes is only required in network deployment.not in standalone server environment.
    $ScriptFile .= "if dm:\n"
                   . "\tprint 'Synchronizing configuration repository with nodes. Please wait...'\n"
                   . "\t" . 'nodes=AdminControl.invoke(dm, "syncActiveNodes", "true")' . "\n"
                   . "\t" . "print 'The following nodes have been synchronized:'+str(nodes)\n"
                   . "else:\n"
                   . "\t" . "print 'Standalone server, no nodes to sync'\n";

    $ScriptFile .=
      "print 'Application  " . $::gAppName . " installed completely.'\n";

    $ScriptFile .= "result = AdminApp.isAppReady('"
          . $::gAppName . "')\n"
          . "print 'Is App Ready = ' + result\n"
          . "while result != 'true':\n"
          . "\tresult = AdminApp.isAppReady('"
          . $::gAppName . "')\n"
          . "\tprint 'Is App Ready = ' + result\n"
          . "\tsleep(3)\n"
          . "print 'The application is ready to start.'\n";

    foreach my $node ( keys %NodeServerHash ) {
        $ScriptFile .= "AdminApplication.startApplicationOnSingleServer('" . $::gAppName . "','" . $node . "','" .  $NodeServerHash{$node} . "')\n";
    }

    foreach $cluster (@clusterList) {
        $ScriptFile .= "AdminApplication.startApplicationOnCluster('" . $::gAppName . "','" . $cluster . "')\n";
    }

    $ScriptFile .= "print 'Application " . $::gAppName . " started successfully.'\n";

    push( @args, '"' . $::gWsadminAbsPath . '"' );

    if ( $::gAdditionalOptions && $::gAdditionalOptions ne '' ) {
        push( @args, $::gAdditionalOptions );
    }

    open( MYFILE, '>deployEnterpriseApp_script.jython' );

    print MYFILE "$ScriptFile";
    close(MYFILE);

    push( @args, '-f deployEnterpriseApp_script.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    if ( $::gClasspath && $::gClasspath ne '' ) {
        push( @args, '-wsadmin_classpath "' . $::gClasspath . '"' );
    }

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

    if ( $::gJavaParams && $::gJavaParams ne '' ) {
        foreach my $param ( split( SEPARATOR_CHAR, $::gJavaParams ) ) {
            push( @args, "-javaoption $param" );
        }
    }

    if ( $::gCommands && $::gCommands ne '' ) {
        foreach my $command ( split( "\n", $::gCommands ) ) {
            push( @args, "-command $command" );
        }
    }

    my $cmdLine = createCommandLine( \@args );
    my $escapedCmdLine = maskPassword( $cmdLine, $configuration{'password'} );

    $props{'deployEnterpriseAppLine'} = $escapedCmdLine;
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

=over

=item B<constructNodeServerHash>

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
    return %metadata;
}

main();

1;

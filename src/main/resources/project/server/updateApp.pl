
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

my $gAppName           = trim(q($[appName]));
my $gIsAppOnCluster    = trim(q($[isAppOnCluster]));
my $gClusterName       = trim(q($[clusterName]));
my $gServerName        = trim(q($[serverName]));
my $gWSAdminAbsPath    = trim(q($[wsadminAbsPath]));
my $gContentType       = trim(q($[contentType]));
my $gOperation         = trim(q($[operation]));
my $gContent           = trim(q($[content]));
my $gContentURI        = trim(q($[contentURI]));
my $gConnectionType    = trim(q($[connectionType]));
my $gAdditionalParams    = trim(q($[additionalParams]));
my $gConfigurationName = "$[configname]";

# -------------------------------------------------------------------------
# Main functions
# -------------------------------------------------------------------------

sub main() {

    # create args array
    my @args = ();
    my %props;
    my %configuration;

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);

    %configuration = getConfiguration( $ec, $gConfigurationName );

    if( $gOperation ne 'delete' && $gContent eq ''){
        print "Error : Content field can not be empty for operation " . $gOperation . ".";
        return;
    }

    if( ($gContentType eq 'file' || $gContentType eq 'modulefile') && $gContentURI eq ''){
        print "Error : Content URI field can not be empty for content type " . $gContentType . ".";
        return;
    }

    if( $gContentType eq 'app' && $gOperation ne 'update') {
        print "Error : Invalid operation " . $gOperation . " for content type APPLICATION.";
        return;
    }
    my $ScriptFile =
        "import sys\n"
      . "result = AdminApp.update('"
      . $gAppName . "', '"
      . $gContentType
      . "', '[-operation "
      . $gOperation
      . " -contents "
      . $gContent
      . " -contenturi "
      . $gContentURI;

    if($gAdditionalParams) {
        ## If optional parameters are supplied

        ## Sanitize any \r \n from the parameters
         $gAdditionalParams =~ s/\n/ /g;
         $gAdditionalParams  =~ s/\r/ /g;
        $ScriptFile .= " " . $gAdditionalParams;
    }

    $ScriptFile .= " ]')\n"
      . "print result\n"
      . "AdminConfig.save()\n"
      . "result = AdminApp.isAppReady('"
      . $gAppName . "')\n"
      . "print 'Is App Ready = ' + result\n"
      . "while result != 'true':\n"
      . "\tresult = AdminApp.isAppReady('"
      . $gAppName . "')\n"
      . "\tprint 'Is App Ready = ' + result\n"
      . "\tsleep(3)\n"
      . "print 'The application is ready to restart.'\n";

    ## If application is deployed on cluster then ripple start the cluster
    ## otherwise restart the application on single server.

    if ($gIsAppOnCluster) {
        ## If user has supplied the name of cluster on which application is deployed.

        if ($gClusterName) {
            ## Validate cluster name
            $ScriptFile .=
              "result = AdminClusterManagement.checkIfClusterExists(\""
              . $gClusterName . "\")\n";
            $ScriptFile .= "if result == \"false\":\n";
            $ScriptFile .=
                "\tprint 'Error : Cluster "
              . $gClusterName
              . " does not exist.'\n";
            $ScriptFile .= "\tsys.exit(1)\n";

            $ScriptFile .= "\n"
              . 'cluster = AdminControl.completeObjectName(\'type=Cluster,name='
              . $gClusterName . ',*\')' . "\n"
              . 'print cluster';

            $ScriptFile .= "\n"
              . 'print "Restarting every member of cluster after application is updated."';
            $ScriptFile .=
              "\n" . 'AdminControl.invoke(cluster, \'rippleStart\')';
            $ScriptFile .=
              "\n" . 'status = AdminControl.getAttribute(cluster, \'state\')';
            $ScriptFile .=
              "\n" . 'desiredStatus = \'websphere.cluster.running\'';
            $ScriptFile .= "\n" . 'print \'Cluster status = \' + status';
            $ScriptFile .= "\n" . 'while 1:';
            $ScriptFile .=
              "\n\t" . 'status = AdminControl.getAttribute(cluster, \'state\')';
            $ScriptFile .= "\n\t" . 'print \'Cluster status = \' + status';
            $ScriptFile .= "\n\t" . 'if status==desiredStatus:';
            $ScriptFile .= "\n\t\t" . 'break';
            $ScriptFile .= "\n\t" . 'else:';
            $ScriptFile .= "\n\t\t" . 'sleep(3)';
            $ScriptFile .= "\nprint 'Application is UP!'";

        }
        else {
            print "Error : Cluster name not provided.\n";
            return;
        }

    }
    else {

        if (my $gServerName) {

            $ScriptFile .=
"appManager = AdminControl.queryNames('type=ApplicationManager,process="
              . my $gServerName
              . ",*')\n"
              . "print appManager\n"
              . "result = AdminControl.invoke(appManager,'stopApplication','"
              . $gAppName . "')\n"
              . "print result\n"
              . "result = AdminControl.invoke(appManager,'startApplication','"
              . $gAppName . "')\n"
              . "print result\n"
              . "appstatus = AdminControl.completeObjectName('type=Application,name="
              . $gAppName
              . ",*')\n"
              . "if appstatus:\n"
              . "\tprint 'Application is UP!'\n"
              . "else:\n"
              . "\tprint 'Application is not UP.'\n";

        }
        else {
            print "Error : Server name not provided.\n";
            return;
        }

    }
    push( @args, '"' . $gWSAdminAbsPath . '"' );

    open( MYFILE, '>updateapp_script.jython' );

    print MYFILE "$ScriptFile";
    close(MYFILE);

    push( @args, '-f updateapp_script.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    if ( $gConnectionType && $gConnectionType ne '' ) {
        push( @args, '-conntype ' . $gConnectionType );
    }

    #inject config...
    if (%configuration) {
        my $hostParamName;

        if ( $gConnectionType eq IPC_CONNECTION_TYPE ) {
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
    }

    my $cmdLine = createCommandLine( \@args );
    my $escapedCmdLine = maskPassword( $cmdLine, $configuration{'password'} );

    $props{'updateAppLine'} = $escapedCmdLine;
    setProperties( $ec, \%props );

    print "WSAdmin command line: $escapedCmdLine\n";

    #execute command
    system($cmdLine);

    #evaluates if exit was successful to mark it as a success or fail the step
    $ec->setProperty( "/myJobStep/outcome", ($? == SUCCESS) ? 'success' : 'error');
}

main();

1;

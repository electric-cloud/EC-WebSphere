# Data that drives the create step picker registration for this plugin.
my %checkPageStatus = (
    label       => "WebSphere - Check Page Status",
    procedure   => "CheckPageStatus",
    description => "Check the status of a page on a given URL",
    category    => "Application Server"
);
my %checkServerStatus = (
    label       => "WebSphere - Check Server Status",
    procedure   => "CheckServerStatus",
    description => "Check the status of the given server URL",
    category    => "Application Server"
);
my %startServer = (
    label       => "WebSphere - Start Server",
    procedure   => "StartServer",
    description => "Start a WebSphere instance",
    category    => "Application Server"
);
my %stopServer = (
    label       => "WebSphere - Stop Server",
    procedure   => "StopServer",
    description => "Stop a WebSphere instance.",
    category    => "Application Server"
);
my %runCustomJob = (
    label       => "WebSphere - Run Custom Job",
    procedure   => "RunCustomJob",
    description => "Run a task using the wsadmin tool",
    category    => "Application Server"
);

my %startApp = (
    label       => "WebSphere - Start Application",
    procedure   => "StartApp",
    description => "Start an application using the wsadmin tool",
    category    => "Application Server"
);

my %stopApp = (
    label       => "WebSphere - Stop Application",
    procedure   => "StopApp",
    description => "Stop an application using the wsadmin tool",
    category    => "Application Server"
);

my %deployApp = (
    label       => "WebSphere - Deploy Application",
    procedure   => "DeployApp",
    description => "Deploy an application using the wsadmin tool",
    category    => "Application Server"
);

my %undeployApp = (
    label       => "WebSphere - Undeploy Application",
    procedure   => "UndeployApp",
    description => "Undeploy an application using the wsadmin tool",
    category    => "Application Server"
);

my %checkApp = (
    label       => "WebSphere - Check Application",
    procedure   => "CheckApp",
    description => "Check if an application is ready",
    category    => "Application Server"
);

my %createDatasource = (
    label       => "WebSphere - Create Datasource",
    procedure   => "CreateDatasource",
    description => "Creates a datasource using the wsadmin tool",
    category    => "Application Server"
);

my %deleteDatasource = (
    label       => "WebSphere - Delete Datasource",
    procedure   => "DeleteDatasource",
    description => "Deletes a datasource using the wsadmin tool",
    category    => "Application Server"
);

my %createJDBCProvider = (
    label       => "WebSphere - Create JDBC Provider",
    procedure   => "CreateJDBCProvider",
    description => "Creates a JDBC Provider using the wsadmin tool",
    category    => "Application Server"
);

my %deleteJDBCProvider = (
    label       => "WebSphere - Delete JDBC Provider",
    procedure   => "DeleteJDBCProvider",
    description => "Deletes a JDBC Provider using the wsadmin tool",
    category    => "Application Server"
);

my %listClusterMembers = (
    label       => "WebSphere - List Cluster Members",
    procedure   => "ListClusterMembers",
    description => "Lists all cluster members using the wsadmin tool",
    category    => "Application Server"
);

my %creatCluster = (
    label       => "WebSphere - Create Application server cluster",
    procedure   => "CreateCluster",
    description => "Creates a new Application Server cluster.",
    category    => "Application Server"
);

my %configureSessionManagement = (
    label       => "WebSphere - Configure Session Management",
    procedure   => "ConfigureSession",
    description => "Configures the session management properties for the deployed application.",
    category    => "Application Server"
);


my %createJMSProvider = (
    label       => "WebSphere - Create JMS Provider",
    procedure   => "CreateJMSProvider",
    description => "Creates a JMS Provider using the wsadmin tool",
    category    => "Application Server"
);

$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Start App");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Stop App");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Deploy App");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Undeploy App");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Check App");

$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Check Page Status");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Check Server Status");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Start Server");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Stop Server");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Run Custom Job");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Start Application");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Stop Application");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Deploy Application");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Undeploy Application");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Check Application");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Datasource");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Delete Datasource");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create JDBC Provider");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Delete JDBC Provider");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - List Cluster Members");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Application server cluster");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Configure Session Management");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create JMS Provider");

@::createStepPickerSteps = (\%checkPageStatus, \%checkServerStatus,
                            \%startServer, \%stopServer,
                            \%runCustomJob, \%startApp,
                            \%stopApp, \%deployApp,
                            \%undeployApp, \%checkApp, 
							\%createDatasource, \%deleteDatasource,
							\%createJDBCProvider, \%deleteJDBCProvider,
							\%creatCluster, \%configureSessionManagement,
							\%createJMSProvider, \%listClusterMembers);




if ($upgradeAction eq "upgrade") {
    my $query = $commander->newBatch();
    my $newcfg = $query->getProperty(
        "/plugins/$pluginName/project/websphere_cfgs");
    my $oldcfgs = $query->getProperty(
        "/plugins/$otherPluginName/project/websphere_cfgs");
	my $creds = $query->getCredentials(
        "\$[/plugins/$otherPluginName]");

	local $self->{abortOnError} = 0;
    $query->submit();

    # if new plugin does not already have cfgs
    if ($query->findvalue($newcfg,"code") eq "NoSuchProperty") {
        # if old cfg has some cfgs to copy
        if ($query->findvalue($oldcfgs,"code") ne "NoSuchProperty") {
            $batch->clone({
                path => "/plugins/$otherPluginName/project/websphere_cfgs",
                cloneName => "/plugins/$pluginName/project/websphere_cfgs"
            });
        }
    }
	
	# Copy configuration credentials and attach them to the appropriate steps
    my $nodes = $query->find($creds);
    if ($nodes) {
        my @nodes = $nodes->findnodes('credential/credentialName');
        for (@nodes) {
            my $cred = $_->string_value;

            # Clone the credential
            $batch->clone({
                path => "/plugins/$otherPluginName/project/credentials/$cred",
                cloneName => "/plugins/$pluginName/project/credentials/$cred"
            });

            # Make sure the credential has an ACL entry for the new project principal
            my $xpath = $commander->getAclEntry("user", "project: $pluginName", {
                projectName => $otherPluginName,
                credentialName => $cred
            });
            if ($xpath->findvalue("//code") eq "NoSuchAclEntry") {
                $batch->deleteAclEntry("user", "project: $otherPluginName", {
                    projectName => $pluginName,
                    credentialName => $cred
                });
                $batch->createAclEntry("user", "project: $pluginName", {
                    projectName => $pluginName,
                    credentialName => $cred,
                    readPrivilege => 'allow',
                    modifyPrivilege => 'allow',
                    executePrivilege => 'allow',
                    changePermissionsPrivilege => 'allow'
                });
            }

            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'StopServer',
                stepName => 'StopInstance'
            });

            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'RunCustomJob',
                stepName => 'RunJob'
            });
            
            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'DeployApp',
                stepName => 'DeployApp'
            });
            
            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'UndeployApp',
                stepName => 'UndeployApp'
            });
            
            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'StartApp',
                stepName => 'StartApp'
            });
            
            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'StopApp',
                stepName => 'StopApp'
            });
            

            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'CheckServerStatus',
                stepName => 'CheckServerStatus'
            });
            
            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'CheckApp',
                stepName => 'CheckApp'
            });
            
            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'CreateDatasource',
                stepName => 'CreateDatasource'
            });
            
            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'DeleteDatasource',
                stepName => 'DeleteDatasource'
            });
            
            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'CreateJDBCProvider',
                stepName => 'CreateJDBCProvider'
            });
            
            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'DeleteJDBCProvider',
                stepName => 'DeleteJDBCProvider'
            });

             # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'CreateCluster',
                stepName => 'CreateCluster'
            });

             # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'ConfigureSession',
                stepName => 'ConfigureSession'
            });

            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'CreateJMSProvider',
                stepName => 'CreateJMSProvider'
            });

            # Attach the credential to the appropriate steps
            $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                procedureName => 'ListClusterMembers',
                stepName => 'ListClusterMembers'
            });
        }
    }
}

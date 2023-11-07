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
use ElectricCommander::Util;
use strict;
use warnings;
no warnings 'redefine';
use Data::Dumper;

use ElectricCommander;
use JSON qw(decode_json);
use subs qw(debug);
use Time::HiRes qw(time gettimeofday tv_interval);

my @logs = ();
sub debug($) {
    my ($message) = @_;
    push @logs, scalar time . ": " . $message;

    if ($ENV{EC_SETUP_DEBUG}) {
        print scalar time . ": $message\n";
    }
}

# External Credential Manageent Update:
# We're retrieving the steps with attached creds from property sheet
my $stepsWithCredentials = getStepsWithCredentials();
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

my %deployEnterpriseApp = (
    label       => "WebSphere - Deploy Enterprise Application",
    procedure   => "DeployEnterpriseApp",
    description => "Deploys an enterprise application using wsadmin tool",
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
    description => "Check if an application is in desired state",
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


my %updateApp = (
    label       => "WebSphere - Update Application",
    procedure   => "UpdateApp",
    description => "Updates existing application",
    category    => "Application Server"
);

my %configEJBContainer = (
    label       => "WebSphere - Configure EJB Container",
    procedure   => "ConfigEJBContainer",
    description => "Configures EJB container using the wsadmin tool",
    category    => "Application Server"
);

my %createEndToEndMailProvider = (
    label       => "WebSphere - Create end to end Mail Provider",
    procedure   => "CreateEndToEndMailProvider",
    description => "Creates a mail provider along with protocol provider and mail session.",
    category    => "Application Server"
);

my %deployOSGi = (
    label       => "WebSphere - Deploy OSGi Application",
    procedure   => "DeployOSGiApp",
    description => "Deploy OSGi application using the wsadmin tool",
    category    => "Application Server"
);

my %publishWSDL = (
    label       => "WebSphere - Publish WSDL file",
    procedure   => "PublishWSDL",
    description => "Publishes WSDL files in each web services-enabled module to the file system location",
    category    => "Application Server"
);

my %removeClusterMembers = (
    label       => "WebSphere - Remove Cluster Members",
    procedure   => "RemoveClusterMembers",
    description => "Removes list of application servers from existing cluster",
    category    => "Application Server"
);


my %deleteCluster = (
    label       => "WebSphere - Delete Application server cluster",
    procedure   => "DeleteCluster",
    description => "Deletes an existing Application server cluster.",
    category    => "Application Server"
);

my %listClusterMembers = (
    label       => "WebSphere - List Cluster Members",
    procedure   => "ListClusterMembers",
    description => "Lists all cluster members using the wsadmin tool",
    category    => "Application Server"
);

my %startCluster = (
    label       => "WebSphere - Start Cluster",
    procedure   => "StartCluster",
    description => "Start cluster using the wsadmin tool",
    category    => "Application Server"
);

my %stopCluster = (
    label       => "WebSphere - Stop Cluster",
    procedure   => "StopCluster",
    description => "Stop cluster using the wsadmin tool",
    category    => "Application Server"
);

my %createCluster = (
    label       => "WebSphere - Create Application server Cluster",
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


my %createMailSession = (
    label       => "WebSphere - Create JavaMail session",
    procedure   => "CreateMailSession",
    description => "Creates a new JavaMail session using the wsadmin tool",
    category    => "Application Server"
);
my %mapSharedLibrary = (
    label       => "WebSphere - Map Shared Library",
    procedure   => "MapSharedLibrary",
    description => "Maps a shared library to the application on WebSphere.",
    category    => "Application Server"
);

my %modifyApplicationClassLoader = (
    label       => "WebSphere - Modify Application ClassLoader",
    procedure   => "ModifyApplicationClassLoader",
    description => "Modifies application class loader.",
    category    => "Application Server"
);

my %syncNodes = (
    label       => "WebSphere - Sync Nodes",
    procedure   => "SyncNodes",
    description => "Synchronizes Active Nodes.",
    category    => "Application Server"
);

my %checkNodeStatus = (
    label       => "WebSphere - Check Node Status",
    procedure   => "CheckNodeStatus",
    description => "Checks Node Status.",
    category    => "Application Server"
);

my %createOrUpdateJMSQueue = (
    label       => "WebSphere - Create Or Update JMS Queue",
    procedure   => "CreateOrUpdateJMSQueue",
    description => "Creates or updates JMS Queues",
    category    => "Application Server"
);

my %createOrUpdateJMSTopic = (
    label       => "WebSphere - Create Or Update JMS Topic",
    procedure   => "CreateOrUpdateJMSTopic",
    description => "Creates or updates JMS Topics",
    category    => "Application Server"
);


my %deleteJMSQueue = (
    label       => "WebSphere - Delete JMS Queue",
    procedure   => "DeleteJMSQueue",
    description => "Deletes JMS Queues",
    category    => "Application Server"
);

my %deleteJMSTopic = (
    label       => "WebSphere - Delete JMS Topic",
    procedure   => "DeleteJMSTopic",
    description => "Deletes JMS Topics",
    category    => "Application Server"
);

## AS and CF

## Create AS
my %createOrUpdateWMQJMSActivationSpec = (
    label       => "WebSphere - Create Or Update WMQ JMS Activation Spec",
    procedure   => "CreateOrUpdateWMQJMSActivationSpec",
    description => "Creates or updates WMQ JMS Activation Spec",
    category    => "Application Server"
);

my %createOrUpdateSIBJMSActivationSpec = (
    label       => "WebSphere - Create Or Update SIB JMS Activation Spec",
    procedure   => "CreateOrUpdateSIBJMSActivationSpec",
    description => "Creates or updates SIB JMS Activation Spec",
    category    => "Application Server"
);

## Create CF
my %createOrUpdateWMQJMSConnectionFactory = (
    label       => "WebSphere - Create Or Update WMQ JMS Connection Factory",
    procedure   => "CreateOrUpdateWMQJMSConnectionFactory",
    description => "Creates or updates WMQ JMS Connection Factory",
    category    => "Application Server"
);

my %createOrUpdateSIBJMSConnectionFactory = (
    label       => "WebSphere - Create Or Update SIB JMS Connection Factory",
    procedure   => "CreateOrUpdateSIBJMSConnectionFactory",
    description => "Creates or updates SIB JMS Connection Factory",
    category    => "Application Server"
);

# deleter for JMS AS

my %deleteJMSActivationSpec = (
    label       => "WebSphere - Delete JMS Activation Spec",
    procedure   => "DeleteJMSActivationSpec",
    description => "Deletes JMS Activation Spec",
    category    => "Application Server"
);

# deleter for JMS CF

my %deleteJMSConnectionFactory = (
    label       => "WebSphere - Delete JMS Connection Factory",
    procedure   => "DeleteJMSConnectionFactory",
    description => "Deletes JMS Connection Factory",
    category    => "Application Server"
);

my %deleteJMSProvider = (
    label       => "WebSphere - Delete JMS Provider",
    procedure   => "DeleteJMSProvider",
    description => "Deletes JMS Provider",
    category    => "Application Server"
);

# Create server
my %createApplicationServer = (
    label       => "WebSphere - Create Application Server",
    procedure   => "CreateApplicationServer",
    description => "This procedure creates an Application Server identical to an existing Application Server or an Application Server definition which is part of a template. In addition it will synchronize nodes if checked.",
    category    => "Application Server"
);

# Create app server template
my %createApplicationServerTemplate = (
    label       => "WebSphere - Create Application Server Template",
    procedure   => "CreateApplicationServerTemplate",
    description => "This procedure creates a template of an existing Application Server.",
    category    => "Application Server"
);

my %deleteApplicationServerTemplate = (
    label       => "WebSphere - Delete Application Server Template",
    procedure   => "DeleteApplicationServerTemplate",
    description => "This procedure deletes an existing Application Server Template.",
    category    => "Application Server"
);

my %startApplicationServers = (
    label       => "WebSphere - Start Application Servers",
    procedure   => "StartApplicationServers",
    description => "This procedure starts one or more Application Servers that can be spread across nodes.",
    category    => "Application Server"
);

my %stopApplicationServers = (
    label       => "WebSphere - Stop Application Servers",
    procedure   => "StopApplicationServers",
    description => "This procedure stops one or more Application Servers that can be spread across nodes.",
    category    => "Application Server"
);

my %exportApplicationServer = (
    label       => "WebSphere - Export Application Server",
    procedure   => "ExportApplicationServer",
    description => "This procedure exports the definition of an Application Server in an environment to an archive (with extension .car) in the filesystem, that can in turn be used import into another environment and create an identical server.",
    category    => "Application Server"
);

my %importApplicationServer = (
    label       => "WebSphere - Import Application Server",
    procedure   => "ImportApplicationServer",
    description => "This procedure imports an Application Server from an Archive and creates an identical server in the environment the Archive is imported into.",
    category    => "Application Server"
);

my %deleteApplicationServer = (
    label       => "WebSphere - Delete Application Server",
    procedure   => "DeleteApplicationServer",
    description => "This procedure deletes an existing Application Server. In addition it will synchronize nodes if checked.",
    category    => "Application Server"
);

my %startDeploymentManager = (
    label       => "WebSphere - Start Deployment Manager",
    procedure   => "StartDeploymentManager",
    description => "This procedure starts the Deployment Manager in a WAS network deployment.",
    category    => "Application Server"
);
my %stopDeploymentManager = (
    label       => "WebSphere - Stop Deployment Manager",
    procedure   => "StopDeploymentManager",
    description => "This procedure starts the Deployment Manager in a WAS network deployment.",
    category    => "Application Server"
);

my %stopNode = (
    label       => "WebSphere - Stop Node",
    procedure   => "StopNode",
    description => "This procedure stops a WAS node and stops the Application Servers in that Node based on the Stop Node policy.",
    category    => "Application Server"
);

my %startNode = (
    label       => "WebSphere - Start Node",
    procedure   => "StartNode",
    description => "This procedure starts a WAS node and optionally starts all Application Servers in that Node.",
    category    => "Application Server"
);

my %createFirstClusterMember = (
    label       => "WebSphere - Create First Cluster Member",
    procedure   => "CreateFirstClusterMember",
    description => "Creates first cluster member.",
    category    => "Application Server"
);

my %createClusterMembers = (
    label       => "WebSphere - Create Cluster Members",
    procedure   => "CreateClusterMembers",
    description => "Creates cluster members",
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
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Update Application");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Configure EJB Container");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create end to end Mail Provider");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Deploy OSGi Application");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Publish WSDL file");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Remove Cluster Members");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Delete Application server cluster");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - List Cluster Members");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Application server cluster");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Configure Session Management");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create JMS Provider");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create JavaMail session");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Map Shared Library");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Modify Application ClassLoader");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Sync Nodes");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Check Node Status");

$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Or Update JMS Queue");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Or Update JMS Topic");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Delete JMS Queue");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Delete JMS Topic");

# AS and CF procedures

# AS
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Or Update WMQ JMS Activation Spec");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Or Update SIB JMS Activation Spec");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Delete JMS Activation Spec");

# CF
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Or Update WMQ JMS Connection Factory");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Or Update SIB JMS Connection Factory");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Delete JMS Connection Factory");

# Delete JMS Provider procedure
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Delete JMS Provider");

# Create Server
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Server");

# Create Application Server Template
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Application Server Template");

# Delete Server Template
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Delete Server Template");

# Start/Stop Application Server
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Start Application Servers");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Stop Application Servers");

# Export/import Server Srocedures
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Export Server");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Import Server");

# Create and delete application server
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Delete Application Server");

# Start/Stop Deployment Manager
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Start Deployment Manager");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Stop Deployment Manager");

# Start/Stop Node
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Stop Node");

# Create Cluster
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Cluster");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create First Cluster Member");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/WebSphere - Create Cluster Members");

@::createStepPickerSteps = (
    \%checkPageStatus, \%checkServerStatus,
    \%startServer, \%stopServer,
    \%runCustomJob, \%startApp,
    \%stopApp, \%deployApp,
    \%undeployApp, \%checkApp,
    \%createDatasource, \%deleteDatasource,
    \%createJDBCProvider, \%deleteJDBCProvider,
    \%configureSessionManagement,
    \%createJMSProvider, \%listClusterMembers,
    \%deleteCluster, \%removeClusterMembers,
    \%publishWSDL, \%deployOSGi,
    \%createEndToEndMailProvider, \%createMailSession,
    \%configEJBContainer, \%updateApp,
    \%deployEnterpriseApp, \%startCluster,
    \%stopCluster, \%mapSharedLibrary,
    \%modifyApplicationClassLoader, \%syncNodes,
    \%checkNodeStatus,
    \%createOrUpdateJMSQueue, \%createOrUpdateJMSTopic,
    \%deleteJMSTopic, \%deleteJMSQueue,
    # AS procedures
    \%createOrUpdateWMQJMSActivationSpec, \%createOrUpdateSIBJMSActivationSpec, \%deleteJMSActivationSpec,
    # CF procedures
    \%createOrUpdateWMQJMSConnectionFactory, \%createOrUpdateSIBJMSConnectionFactory, \%deleteJMSConnectionFactory,
    # Delete JMS Provider procedure
    \%deleteJMSProvider,
    \%createApplicationServerTemplate,
    \%deleteApplicationServerTemplate,
    # Start/stop application server
    \%startApplicationServers, \%stopApplicationServers,
    # Import/Export server
    \%exportApplicationServer, \%importApplicationServer,
    # Create/Delete application server
    \%createApplicationServer,
    \%deleteApplicationServer,
    # Start/Stop Deployment Manager
    \%startDeploymentManager, \%stopDeploymentManager,
    # Start/Stop Node
    \%startNode, \%stopNode,
    # Create cluster
    \%createCluster,
    # Create 1st cluster member
    # Uncomment after 2.5.0 release
    \%createFirstClusterMember, \%createClusterMembers
);

if ($upgradeAction eq "upgrade") {
    migrateConfigurations($otherPluginName);
    migrateProperties($otherPluginName);
    debug "Migrated properties";
    reattachExternalCredentials($otherPluginName);
}

# Disabling this branch of logic temporary
if (0 && $upgradeAction eq "upgrade") {
    my $query = $commander->newBatch();

    # When upgrading from older versions, find steps that call plugins procedures
    # and remove extra outdated parameters
    my @filterList = ({ 'propertyName' => 'subproject',
         'operator' => 'equals',
         'operand1' => '/plugins/@PLUGIN_KEY@/project' });

    my $result = $commander->findObjects('procedureStep', {
        filter => [ { operator => 'and', filter => \@filterList} ]
    });

    for my $procedureStep ($result->findnodes('//step')) {
        my $projectName = $procedureStep->find('projectName')->string_value;
        my $procedureName = $procedureStep->find('procedureName')->string_value;
        my $stepName = $procedureStep->find('stepName')->string_value;

        if($procedureName eq 'UpdateApp') {
            $query->deleteActualParameter($projectName, $procedureName, $stepName, 'isAppOnCluster');
        }

        $query->deleteActualParameter($projectName, $procedureName, $stepName, 'connectionType');
    }

    # Update old configs, set conntype to SOAP if it does not exists
    my $old_configs_path = "/plugins/$otherPluginName/project/websphere_cfgs";
    my $configurations = $commander->getProperties({path => $old_configs_path});
    for my $configuration ($configurations->findnodes('//propertyName')) {
        my $conntype_path = $old_configs_path.$configuration->string_value."/conntype";

        my $conntype = $commander->getProperty($conntype_path);
        if(!$conntype->find('//value')) {
            $query->setProperty($conntype_path, 'SOAP');
        }
    }

    my $newcfg = $query->getProperty("/plugins/$pluginName/project/websphere_cfgs");
    my $oldcfgs = $query->getProperty($old_configs_path);
    my $olddiscovery = $query->getProperty("/plugins/$otherPluginName/project/ec_discovery/discovered_data");
    my $creds = $query->getCredentials("\$[/plugins/$otherPluginName]");

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

    # Copy discovered data
    if ($query->findvalue($olddiscovery, "code") ne "NoSuchProperty") {
        $batch->clone({
            path => "/plugins/$otherPluginName/project/ec_discovery/discovered_data",
            cloneName => "/plugins/$pluginName/project/ec_discovery/discovered_data"
        });
    }

    #--------------------------------------------------------------
    # Update Time Limit

    # use Data::Dumper;

    my $oldProjectName = "/plugins/$otherPluginName/project";
    my $newProjectName = "/plugins/$pluginName/project";

    # print Dumper($oldProjectName);
    # print Dumper($newProjectName);

    for my $procedure ($commander->getProcedures({ projectName => $oldProjectName })->findnodes('//procedure')) {
        my $procedureName = $procedure->findvalue('procedureName')->string_value;
        # print("Procedure: $procedureName\n");
        for my $oldStep ($commander->getSteps({ projectName => $oldProjectName, procedureName => $procedureName })->findnodes('//step')) {
            my $stepName = $oldStep->findvalue('stepName')->string_value;
            # print("\tStep: $stepName\n");

            my $oldTimeLimit = $oldStep->findvalue("//timeLimit");
            next unless($oldTimeLimit);

            my $oldTimeLimitUnits = $oldStep->findvalue("//timeLimitUnits");
            next unless($oldTimeLimitUnits);

            my $newStep = $commander->getStep($newProjectName, $procedureName, $stepName);

            my $newTimeLimit = $newStep->findvalue("//timeLimit");
            next unless($newTimeLimit);

            my $newTimeLimitUnits = $newStep->findvalue("//timeLimitUnits");
            next unless($newTimeLimitUnits);

            my $oldLimit = $oldTimeLimit->value;
            my $oldLimitUnits = $oldTimeLimitUnits->value;
            # print("\t\toldLimit: $oldLimit; $oldLimitUnits\n");

            my $res = $commander->modifyStep($newProjectName, $procedureName, $stepName, {timeLimit => $oldLimit, timeLimitUnits => $oldLimitUnits});

            my @errors = $res->findErrors;
            if (@errors > 0) {
                # print Dumper(\@errors);
                next
            }

            # print("\t\OK\n");
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

            for my $step (@$stepsWithCredentials) {
                # Attach the credential to the appropriate steps
                $batch->attachCredential("\$[/plugins/$pluginName/project]", $cred, {
                    procedureName => $step->{procedureName},
                    stepName => $step->{stepName}
                });
            }
        }
    }
}

if ($promoteAction eq "promote") {
    reattachExternalConfigurations($otherPluginName);
}

sub get_major_minor {
    my ($version) = @_;

    if ($version =~ m/^(\d+\.\d+)/) {
        return $1;
    }
    return undef;
}

sub reattachExternalCredentials {
    my ($otherPluginName) = @_;

    my $configName = getConfigLocation($otherPluginName);
    my $configsPath = "/plugins/$otherPluginName/project/$configName";

    my $xp = $commander->getProperty($configsPath);

    my $id = $xp->findvalue('//propertySheetId')->string_value();
    my $props = $commander->getProperties({propertySheetId => $id});
    for my $node ($props->findnodes('//property/propertySheetId')) {
        my $configPropertySheetId = $node->string_value();
        my $config = $commander->getProperties({propertySheetId => $configPropertySheetId});

        # iterate through props to get credentials.
        for my $configRow ($config->findnodes('//property')) {
            my $propName = $configRow->findvalue('propertyName')->string_value();
            my $propValue = $configRow->findvalue('value')->string_value();
            # print "Name $propName, value: $propValue\n";
            if ($propName =~ m/credential$/s && $propValue =~ m|^\/|s) {
                for my $step (@$stepsWithCredentials) {
                    $batch->attachCredential({
                        projectName    => $pluginName,
                        procedureName  => $step->{procedureName},
                        stepName       => $step->{stepName},
                        credentialName => $propValue,
                    });
                    #    debug "Attached credential to $step->{stepName}";
                }
                print "Reattaching $propName with val: $propValue\n";
            }
        }
        # exit 0;
    }
}

sub getConfigLocation {
    my ($otherPluginName) = @_;

    my $configName = eval {
        $commander->getProperty("/plugins/$otherPluginName/project/ec_configPropertySheet")->findvalue('//value')->string_value
    } || 'websphere_cfgs';
    return $configName;
}

sub getStepsWithCredentials {
    my $retval = [];
    eval {
        my $pluginName = '@PLUGIN_NAME@';
        my $stepsJson = $commander->getProperty("/projects/$pluginName/procedures/CreateConfiguration/ec_stepsWithAttachedCredentials")->findvalue('//value')->string_value;
        $retval = decode_json($stepsJson);
    };
    return $retval;
}

sub reattachExternalConfigurations {
    my ($otherPluginName) = @_;

    my %migrated = ();
    # For the configurations that exists while the plugin was deleted
    # The api is new so it requires the upgraded version of the agent
    eval {
        my $cfgs = $commander->getPluginConfigurations({
            pluginKey => '@PLUGIN_KEY@',
        });
        my @creds = ();
        for my $cfg ($cfgs->findnodes('//pluginConfiguration/credentialMappings/parameterDetail')) {
            my $value = $cfg->findvalue('parameterValue')->string_value();
            push @creds, $value;
        }

        for my $cred (@creds) {
            next if $migrated{$cred};
            for my $stepWithCreds (@$stepsWithCredentials) {
                $commander->attachCredential({
                    projectName => "/plugins/$pluginName/project",
                    credentialName => $cred,
                    procedureName => $stepWithCreds->{procedureName},
                    stepName => $stepWithCreds->{stepName}
                });
            }
            $migrated{$cred} = 1;
            debug "Migrated $cred";
        }
        1;
    } or do {
        debug "getPluginConfiguration API is not supported on the promoting agent, falling back";
        for my $stepWithCreds (@$stepsWithCredentials) {
            my $step = $commander->getStep({
                projectName => "/plugins/$otherPluginName/project",
                procedureName => $stepWithCreds->{procedureName},
                stepName => $stepWithCreds->{stepName},
            });
            for my $attachedCred ($step->findnodes('//attachedCredentials/credentialName')) {
                my $credName = $attachedCred->string_value();
                $commander->attachCredential({
                    projectName => "/plugins/$pluginName/project",
                    credentialName => $credName,
                    procedureName => $stepWithCreds->{procedureName},
                    stepName => $stepWithCreds->{stepName}
                });
                $migrated{$credName} = 1;
                debug "Migrated credential $credName to $stepWithCreds->{procedureName}";
            }
        }
    };
}

sub migrateConfigurations {
    my ($otherPluginName) = @_;

    my $configName = getConfigLocation($otherPluginName);
    # my $configName = eval {
    #     $commander->getProperty("/plugins/$otherPluginName/project/ec_configPropertySheet")->findvalue('//value')->string_value
    # } || 'ec_plugin_cfgs';

    $commander->clone({
        path      => "/plugins/$otherPluginName/project/$configName",
        cloneName => "/plugins/$pluginName/project/$configName"
    });

    my $xpath = $commander->getCredentials("/plugins/$otherPluginName/project");
    for my $credential ($xpath->findnodes('//credential')) {
        my $credName = $credential->findvalue('credentialName')->string_value;

        # If credential name starts with "/", it means that it is a reference.
        # We do not need to clone it.
        # if ($credName !~ m|^\/|s) {
        debug "Migrating old configuration $credName";
        $batch->clone({
            path      => "/plugins/$otherPluginName/project/credentials/$credName",
            cloneName => "/plugins/$pluginName/project/credentials/$credName"
        });
        $batch->deleteAclEntry({
            principalName  => "project: $otherPluginName",
            projectName    => $pluginName,
            credentialName => $credName,
            principalType  => 'user'
        });
        $batch->deleteAclEntry({
            principalType  => 'user',
            principalName  => "project: $pluginName",
            credentialName => $credName,
            projectName    => $pluginName,
        });

        $batch->createAclEntry({
            principalType              => 'user',
            principalName              => "project: $pluginName",
            projectName                => $pluginName,
            credentialName             => $credName,
            objectType                 => 'credential',
            readPrivilege              => 'allow',
            modifyPrivilege            => 'allow',
            executePrivilege           => 'allow',
            changePermissionsPrivilege => 'allow'
        });
        #}

        for my $step (@$stepsWithCredentials) {
            $batch->attachCredential({
                projectName    => $pluginName,
                procedureName  => $step->{procedureName},
                stepName       => $step->{stepName},
                credentialName => $credName,
            });
            debug "Attached credential to $step->{stepName}";
        }
    }
}

sub migrateProperties {
    my ($otherPluginName) = @_;
    my $clonedPropertySheets = eval {
        decode_json($commander->getProperty("/plugins/$otherPluginName/project/ec_clonedProperties")->findvalue('//value')->string_value);
    };
    unless ($clonedPropertySheets) {
        debug "No properties to migrate";
        return;
    }

    for my $prop (@$clonedPropertySheets) {
        $commander->clone({
            path      => "/plugins/$otherPluginName/project/$prop",
            cloneName => "/plugins/$pluginName/project/$prop"
        });
        debug "Cloned $prop"
    }
}

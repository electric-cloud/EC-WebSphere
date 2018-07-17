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

##########################
# createAndAttachCredential.pl
##########################

use ElectricCommander;

use constant {
	SUCCESS => 0,
	ERROR   => 1,
};

my $ec = new ElectricCommander();
$ec->abortOnError(0);

my $credName = "$[/myJob/config]";
my $xpath = $ec->getFullCredential("credential");
my $userName = $xpath->findvalue("//userName");
my $password = $xpath->findvalue("//password");

# Create credential
my $projName = "@PLUGIN_KEY@-@PLUGIN_VERSION@";

$ec->deleteCredential($projName, $credName);
$xpath = $ec->createCredential($projName, $credName, $userName, $password);
my $errors = $ec->checkAllErrors($xpath);

# Give config the credential's real name
my $configPath = "/projects/$projName/websphere_cfgs/$credName";
$xpath = $ec->setProperty($configPath . "/credential", $credName);
$errors .= $ec->checkAllErrors($xpath);

# Give job launcher full permissions on the credential
my $user = "$[/myJob/launchedByUser]";
$xpath = $ec->createAclEntry("user", $user,
    {projectName => $projName,
     credentialName => $credName,
     readPrivilege => allow,
     modifyPrivilege => allow,
     executePrivilege => allow,
     changePermissionsPrivilege => allow});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'StopServer',
     stepName => 'StopInstance'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CheckServerStatus',
     stepName => 'CheckServerStatus'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'RunCustomJob',
     stepName => 'RunJob'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'StartApp',
     stepName => 'StartApp'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'StopApp',
     stepName => 'StopApp'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DeployApp',
     stepName => 'DeployApp'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'UndeployApp',
     stepName => 'UndeployApp'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CheckApp',
     stepName => 'CheckApp'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DeployEnterpriseApp',
     stepName => 'DeployEnterpriseApp'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'UpdateApp',
     stepName => 'UpdateApp'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'ConfigEJBContainer',
     stepName => 'ConfigEJBContainer'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateEndToEndMailProvider',
     stepName => 'CreateEndToEndMailProvider'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DeployOSGiApp',
     stepName => 'DeployOSGiApp'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'publishWSDL',
     stepName => 'publishWSDL'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'RemoveClusterMembers',
     stepName => 'RemoveClusterMembers'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DeleteCluster',
     stepName => 'DeleteCluster'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'ListClusterMembers',
     stepName => 'ListClusterMembers'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'StartCluster',
     stepName => 'StartCluster'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'StopCluster',
     stepName => 'StopCluster'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateCluster',
     stepName => 'CreateCluster'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'ConfigureSession',
     stepName => 'ConfigureSession'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateJMSProvider',
     stepName => 'CreateJMSProvider'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateMailSession',
     stepName => 'CreateMailSession'});

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'Discover',
     stepName => 'DiscoverResources'});

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DiscoverResource',
     stepName => 'DiscoverResource'});

$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'MapSharedLibrary',
     stepName => 'MapSharedLibrary'});

$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'ModifyApplicationClassLoader',
     stepName => 'ModifyApplicationClassLoader'});

$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'SyncNodes',
     stepName => 'SyncNodes'});

$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CheckNodeStatus',
     stepName => 'CheckNodeStatus'});

$errors .= $ec->checkAllErrors($xpath);


$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateJDBCProvider',
     stepName => 'CreateJDBCProvider'});

$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DeleteJDBCProvider',
     stepName => 'DeleteJDBCProvider'});

$errors .= $ec->checkAllErrors($xpath);


$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateDatasource',
     stepName => 'CreateDatasource'});

$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DeleteDatasource',
     stepName => 'DeleteDatasource'});

$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateOrUpdateJMSQueue',
     stepName => 'CreateOrUpdateJMSQueue'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateOrUpdateJMSTopic',
     stepName => 'CreateOrUpdateJMSTopic'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DeleteJMSQueue',
     stepName => 'DeleteJMSQueue'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DeleteJMSTopic',
     stepName => 'DeleteJMSTopic'});
$errors .= $ec->checkAllErrors($xpath);

# AS and CF section
$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateOrUpdateWMQJMSActivationSpec',
     stepName => 'CreateOrUpdateWMQJMSActivationSpec'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateOrUpdateSIBJMSActivationSpec',
     stepName => 'CreateOrUpdateSIBJMSActivationSpec'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DeleteJMSActivationSpec',
     stepName => 'DeleteJMSActivationSpec'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateOrUpdateWMQJMSConnectionFactory',
     stepName => 'CreateOrUpdateWMQJMSConnectionFactory'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateOrUpdateSIBJMSConnectionFactory',
     stepName => 'CreateOrUpdateSIBJMSConnectionFactory'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DeleteJMSConnectionFactory',
     stepName => 'DeleteJMSConnectionFactory'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'DeleteJMSProvider',
     stepName => 'DeleteJMSProvider'});
$errors .= $ec->checkAllErrors($xpath);

$xpath = $ec->attachCredential($projName, $credName,
    {procedureName => 'CreateServer',
     stepName => 'CreateServer'});
$errors .= $ec->checkAllErrors($xpath);



# CreateDatasource
# DeleteDatasource
if ($errors ne '') {
    # Cleanup the partially created configuration we just created
    $ec->deleteProperty($configPath);
    $ec->deleteCredential($projName, $credName);
    my $errMsg = 'Error creating configuration credential: ' . $errors;
    $ec->setProperty("/myJob/configError", $errMsg);
    print $errMsg;
    exit ERROR;
}

#
#  Copyright 2016 Electric Cloud, Inc.
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

import time

appName = r'''
$[appName]
'''.strip()
appName.replace(' ', '_')

commands = r'''
$[commands]
'''.strip()

additionalCommands = r'''
$[additionalcommands]
'''.strip()

cluster = r'''
$[cluster]
'''.strip()

serverList = r'''
$[serverList]
'''.strip()

appPath = r'''
$[apppath]
'''.strip()

precompileJSP = r'''
$[precompileJSP]
'''.strip()

installDir = r'''
$[installDir]
'''.strip()

distributeApp = r'''
$[distributeApp]
'''.strip()

binaryConfig = r'''
$[binaryConfig]
'''.strip()

deployBeans = r'''
$[deployBeans]
'''.strip()

createMBeans = r'''
$[createMBeans]
'''.strip()

overrideClassReloading = r'''
$[overrideClassReloading]
'''.strip()

reloadInterval = r'''
$[reloadInterval]
'''.strip()

deployWS = r'''
$[deployWS]
'''.strip()

validateRefs = r'''
$[validateRefs]
'''.strip()

processEmbConfig = r'''
$[processEmbConfig]
'''.strip()

filePermissions = r'''
$[filePermissions]
'''.strip()

customFilePermissions = r'''
$[customFilePermissions]
'''.strip()

blaName = r'''
$[blaName]
'''.strip()

autoResolveEJBRef = r'''
$[autoResolveEJBRef]
'''.strip()

deployClientMod = r'''
$[deployClientMod]
'''.strip()

clientDeployMode = r'''
$[clientDeployMode]
'''.strip()

validateSchema = r'''
$[validateSchema]
'''.strip()

mapModulesToServers = r'''
$[MapModulesToServers]
'''.strip()

additionalDeployParams = r'''
$[additionalDeployParams]
'''.strip()

startApp = r'''
$[startApp]
'''.strip()

syncActiveNodes = r'''
$[syncActiveNodes]
'''.strip()


contextRoot = r'''
$[contextRoot]
'''.strip()

print 'Installing %s ....\n' % appName

installParams = []

def toBoolean(value):
    if value.lower() == 'true' or value == '1':
        return 1

    return 0

def append_bool(name, value):
    if toBoolean(value):
        installParams.append('-' + name)
    else:
        installParams.append('-no' + name)

def append(name, value, default_value = None, value_prefix = "", value_suffix = ""):
    if default_value and not value:
        value = default_value

    if value:
        value = value_prefix + value + value_suffix
        installParams.append('-' + name + ' ' + value)

def wsadminToList(inStr):
        inStr = inStr.rstrip();
        outList = []
        if (len(inStr) > 0 and inStr[0] == '[' and inStr[-1] == ']'):
                tmpList = inStr[1:-1].split(" ")
        else:
                tmpList = inStr.split("\n")   #splits for Windows or Linux

        for item in tmpList:
                item = item.rstrip();         #removes any Windows "\r"
                if (len(item) > 0):
                      outList.append(item)
        return outList

append_bool('preCompileJSPs', precompileJSP)
append_bool('distributeApp', distributeApp)
append_bool('useMetaDataFromBinary', binaryConfig)
append_bool('deployejb', deployBeans)
append_bool('createMBeansForResources', createMBeans)
append_bool('reloadEnabled', overrideClassReloading)
append_bool('deployws', deployWS)
append_bool('processEmbeddedConfig', processEmbConfig)
append_bool('useAutoLink', autoResolveEJBRef)

# For WAS 7.0, where validateSchema option doesn't exists.
if toBoolean(validateSchema):
    append_bool('validateSchema', validateSchema)

append('cluster', cluster)
append('installed.ear.destination', installDir)
append('appname', appName)
append('reloadInterval', reloadInterval)
append('validateinstall', validateRefs)
append('blaname', blaName)
append('MapModulesToServers', mapModulesToServers, None, '[', ']')

if contextRoot:
    append('contextroot', contextRoot)

if toBoolean(deployClientMod):
    append_bool('enableClientModule', deployClientMod)
    append('clientMode', clientDeployMode, 'isolated')

if not customFilePermissions and filePermissions:
    customFilePermissions = filePermissions

append('filepermission', customFilePermissions, '.*\.dll=755#.*\.so=755#.*\.a=755#.*\.sl=755')

servers = []

if serverList:
    targetServers = ''
    nodes_servers = serverList.split(',')

    for node_server in nodes_servers:
        (node, server) = node_server.split('=')
        serverRow = {
            'node' : node,
            'server' : server
        }
        servers.append(serverRow)

        if targetServers:
            targetServers += '+'

        targetServers += "WebSphere:node=%s,server=%s" % (node, server)

    if targetServers:
        append('target', targetServers)

deployment=AdminConfig.getid('/Deployment:%s/' % (appName))

installParamsString = ' '.join(installParams)

if additionalDeployParams:
    installParamsString += ' ' + additionalDeployParams

if deployment:
    AdminApp.update(appName, 'app', '[ -operation update -contents "%s" %s ]' % (appPath, installParamsString))
else:
    AdminApp.install(appPath, '[ %s ]' % (installParamsString))

AdminConfig.save()

# Obtain deployment manager MBean
dm = AdminControl.queryNames('type=DeploymentManager,*')

# Synchronization of configuration changes is only required in network deployment.not in standalone server environment.

if toBoolean(syncActiveNodes):
    if dm:
        print 'Synchronizing configuration repository with nodes. Please wait...'
        nodes=AdminControl.invoke(dm, "syncActiveNodes", "true")
        print 'The following nodes have been synchronized: ' + str(nodes)
    else:
        print 'Standalone server, no nodes to sync'

if deployment:
    print 'Application %s updated successfully.' % (appName)
else:
    print 'Application %s installed successfully.' % (appName)

# Check application state, if it is not started already, start it
if toBoolean(startApp):
    if AdminControl.completeObjectName('type=Application,name=' + appName + ',*') == "":
        if cluster:
            print 'Starting application %s on cluster %s.' % (appName, cluster)
            AdminApplication.startApplicationOnCluster(appName, cluster)
        elif len(servers):
            for server in servers:
                print 'Starting application %s on node %s on server %s.' % (appName, server['node'], server['server'])
                AdminApplication.startApplicationOnSingleServer(appName, server['node'], server['server'])
        else:
            # For WebSphere Base Edition
            print 'Starting application %s' % (appName)
            appmgr = AdminControl.queryNames('name=ApplicationManager,*')
            AdminControl.invoke(appmgr, 'startApplication', appName)

        print 'Application %s started successfully.' % (appName)

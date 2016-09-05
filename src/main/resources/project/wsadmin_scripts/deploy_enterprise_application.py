import time

appName='$[appName]'.replace(' ', '_')
commands='$[commands]'
additionalCommands='$[additionalcommands]'
cluster='$[cluster]'
serverList='$[serverList]'
appPath='$[apppath]'
precompileJSP='$[precompileJSP]'
installDir='$[installDir]'
distributeApp='$[distributeApp]'
binaryConfig='$[binaryConfig]'
deployBeans='$[deployBeans]'
createMBeans='$[createMBeans]'
overrideClassReloading='$[overrideClassReloading]'
reloadInterval='$[reloadInterval]'
deployWS='$[deployWS]'
validateRefs='$[validateRefs]'
processEmbConfig='$[processEmbConfig]'
filePermissions='$[filePermissions]'
customFilePermissions='$[customFilePermissions]'
blaName='$[blaName]'
autoResolveEJBRef='$[autoResolveEJBRef]'
deployClientMod='$[deployClientMod]'
clientDeployMode='$[clientDeployMode]'
validateSchema='$[validateSchema]'
mapModulesToServers='$[MapModulesToServers]'
additionalDeployParams='$[additionalDeployParams]'

print 'Installing %s ....\n' % appName

installParams = []

def toBoolean(value):
    if value.lower() == 'true' or value == '1':
        return 1
    
    return 0

def append_bool(name, value):
    if toBoolean(value):
        installParams.append('-'+name)
    else:
        installParams.append('-no'+name)
    
def append(name, value, default_value = None, value_prefix="", value_suffix = ""):
    if default_value and not value:
        value = default_value

    if value:
        value = value_prefix + value + value_suffix
        installParams.append('-'+name + ' '+value)

def wsadminToList(inStr):
        inStr = inStr.rstrip();
        outList=[]
        if (len(inStr)>0 and inStr[0]=='[' and inStr[-1]==']'):
                tmpList = inStr[1:-1].split(" ")
        else:
                tmpList = inStr.split("\n")   #splits for Windows or Linux

        for item in tmpList:
                item = item.rstrip();         #removes any Windows "\r"
                if (len(item)>0):
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
append_bool('validateSchema', validateSchema)

append('cluster', cluster)
append('installed.ear.destination', installDir)
append('appname', appName)
append('reloadInterval', reloadInterval)
append('validateinstall', validateRefs)
append('blaname', blaName)
append('MapModulesToServers', mapModulesToServers, None, '[', ']')

if toBoolean(deployClientMod):
    append_bool('enableClientModule', deployClientMod)
    append('clientMode', clientDeployMode, 'isolated')

if not customFilePermissions and filePermissions:
    customFilePermissions = filePermissions

append('filepermission', customFilePermissions, '.*\.dll=755#.*\.so=755#.*\.a=755#.*\.sl=755')

servers = {}

if serverList:
    targetServers = ''
    nodes_servers = serverList.split(',')

    for node_server in nodes_servers:
        (node, server) = node_server.split('=')
        servers[server] = node 
        
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
dm=AdminControl.queryNames('type=DeploymentManager,*')

# Synchronization of configuration changes is only required in network deployment.not in standalone server environment.
if dm:
    print 'Synchronizing configuration repository with nodes. Please wait...'
    nodes=AdminControl.invoke(dm, "syncActiveNodes", "true")
    print 'The following nodes have been synchronized:'+str(nodes)
else:
    print 'Standalone server, no nodes to sync'

result = AdminApp.isAppReady(appName)
print 'Is App Ready = ' + result
while result != 'true':
    result = AdminApp.isAppReady(appName)
    print 'Is App Ready = ' + result
    sleep(3)

if deployment:
    print 'Application  %s updated successfully.' % (appName)
else:
    if cluster:
        AdminApplication.startApplicationOnCluster(appName, cluster)
    elif len(servers):
        for server in servers.keys():
            AdminApplication.startApplicationOnSingleServer(appName, servers[server], server)
    else:
        # For WebSphere Base Edition
        appmgr = AdminControl.queryNames('name=ApplicationManager,*')
        AdminControl.invoke(appmgr, 'startApplication', appName)

    print 'Application %s started successfully.' % (appName)

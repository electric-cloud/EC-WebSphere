import sys

appName = r'''
$[appName]
'''.strip()
clusterName = r'''
$[clusterName]
'''.strip()
contentType = r'''
$[contentType]
'''.strip()
contentURI = r'''
$[contentURI]
'''.strip()
clusterName = r'''
$[clusterName]
'''.strip()
serverName = r'''
$[serverName]
'''.strip()


updateCommand = r'''
[-operation $[operation] -contents $[content]
'''.strip()
if contentURI:
    updateCommand + ' ' # just whitespace - as a delimeter
    updateCommand += r'''-contenturi $[contentURI]'''.strip()

updateCommand += ' ' # another delimeter
updateCommand += r'''$[additionalParams]]'''.strip()

result = AdminApp.update(appName, contentType, updateCommand)
print result
AdminConfig.save()
result = AdminApp.isAppReady(appName)
print 'Is App Ready = ' + result
while result != 'true':
    result = AdminApp.isAppReady(appName)
    print 'Is App Ready = ' + result
    sleep(3)
print 'The application is ready to restart.'


if clusterName:
    result = AdminClusterManagement.checkIfClusterExists(clusterName)
    if result == "false":
        print 'Error : Cluster ' + clusterName + ' does not exist.'
        sys.exit(1)

    cluster = AdminControl.completeObjectName('type=Cluster,name=' + clusterName + ',*')
    print cluster
    print "Restarting every member of cluster after application is updated."
    AdminControl.invoke(cluster, 'rippleStart')
    status = AdminControl.getAttribute(cluster, 'state')
    desiredStatus = 'websphere.cluster.running'
    print 'Cluster status = ' + status
    while 1:
        status = AdminControl.getAttribute(cluster, 'state')
        print 'Cluster status = ' + status
        if status == desiredStatus:
            break
        else:
            sleep(3)
    print 'Application is UP!'

else:

    appManager = AdminControl.queryNames('type=ApplicationManager,process=' + serverName + ',*')
    print appManager
    result = AdminControl.invoke(appManager,'stopApplication', appName)
    print result
    result = AdminControl.invoke(appManager,'startApplication', appName)
    print result
    appstatus = AdminControl.completeObjectName('type=Application,name=' + appName + ',*')
    if appstatus:
        print 'Application is UP!'
    else:
        print 'Application is not UP.'

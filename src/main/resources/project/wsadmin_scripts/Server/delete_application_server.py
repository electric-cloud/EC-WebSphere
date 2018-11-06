$[/myProject/wsadmin_scripts/preamble.py]

nodeName = '''
$[wasNodeName]
'''.strip()
appServerName = '''
$[wasAppServerName]
'''.strip()
syncNodes = '''
$[wasSyncNodes]
'''.strip()

if not isServerExists(nodeName, appServerName):
    bailOut("Server %s:%s does not exist", % (nodeName, serverName))
    
serverStatus = showServerStatus(nodeName, appServerName)
if serverStatus == '':
    bailOut("Server %s:%s is in undefined state and can't be deleted" % (nodeName, appServerName))
if serverStatus != 'Stopped':
    bailOut("Server %s:%s can't be deleted because it is running.\nDeletion of running server may damage websphere instance and leads to undefined behaviour.\nPlease, stop your server first." % (nodeName, appServerName))

try:
    AdminServerManagement.deleteServer(nodeName, appServerName)
except:
    forwardException(getExceptionMsg())
    logSummary("Failed to delete application server %s on node %s" % (appServerName, nodeName));
    sys.exit(1)

logSummary("Application server %s on node %s has been deleted" % (appServerName, nodeName))
AdminConfig.save()

if toBoolean(syncNodes):
    syncActiveNodes()


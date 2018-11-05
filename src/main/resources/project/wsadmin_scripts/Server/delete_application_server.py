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

serverStatus = showServerStatus(nodeName, appServerName)
if serverStatus != 'Stopped':
    bailOut("Server %s:%s can't be deleted because it is running.\nDeletion of running server may damage websphere instance and leads to undefined behaviour.\nPlease, stop your server first." % (nodeName, appServerName))
    sys.exit(1)

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


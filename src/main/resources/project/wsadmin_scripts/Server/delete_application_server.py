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

try:
    AdminServerManagement.deleteServer(nodeName, appServerName)
except Exception as e:
    forwardException(str(e))
    logSummary("Failed to delete server %s on node %s" % (appServerName, nodeName));
    sys.exit(1)

logSummary("Server %s on node %s has been deleted" % (appServerName, nodeName))
AdminConfig.save()

if toBoolean(syncNodes):
    syncActiveNodes()


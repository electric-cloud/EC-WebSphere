$[/myProject/wsadmin_scripts/preamble.py]

clusterName = '''
$[wasClusterName]
'''.strip()

syncNodes = '''
$[wasSyncNodes]
'''.strip()
try:
    AdminTask.deleteCluster(['-clusterName', clusterName])
except:
    forwardException(getExceptionMsg())
    sys.exit(1)

logSummary("Cluster %s has been deleted" % (clusterName))
AdminConfig.save()

if toBoolean(syncNodes):
    syncActiveNodes()


$[/myProject/wsadmin_scripts/preamble.py]

clusterName = '''
$[wasClusterName]
'''.strip()

syncNodes = '''
$[wasSyncNodes]
'''.strip()

if not isClusterExists(clusterName):
    bailOut("Cluster %s does not exist" % (clusterName))

# Using websphere UI we can't delete running cluster. But using jython it is possible.
# During testing we've found that it leads to undefined behaviour and may damage websphere instance.
# So, we will abort this job if cluster is not stopped.
clusterState = getClusterState(clusterName)
if clusterState != 'websphere.cluster.stopped':
    bailOut("Cluster %s can't be deleted because it is running.\nDeletion of running cluster may damage websphere instance and leads to undefined behaviour.\nPlease, stop your cluster first." % (clusterName))

try:
    AdminTask.deleteCluster(['-clusterName', clusterName])
except:
    forwardException(getExceptionMsg())
    sys.exit(1)

logSummary("Cluster %s has been deleted" % (clusterName))
AdminConfig.save()

if toBoolean(syncNodes):
    syncActiveNodes()


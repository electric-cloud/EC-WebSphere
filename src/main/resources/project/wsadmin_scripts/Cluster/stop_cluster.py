$[/myProject/wsadmin_scripts/preamble.py]

clusterName = r'''
$[wasClusterName]
'''.strip()

timeout = r'''
$[wasTimeout]
'''.strip()
timeout = uintOrZero(timeout)

rippleStart = r'''
$[wasRippleStart]
'''.strip()

desiredClusterState = 'websphere.cluster.stopped'

clusterMgr = getClusterManager()

if not clusterMgr:
    logError("No cluster manager available, please, check your WebSphere environment")
    sys.exit(1)

cluster = getClusterObject(clusterName)

if not cluster:
    suggestion = getClusterNotFoundSuggestion()
    logSummary('Failed to start cluster %s.\nCluster %s was not found.\nTip: %s' % (clusterName, clusterName, suggestion))
    sys.exit(1)

if toBoolean(rippleStart):
    AdminControl.invoke(cluster, 'rippleStart')
    desiredClusterState = 'websphere.cluster.running'
else:
    AdminControl.invoke(cluster, 'stop')

result = waitForClusterStatus(desiredClusterState, cluster, timeout )

if result:
    if toBoolean(rippleStart):
        logSummary("Cluster %s has been restarted using RippleStart" % (clusterName))
    else:
        logSummary("Cluster %s has been stopped" % (clusterName))
    os._exit(0)
else:
    logError("Cluster %s has not been stopped, exited by timeout" % (clusterName))
    membersStatus(clusterName)
    sys.exit(1)

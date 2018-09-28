$[/myProject/wsadmin_scripts/preamble.py]

clusterName = r'''
$[wasClusterName]
'''.strip()

timeout = r'''
$[wasTimeout]
'''.strip()
timeout = uintOrZero(timeout)

desiredClusterState = 'websphere.cluster.running'


clusterMgr = getClusterManager()

if not clusterMgr:
    logError("No cluster manager available, please, check your WebSphere environment")
    sys.exit(1)

cluster = getClusterObject(clusterName)

if not cluster:
    suggestion = getClusterNotFoundSuggestion()
    logSummary('Failed to start cluster %s.\nCluster %s was not found.\nTip: %s' % (clusterName, clusterName, suggestion))
    sys.exit(1)


AdminControl.invoke(cluster, 'start')
result = waitForClusterStatus(desiredClusterState, cluster, timeout)

if result:
    logSummary("Cluster %s has been started" % (clusterName))
    os._exit(0)
else:
    logSummary("Cluster was not started, exited by timeout")
    membersStatus(clusterName)
    sys.exit(1)

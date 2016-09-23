import time

clusterName = '$[clusterName]'
cellName = '$[cellName]'
timeout = $[clusterCommandTimeout]

clusterMgr = AdminControl.completeObjectName('cell=' + cellName + ',type=ClusterMgr,*')

if not clusterMgr:
    print 'No ClusterMgr found for cell %s' % ( cellName )
    sys.exit(1)

cluster = AdminControl.completeObjectName('cell=' + cellName + ',type=Cluster,name=' + clusterName + ',*')

if not cluster:
    print 'No cluster found by name %s' % ( clusterName )
    sys.exit(1)

AdminControl.invoke(cluster, 'stop')

def waitForClusterStatus( status, cluster, timeout = 15 ):
    run_index = 0
    while( run_index < timeout ):
        run_index += 1
        time.sleep(1)
        clusterStatus = AdminControl.getAttribute(cluster, "state" )
        if clusterStatus == status:
            return 1
    return 0


def membersStatus(clusterName):
    clusterId = AdminConfig.getid('/ServerCluster:' + clusterName + '/')
    clusterList = AdminConfig.list('ClusterMember', clusterId)
    
    servers = clusterList.split()
    
    allStopped = 1

    for serverId in servers:
        serverName = AdminConfig.showAttribute(serverId, "memberName")
        server = AdminControl.completeObjectName("type=Server,name=" + serverName + ",*")
        if server != "":
            allStopped = 0
            state = AdminControl.getAttribute(server, "state")
            print "Server " + serverName + " is in " + state + " state "
               
    return allStopped

result = waitForClusterStatus('websphere.cluster.stopped', cluster, timeout )

if result:
    print "Cluster stopped"
    sys.exit(0)
else:
    print "Cluster was not stopped, exited by timeout"
    membersStatus(clusterName)
    sys.exit(1)

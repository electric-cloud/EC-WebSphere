#
#  Copyright 2016 Electric Cloud, Inc.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

import time
import os

clusterName = r'''
$[clusterName]
'''.strip()

cellName = r'''
$[cellName]
'''.strip()

timeout = r'''
$[clusterCommandTimeout]
'''.strip()

timeout = int(timeout)

clusterMgr = AdminControl.completeObjectName('cell=' + cellName + ',type=ClusterMgr,*')

if not clusterMgr:
    print 'No ClusterMgr found for cell %s' % ( cellName )
    sys.exit(1)

cluster = AdminControl.completeObjectName('cell=' + cellName + ',type=Cluster,name=' + clusterName + ',*')

if not cluster:
    print 'No cluster found by name %s' % ( clusterName )
    sys.exit(1)


AdminControl.invoke(cluster, 'start')

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
    allStarted = 1
    
    for serverId in servers:
        serverName = AdminConfig.showAttribute(serverId, "memberName")
        server = AdminControl.completeObjectName("type=Server,name=" + serverName + ",*")
        if server == "":
            allStarted = 0
        else:
            state = AdminControl.getAttribute(server, "state")
            if state != "STARTED":
                allStarted = 0
                print "Server " + serverName + " is in " + state + " state "
    return allStarted

result = waitForClusterStatus( "websphere.cluster.running", cluster, timeout )

if result:
    print "Cluster started"
    os._exit(0)
else:
    print "Cluster was not started, exited by timeout"
    membersStatus(clusterName)
    sys.exit(1)

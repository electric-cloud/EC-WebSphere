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

import sys
myNode = r'''
$[nodeName]
'''.strip()

# This will be one of three: ALL_RUNNING, ALL_STOPPED, NODEAGENT_RUNNING
successCriteria = r'''
$[successCriteria]
'''.strip()

def showServerStatus(serverName, nodeName):
    serverObj = AdminControl.completeObjectName('WebSphere:type=Server,node=' + nodeName + ',name=' + serverName + ',*')
    if len(serverObj) > 0:
        serverStatus = AdminControl.getAttribute(serverObj, 'state')
    else:
        serverStatus = 'STOPPED'

    return serverStatus

def checkServerByCriteria(serverName, serverStatus, successCriteria):
    if successCriteria == 'ALL_RUNNING':
        if serverStatus != 'STARTED':
            return 0
    elif successCriteria == 'ALL_STOPPED':
        if serverStatus == 'STARTED':
            return 0
    else:
        if serverName == 'nodeagent' and serverStatus != 'STARTED':
            return 0
    # end of conditions
    return 1


nodeFound = 0
errorsCount = 0

for node in AdminConfig.list('Node').splitlines() :
    nodeName = AdminConfig.showAttribute(node, 'name')
    if myNode != nodeName :
        continue
    nodeFound = 1
    print '\n\n\n'
    for server in AdminConfig.list('Server', node).splitlines() :
        serverName = AdminConfig.showAttribute(server, 'name')
        serverStatus = showServerStatus(serverName, nodeName)
        # if serverStatus != 'STARTED':
        if not checkServerByCriteria(serverName, serverStatus, successCriteria):
            errorsCount += 1
        print '  Node: %s\nServer: %s\nStatus: %s\n' % (nodeName, serverName, serverStatus)
        print '==========\n'

if not nodeFound:
    print 'Node with name %s wasn\'t found' % myNode
    sys.exit(1)

if errorsCount > 0:
    print "Success Criteria wasn't met."
    sys.exit(1)

print "Success Criteria was met."

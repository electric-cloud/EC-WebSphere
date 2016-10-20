import sys
myNode = r'''
$[nodeName]
'''.strip()

def showServerStatus(serverName):
    serverObj = AdminControl.completeObjectName('WebSphere:type=Server,name=' + serverName + ',*')
    if len(serverObj) > 0:
        serverStatus = AdminControl.getAttribute(serverObj, 'state')
    else:
        serverStatus = 'STOPPED'

    return serverStatus


node_found = 0
errors_count = 0

for node in AdminConfig.list( 'Node' ).splitlines() :
    nodeName = AdminConfig.showAttribute( node, 'name' )
    if myNode != nodeName :
        continue
    node_found = 1
    print '\n\n\n'
    for server in AdminConfig.list( 'Server', node ).splitlines() :
        servName = AdminConfig.showAttribute( server, 'name' )
        serverStatus = showServerStatus(servName)
        if serverStatus != 'STARTED':
            errors_count += 1
        print '  Node: %s\nServer: %s\nStatus: %s\n' % ( nodeName, servName, serverStatus )
        print '==========\n'

if not node_found:
    print 'Node with name %s wasn\'t found' % myNode
    sys.exit(1)

if errors_count > 0:
    print "Node is not running. Please, check output"
    sys.exit(1)



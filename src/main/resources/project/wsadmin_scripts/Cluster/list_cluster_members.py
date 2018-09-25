$[/myProject/wsadmin_scripts/preamble.py]

clusterName = '''
$[wasClusterName]
'''.strip()

if not isClusterExists(clusterName):
    logError("Cluster %s does not exist" % (clusterName))
    sys.exit(1)

clusterMembers = getClusterMembers(clusterName)
if len(clusterMembers) == 0:
    logSummary("Cluster %s is empty" % (clusterName))
    os._exit(0)

# cluster is not empty. Adding pretty summary
logSummary("Cluster %s has following members:" % (clusterName))

memberPropData = []
for member in clusterMembers:
    serverName = AdminConfig.showAttribute(member, "memberName")
    nodeName = AdminConfig.showAttribute(member, "nodeName")
    memberPropData.append('%s:%s' % (nodeName, serverName))
    logSummary("Server %s on node %s" % (serverName, nodeName))

setProperty('/myJob/members', ','.join(memberPropData))


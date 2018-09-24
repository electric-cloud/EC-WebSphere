$[/myProject/wsadmin_scripts/preamble.py]

clusterName = '''
$[wasClusterName]
'''.strip()
clusterMembers = '''
$[wasClusterMembers]
'''.strip()
syncNodes = '''
$[wasSyncNodes]
'''.strip()

if not AdminClusterManagement.checkIfClusterExists(clusterName):
    logError("Cluster %s does not exist" % (clusterName))
    sys.exit(1)

# Cluster exists, continue
parsedMembersList = parseServerListAsList(clusterMembers, {'filterUnique': 1})

for member in parsedMembersList:
    try:
        AdminClusterManagement.deleteClusterMember(clusterName, member['Node'], member['Server'])
        logSummary("Cluster member %s on node %s has been removed from cluster %s and deleted" % (member['Server'], member['Node'], clusterName))
    except:
        forwardException(getExceptionMsg())
        sys.exit(1)

AdminConfig.save()
if toBoolean(syncNodes):
    syncActiveNodes()

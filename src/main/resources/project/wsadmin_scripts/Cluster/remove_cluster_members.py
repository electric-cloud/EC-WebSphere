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

if not isClusterExists(clusterName):
    bailOut("Cluster %s does not exist" % (clusterName))
    sys.exit(1)

# Cluster exists, continue
parsedMembersList = parseServerListAsList(clusterMembers, {'filterUnique': 1})

# check members
clusterMembers = getClusterMembersAsList(clusterName);
errors = 0
for member in parsedMembersList:
    if member not in clusterMembers:
        exists = 'exists'
        errors = 1
        if not isServerExists(member['Node'], member['Server']):
            exists = 'does not exist'
            
        logError("Server %s:%s (%s) is not a member of cluster %s, please, check your input" % (member['Node'], member['Server'], exists, clusterName))

if errors > 0:
    sys.exit(1)

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

$[/myProject/wsadmin_scripts/preamble.py]

clusterName = '''
$[wasClusterName]
'''.strip()

genUniquePorts = '''
$[wasClusterMembersGenUniquePorts]
'''.strip()

clusterMemberWeight = '''
$[wasClusterMemberWeight]
'''.strip()

membersList = '''
$[wasClusterMembersList]
'''.strip()

syncNodes = '''
$[wasSyncNodes]
'''.strip()

# Check if cluster is empty. Cluster members can't be created if
print "Checking cluster for existance"
if not isClusterExists(clusterName):
    bailOut("Cluster %s does not exist" % (clusterName))

print "Checking cluster for emptiness"
if len(getClusterMembers(clusterName)) < 1:
    bailOut("Can't add cluster members to empty cluster %s. Please, create first cluster member and try again" % (clusterName))

parsedMembersList = []
try:
    parsedMembersList = parseServerListAsList(membersList, {'filterUnique': 1})
except:
    forwardException(getExceptionMsg())
    sys.exit(1)

for server in parsedMembersList:
    try:
        createMemberParams = {
            'clusterName': clusterName,
            'targetNode': server['Node'],
            'targetName': server['Server'],
            'memberWeight': clusterMemberWeight,
            'genUniquePorts': genUniquePorts
        }
        createClusterMembers(createMemberParams)
    except:
        forwardException(getExceptionMsg())
        sys.exit(1)
    logSummary("Server %s on node %s has been created and added to %s cluster" % (server['Server'], server['Node'], clusterName))

AdminConfig.save()

if toBoolean(syncNodes):
    syncActiveNodes()

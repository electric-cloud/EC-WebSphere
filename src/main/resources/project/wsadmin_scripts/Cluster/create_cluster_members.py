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
            'memberWeight': clusterMemberWeight
        }
        createClusterMembers(createMemberParams)
    except:
        forwardException(getExceptionMsg())
        sys.exit(1)
    logSummary("Server %s on node %s has been created and added to %s cluster" % (server['Server'], server['Node'], clusterName))

AdminConfig.save()

if toBoolean(syncNodes):
    syncActiveNodes()

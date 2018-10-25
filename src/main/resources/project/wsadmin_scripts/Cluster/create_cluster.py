$[/myProject/wsadmin_scripts/preamble.py]

clusterName = '''
$[wasClusterName]
'''.strip()

preferLocal = '''
$[wasPreferLocal]
'''.strip()

createFirstMember = '''
$[wasCreateFirstClusterMember]
'''.strip()

firstMemberNode = '''
$[wasFirstClusterMemberNode]
'''.strip()

firstMemberName = '''
$[wasFirstClusterMemberName]
'''.strip()

firstMemberWeight = '''
$[wasFirstClusterMemberWeight]
'''.strip()

firstMemberGenUniquePorts = '''
$[wasFirstClusterMemberGenUniquePorts]
'''.strip()

promotionPolicy = '''
$[wasServerResourcesPromotionPolicy]
'''.strip()

creationPolicy = '''
$[wasFirstClusterMemberCreationPolicy]
'''.strip()

templateName = '''
$[wasFirstClusterMemberTemplateName]
'''.strip()

sourceServerName = '''
$[wasSourceServerName]
'''.strip()

addClusterMembers = '''
$[wasAddClusterMembers]
'''.strip()

membersGenUniquePorts = '''
$[wasClusterMembersGenUniquePorts]
'''.strip()

memberWeight = '''
$[wasClusterMemberWeight]
'''.strip()

membersList = '''
$[wasClusterMembersList]
'''.strip()

syncNodes = '''
$[wasSyncNodes]
'''.strip()


# 1. parameters check
# Checking all parameters if create 1st member is set
if toBoolean(createFirstMember):
    if not creationPolicy:
        bailOut("Creation Policy is mandatory when create 1st cluster member is chosen")
    if creationPolicy not in ['existing', 'convert', 'template']:
        bailOut("Creation policy should be one of: existing, convert or template. Got %s" % (creationPolicy))
    if creationPolicy in ['template', 'existing'] and (not firstMemberName or not firstMemberNode):
        bailOut("First Member Name and First Member Node should be provided when create 1st cluster member is chosen")
    if not promotionPolicy:
        bailOut("Promotion Policy is mandatory when create first cluster member is chosen");
    if promotionPolicy and promotionPolicy not in ['cluster', 'server', 'both']:
        bailOut("Promotion policy should be cluster, server, or both, got %s" % (promotionPolicy))

# 2. Create cluster
if toBoolean(preferLocal):
    preferLocal = 'true'
else:
    preferLocal = 'false'

parsedMembersList = []
if toBoolean(addClusterMembers):
    if not membersList:
        bailOut("No members to add")
    try:
        parsedMembersList = parseServerListAsList(membersList, {'filterUnique': 1})
    except:
        forwardException(getExceptionMsg())
        sys.exit(1)
    
clusterConfig = "[-clusterName '%s' -preferLocal '%s' -clusterType APPLICATION_SERVER]" % (clusterName, preferLocal)

clusterCreationParams = [
    '-clusterConfig', clusterConfig
]

# now we're adding parameters if we want to convert server to be a first cluster member.
okConvertedLog = ''
if toBoolean(createFirstMember) and creationPolicy == 'convert':
    nodeServer = splitNodeServer(sourceServerName)
    clusterCreationParams.append('-convertServer')
    convertParams = "[-serverName '%s' -serverNode '%s' -resourcesScope '%s']" % (nodeServer['Server'], nodeServer['Node'], promotionPolicy)
    clusterCreationParams.append(convertParams)
    okConvertedLog = "Server %s on node %s has been converted to be the first member of cluster %s" % (nodeServer['Server'], nodeServer['Node'], clusterName)

print clusterCreationParams
try:
    AdminTask.createCluster(clusterCreationParams)
except:
    forwardException(getExceptionMsg())
    sys.exit(1)

logSummary("Cluster %s has been created" % (clusterName))

if creationPolicy == 'convert':
    logSummary(okConvertedLog)


# 3. Add first cluster member
# TODO: Add weights
if toBoolean(createFirstMember) and creationPolicy in ['template', 'existing']:
    logInfo("Creating first member from template or existing server")
    try:
        createFirstMemberParams = {
            'clusterName'   : clusterName,
            'creationPolicy': creationPolicy,
            'targetNode'    : firstMemberNode,
            'targetServer'  : firstMemberName,
            'templateName'  : templateName,
            'memberWeight'  : firstMemberWeight,
            'resourcesScope': promotionPolicy,
        }
        if toBoolean(firstMemberGenUniquePorts):
            createFirstMemberParams['genUniquePorts'] = 'true'
        else:
            createFirstMemberParams['genUniquePorts'] = 'false'

        if creationPolicy == 'existing':
            nodeServer = splitNodeServer(sourceServerName)
            createFirstMemberParams['sourceNode'] = nodeServer['Node']
            createFirstMemberParams['sourceServer'] = nodeServer['Server']
        createFirstClusterMember(createFirstMemberParams)
    except:
        forwardException(getExceptionMsg())
        sys.exit(1)
    if creationPolicy == 'template':
        logSummary("First cluster member %s has been created on node %s from template %s" % (firstMemberName, firstMemberNode, templateName))
    else:
        logLine = "First cluster member %s has been created on node %s using server %s on node %s as source" \
            % (firstMemberName, firstMemberNode, nodeServer['Server'], nodeServer['Node'])
        logSummary(logLine)
# 4. Add cluster members
if toBoolean(addClusterMembers):
    for server in parsedMembersList:
        try:
            createMemberParams = {
                'clusterName': clusterName,
                'targetNode': server['Node'],
                'targetName': server['Server'],
                'memberWeight': memberWeight,
                'genUniquePorts': membersGenUniquePorts
            }
            # if toBooleanString(membersGenUniquePorts) in ['true', 'false']:
            #     createMemberParams['genUniquePorts'] = toBooleanString(membersGenUniquePorts)

            createClusterMembers(createMemberParams)
        except:
            forwardException(getExceptionMsg())
            sys.exit(1)
        logSummary("Server %s on node %s has been created and added as cluster member" % (server['Server'], server['Node']))

AdminConfig.save()

if toBoolean(syncNodes):
    syncActiveNodes()

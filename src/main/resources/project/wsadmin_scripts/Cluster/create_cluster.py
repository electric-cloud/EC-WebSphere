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
    # TODO: Check promotion policy
# 2. Create cluster
if toBoolean(preferLocal):
    preferLocal = 'true'
else:
    preferLocal = 'false'


clusterConfig = "[-clusterName '%s' -preferLocal '%s' -clusterType APPLICATION_SERVER]" % (clusterName, preferLocal)

clusterCreationParams = [
    '-clusterConfig', clusterConfig
]

# now we're adding parameters if we want to convert server to be a first cluster member.
if creationPolicy == 'convert':
    nodeServer = splitNodeServer(sourceServerName)
    clusterCreationParams.append('-convertServer')
    convertParams = "[-serverName '%s' -serverNode '%s' -resourcesScope '%s']" % (nodeServer['Server'], nodeServer['Node'], promotionPolicy)
    clusterCreationParams.append(convertParams)

print clusterCreationParams
try:
    AdminTask.createCluster(clusterCreationParams)
except:
    forwardException(getExceptionMsg())
    sys.exit(1)

logSummary("Cluster %s has been created" % (clusterName))
# AdminConfig.save()

# 3. Add cluster members
# TODO: Add weights
if creationPolicy == 'template':
    try:
        createFirstClusterMember({
            'clusterName': clusterName,
            'creationPolicy': 'template',
            'templateName': templateName,
            'targetNode': firstMemberNode,
            'targetServer': firstMemberName
        })
    except:
        forwardException(getExceptionMsg())
        sys.exit(1)
elif creationPolicy == 'existing':
    try:
        nodeServer = splitNodeServer(sourceServerName)
        createFirstClusterMember({
            'clusterName': clusterName,
            'creationPolicy': 'existing',
            'targetNode': firstMemberNode,
            'targetServer': firstMemberName,
            'sourceNode': nodeServer['Node'],
            'sourceServer': nodeServer['Server']
        })
    except:
        forwardException(getExceptionMsg())
        sys.exit(1)

AdminConfig.save()
if toBoolean(syncNodes):
    syncActiveNodes()

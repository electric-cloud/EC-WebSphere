$[/myProject/wsadmin_scripts/preamble.py]

clusterName = '''
$[wasClusterName]
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

syncNodes = '''
$[wasSyncNodes]
'''.strip()

nodeServer = {'Node': '', 'Server': ''}

if creationPolicy == 'existing' and  sourceServerName:
    try:
        nodeServer = splitNodeServer(sourceServerName)
    except:
        forwardException(getExceptionMsg())
        sys.exit(1)

createFirstMemberParams = {
    'clusterName'   : clusterName,
    'creationPolicy': creationPolicy,
    'targetNode'    : firstMemberNode,
    'targetServer'  : firstMemberName,
    'templateName'  : templateName,
    'memberWeight'  : firstMemberWeight,
    'resourcesScope': promotionPolicy,
    'genUniquePorts': firstMemberGenUniquePorts,
    'sourceNode'    : nodeServer['Node'],
    'sourceServer'  : nodeServer['Server']
}

try:
    createFirstClusterMember(createFirstMemberParams)
except:
    forwardException(getExceptionMsg())
    sys.exit(1)

if creationPolicy == 'existing':
    logSummary("First cluster member %s has been created on %s node and added to cluster %s using server %s as template" % (firstMemberName, firstMemberNode, clusterName, sourceServerName))
else:
    logSummary("First cluster member %s has been created on %s node and added to cluster %s using %s template" % (firstMemberName, firstMemberNode, clusterName, templateName))

AdminConfig.save()

if toBoolean(syncNodes):
    syncActiveNodes()

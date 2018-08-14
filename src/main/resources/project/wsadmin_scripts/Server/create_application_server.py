$[/myProject/wsadmin_scripts/preamble.py]

# properties from procedure section

nodeName = '''
$[wasNodeName]
'''.strip()

serverName = '''
$[wasAppServerName]
'''.strip()

sourceType = '''
$[wasSourceType]
'''.strip()

templateName = '''
$[wasTemplateName]
'''.strip()

templateLocation = '''
$[wasTemplateLocation]
'''.strip()

sourceServerName = '''
$[wasSourceServerName]
'''.strip()

genUniquePorts = '''
$[wasGenUniquePorts]
'''.strip()

syncNodes = '''
$[wasSyncNodes]
'''.strip()

if sourceType and sourceType not in ['server', 'template']:
    logError("Source type %s is not valid, server or template expected" % (sourceType))
    sys.exit(1)

createdTemplateName = ''
# 1. Determine a way of server creation.
if sourceType == 'server':
    createdTemplateName = genUUID()
    if not sourceServerName:
        logError("Source server name is required when source type is set to server")
        sys.exit(1)
    # TODO: move this to separate function
    server = re.split(':', sourceServerName)
    # create intermediate template
    try:
        AdminServerManagement.createAppServerTemplate(server[0], server[1], createdTemplateName)
    except:
        logSummary("Failed to create intermediate template %s" % (createdTemplateName))
        forwardException(getExceptionMsg())
        sys.exit(1)
        # create application server using this template
    templateName = createdTemplateName

creationParams = [
    '-name', serverName
]

if templateName:
    creationParams.append('-templateName')
    creationParams.append(templateName)
if templateLocation:
    creationParams.append('-templateLocation')
    creationParams.append(templateLocation)
if genUniquePorts:
    creationParams.append('-genUniquePorts')
    if toBoolean(genUniquePorts):
        creationParams.append('true')
    else:
        creationParams.append('false')

# Server creation
try:
    AdminTask.createApplicationServer(nodeName, creationParams)
except:
    logSummary("Failed to create %s server on %s node" % (serverName, nodeName))
    forwardException(getExceptionMsg())
    if createdTemplateName:
        AdminServerManagement.deleteServerTemplate(createdTemplateName)
    sys.exit(1)

if createdTemplateName:
    AdminServerManagement.deleteServerTemplate(createdTemplateName)

logSummary("Application server %s has been created on node %s" % (serverName, nodeName))
AdminConfig.save()
if toBoolean(syncNodes):
    syncActiveNodes()

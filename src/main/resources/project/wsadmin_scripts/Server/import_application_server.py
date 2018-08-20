$[/myProject/wsadmin_scripts/preamble.py]

# Mandatory parameters
nodeName = '''
$[wasNodeName]
'''.strip()

appServerName = '''
$[wasAppServerName]
'''.strip()

archivePath = '''
$[wasArchivePath]
'''.strip()

# Optional parameters
nodeNameInArchive = '''
$[wasNodeNameInArchive]
'''.strip()

appServerNameInArchive = '''
$[wasAppServerNameInArchive]
'''.strip()

coreGroup = '''
$[wasCoreGroup]
'''.strip()

wasSyncNodes = '''
$[wasSyncNodes]
'''.strip()

params = [
    '-nodeName', nodeName,
    '-serverName', appServerName,
    '-archive', archivePath
]

if nodeNameInArchive:
    params.append('-nodeInArchive')
    params.append(nodeNameInArchive)

if appServerNameInArchive:
    params.append('-serverInArchive')
    params.append(appServerNameInArchive)

if coreGroup:
    params.append('-coreGroup')
    params.append(coreGroup)

try:
    AdminTask.importServer(params)
except:
    logSummary("Failed to import application server %s to node %s" % (appServerName, nodeName))
    forwardException(getExceptionMsg())
    sys.exit(1)

AdminConfig.save()
logSummary("Application server %s has been imported to node %s" % (appServerName, nodeName))

# sync nodes if required
if toBoolean(wasSyncNodes):
    syncActiveNodes()

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
    params.appname(coreGroup)

AdminTask.importServer(params)

AdminConfig.save()

# sync nodes if required
if toBoolean(wasSyncNodes):
    syncActiveNodes()

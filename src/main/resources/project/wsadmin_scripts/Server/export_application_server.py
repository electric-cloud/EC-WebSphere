$[/myProject/wsadmin_scripts/preamble.py]

nodeName = '''
$[wasNodeName]
'''.strip()

serverName = '''
$[wasAppServerName]
'''.strip()

archivePath = '''
$[wasArchivePath]
'''.strip()

params = [
    '-nodeName', nodeName,
    '-serverName', serverName,
    '-archive', archivePath
]
try:
    AdminTask.exportServer(params)
except:
    logSummary("Failed to export application server %s from node %s" % (serverName, nodeName))
    forwardException(getExceptionMsg())
    sys.exit(1)

logSummary("Application server %s from node %s has been exported" % (serverName, nodeName));
AdminConfig.save()

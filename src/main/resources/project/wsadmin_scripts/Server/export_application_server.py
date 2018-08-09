$[/myProject/wsadmin_scripts/preamble.py]

nodeName = '''
$[wasNodeName]
'''.strip()

appServerName = '''
$[wasAppServerName]
'''.strip()

archivePath = '''
$[wasArchivePath]
'''.strip()

params = [
    '-nodeName', nodeName,
    '-serverName', appServerName,
    '-archive', archivePath
]
AdminTask.exportServer(params)

AdminConfig.save()

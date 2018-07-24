$[/myProject/wsadmin_scripts/preamble.py]

nodeName = '''
$[/myJobStep/tmpl/nodeName]
'''.strip()
appServerName = '''
$[/myJobStep/tmpl/appServerName]
'''.strip()

AdminServerManagement.deleteServer(nodeName, appServerName)

AdminConfig.save()

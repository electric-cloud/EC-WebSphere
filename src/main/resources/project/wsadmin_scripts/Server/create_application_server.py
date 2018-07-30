$[/myProject/wsadmin_scripts/preamble.py]

nodeName = '''
$[/myJobStep/tmpl/nodeName]
'''.strip()

appServerName = '''
$[/myJobStep/tmpl/appServerName]
'''.strip()

templateName = '''
$[/myJobStep/tmpl/templateName]
'''.strip()

AdminServerManagement.createAppServerTemplate(nodeName, appServerName, templateName)

AdminConfig.save()

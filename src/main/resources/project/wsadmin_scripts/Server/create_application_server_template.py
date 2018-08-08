$[/myProject/wsadmin_scripts/preamble.py]

nodeName = '''
$[wasNodeName]
'''.strip()

appServerName = '''
$[wasAppServerName]
'''.strip()

templateName = '''
$[wasTemplateName]
'''.strip()

templateLocation = '''
$[wasTemplateLocation]
'''.strip()

templateDescription = '''
$[wasTemplateDescription]
'''.strip()

params = [
    '-templateName', templateName,
    '-serverName', appServerName,
    '-nodeName', nodeName
]

if templateDescription:
    params.append('-description')
    params.append(templateDescription)

if templateLocation:
    params.append('-templateLocation')
    params.append(templateLocation)

# AdminServerManagement.createAppServerTemplate(nodeName, appServerName, templateName)
AdminTask.createApplicationServerTemplate(params)

AdminConfig.save()

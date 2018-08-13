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

wasSyncNodes = '''
$[wasSyncNodes]
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

try:
    AdminTask.createApplicationServerTemplate(params)
except:
    logSummary("Failed to create Application Server Template %s" % (templateName))
    forwardException(getExceptionMsg())
    sys.exit(1)

logSummary("Application Server template %s has been created" % (templateName))
AdminConfig.save()

wasSyncNodes = '''
$[wasSyncNodes]
'''.strip()

$[/myProject/wsadmin_scripts/preamble.py]

templateName = '''
$[wasTemplateName]
'''.strip()

try:
    AdminServerManagement.deleteServerTemplate(templateName)
except:
    logSummary("Failed to delete application server template %s" % (templateName));
    forwardException(getExceptionMsg());
    sys.exit(1)

logSummary("Application server template %s has been deleted" % (templateName))
AdminConfig.save()

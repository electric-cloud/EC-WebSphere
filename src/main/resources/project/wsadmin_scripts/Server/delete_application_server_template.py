$[/myProject/wsadmin_scripts/preamble.py]

templateName = '''
$[wasTemplateName]
'''.strip()

AdminServerManagement.deleteServerTemplate(templateName)

AdminConfig.save()

$[/myProject/wsadmin_scripts/preamble.py]

templateName = '''
$[/myJobStep/tmpl/templateName]
'''.strip()

AdminServerManagement.deleteServerTemplate(templateName)

AdminConfig.save()

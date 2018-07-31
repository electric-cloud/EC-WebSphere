$[/myProject/wsadmin_scripts/preamble.py]

exportParams = '''
$[/myJobStep/tmpl/exportParams]
'''.strip()

AdminTask.exportServer(exportParams)

AdminConfig.save()

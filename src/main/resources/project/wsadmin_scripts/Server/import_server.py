$[/myProject/wsadmin_scripts/preamble.py]

importParams = '''
$[/myJobStep/tmpl/importParams]
'''.strip()

AdminTask.importServer(importParams)

AdminConfig.save()

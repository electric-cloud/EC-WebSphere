$[/myProject/wsadmin_scripts/preamble.py]

scope = '''
$[/myJobStep/tmpl/providerScope]
'''.strip()

providerName = '''
$[providerName]
'''.strip()

### checking resource
result = getJMSProviderAtScope(providerName, scope)

deleteJMSProvider(result)

AdminConfig.save()

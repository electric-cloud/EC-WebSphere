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

print "Status: OK, Message: JMS Provider %s has been deleted" % (providerName)
AdminConfig.save()

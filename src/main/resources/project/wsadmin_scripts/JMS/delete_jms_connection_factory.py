$[/myProject/wsadmin_scripts/preamble.py]

scope = '''
$[/myJobStep/tmpl/factoryScope]
'''.strip()

# this parameter is responsible for flow
api = '''
$[/myJobStep/tmpl/wasApi]
'''.strip()

resName = '''
$[factoryAdministrativeName]
'''.strip()

### checking resource
resourceScope = isResourceExists(scope, api, resName)
if resourceScope:
    print "Resource: %s exists. Deleting." %(resourceScope)
    if api == 'WMQ_ConnectionFactory':
        actionResult = AdminTask.deleteWMQConnectionFactory(resourceScope)
        print "Status: OK, Message: WMQ JMS Connection Factory %s has been deleted" % (resName)
    elif api == 'SIB_ConnectionFactory':
        actionResult = AdminTask.deleteSIBJMSConnectionFactory(resourceScope)
        print "Status: OK, Message: SIB JMS Connection Factory %s has been deleted" % (resName)
else:
    print "Resource %s with type %s does not exist, can't delete" % (resName, api)
    sys.exit(1)

AdminConfig.save()

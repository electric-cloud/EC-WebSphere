$[/myProject/wsadmin_scripts/preamble.py]

scope = '''
$[/myJobStep/tmpl/factoryScope]
'''.strip()

# this parameter is responsible for flow
api = '''
$[/myJobStep/tmpl/wasApi]
'''.strip()

requestParams = '''
$[/myJobStep/tmpl/requestParameters]
'''.strip()

resName = '''
$[factoryAdministrativeName]
'''.strip()

mode = "create";

### some logic to switch to edit mode if required goes here:
editResourceScope = isResourceExists(scope, api, resName)
if editResourceScope:
    print "Raw resource scope: %s" %(editResourceScope)
    mode = "edit"
### end of logic

print "Operation mode: %s" % (mode);
scopeResult = AdminConfig.getid(scope)

print scopeResult
actionResult = None

if mode == 'create':
    print "Creating WMQ JMS Connection Factory"
    actionResult = AdminTask.createWMQConnectionFactory(
        scopeResult,
        [requestParams]
    )
    print "Status: OK, Message: WMQ JMS Connection Factory %s has been created" % (resName)
elif mode == 'edit':
    print "Edit WMQ JMS Connection Factory"
    actionResult = AdminTask.modifyWMQConnectionFactory(
        editResourceScope,
        [requestParams]
    )
    print "Status: OK, Message: WMQ JMS Connection Factory %s has been updated" % (resName)
else:
    print "unknown action"
    sys.exit(1)

AdminConfig.save()

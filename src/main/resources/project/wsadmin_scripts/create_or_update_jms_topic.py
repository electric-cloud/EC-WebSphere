$[/myProject/wsadmin_scripts/preamble.py]

scope = '''
$[topicScope]
'''.strip()

# this parameter is responsible for flow
api = '''
$[/myJobStep/tmpl/wasApi]
'''.strip()

requestParams = '''
$[/myJobStep/tmpl/requestParameters]
'''.strip()

resName = '''
$[topicAdministrativeName]
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

if api == 'WMQ_Topic' and mode == 'create':
    print "Creating WMQ Topic"
    actionResult = AdminTask.createWMQTopic(
        scopeResult,
        [requestParams]
    )
    print "Status: OK, Message: WMQ JMS Topic %s has been created" % (resName)
elif api == 'WMQ_Topic' and mode == 'edit':
    print "Editing WMQ_Topic"
    actionResult = AdminTask.modifyWMQTopic(
        editResourceScope,
        [requestParams]
    )
    print "Status: OK, Message: WMQ JMS Topic %s has been updated" % (resName)
elif api == 'SIB_Topic' and mode == 'create':
    print "Creating SIB Topic"
    actionResult = AdminTask.createSIBJMSTopic(
        scopeResult,
        [requestParams]
    )
    print "Status: OK, Message: SIB JMS Topic %s has been created" % (resName)
elif api == 'SIB_Topic' and mode == 'edit':
    print "Editing SIB_Topic"
    actionResult = AdminTask.modifySIBJMSTopic(
        editResourceScope,
        [requestParams]
    )
    print "Status: OK, Message: SIB JMS Topic %s has been updated" % (resName)

else:
    print "unknown action"
    sys.exit(1)

AdminConfig.save()

$[/myProject/wsadmin_scripts/preamble.py]

scope = '''
$[resourceScope]
'''.strip()

# this parameter is responsible for flow
api = '''
$[wmqResourceAPI]
'''.strip()

requestParams = '''
$[/myJobStep/requestParameters]
'''.strip()

requestParamsEdit = '''
$[/myJobStep/requestParametersEdit]
'''.strip()

resName = '''
$[wmqResourceName]
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
    print "Modifying WMQ Topic"
    actionResult = AdminTask.modifyWMQTopic(
        editResourceScope,
        [requestParamsEdit]
    )
    print "Status: OK, Message: WMQ JMS Topic %s has been updated" % (resName)
elif api == 'WMQ_Queue' and mode == 'create':
    print "Creating WMQ Queue"
    actionResult = AdminTask.createWMQQueue(
        scopeResult,
        [requestParams]
    )
    print "Status: OK, Message: WMQ JMS Queue %s has been created" % (resName)
elif api == 'WMQ_Queue' and mode == 'edit':
    print "Editing WMQ_Queue"
    actionResult = AdminTask.modifyWMQQueue(
        editResourceScope,
        [requestParamsEdit]
    )
    print "Status: OK, Message: WMQ JMS Queue %s has been updated" % (resName)
elif api == 'WMQ_ConnectionFactory' and mode == 'create':
    print "Creating WMQ_ConnectionFactory"
    actionResult = AdminTask.createWMQConnectionFactory(
        scopeResult,
        [requestParams]
    )
    print "Status: OK, Message: WMQ JMS ConnectionFactory %s has been created" % (resName)
elif api == 'WMQ_ConnectionFactory' and mode == 'edit':
    print "Creating WMQ_ConnectionFactory"
    actionResult = AdminTask.modifyWMQConnectionFactory(
        editResourceScope,
        [requestParamsEdit]
    )
    print "Status: OK, Message: WMQ JMS ConnectionFactory %s has been updated" % (resName)
elif api == 'WMQ_ActivationSpec' and mode == 'create':
    print "Creating WMQ_ActivationSpec"
    actionResult = AdminTask.createWMQActivationSpec(
        scopeResult,
        [requestParams]
    )
    print "Status: OK, Message: WMQ JMS ActivationSpec %s has been created" % (resName)
elif api == 'WMQ_ActivationSpec' and mode == 'edit':
    print "Creating WMQ_ActivationSpec"
    actionResult = AdminTask.modifyWMQActivationSpec(
        editResourceScope,
        [requestParamsEdit]
    )
    print "Status: OK, Message: WMQ JMS ActivationSpec %s has been updated" % (resName)
else:
    print "unknown action"
    sys.exit(1)

AdminConfig.save()

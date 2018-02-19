$[/myProject/wsadmin_scripts/preamble.py]

scope = '''
$[/myJobStep/tmpl/queueScope]
'''.strip()

# this parameter is responsible for flow
api = '''
$[/myJobStep/tmpl/wasApi]
'''.strip()

requestParams = '''
$[/myJobStep/tmpl/requestParameters]
'''.strip()

resName = '''
$[queueAdministrativeName]
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

if api == 'WMQ_Queue' and mode == 'create':
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
        [requestParams]
    )
    print "Status: OK, Message: WMQ JMS Queue %s has been updated" % (resName)
elif api == 'SIB_Queue' and mode == 'create':
    print "Creating SIB Queue"
    actionResult = AdminTask.createSIBJMSQueue(
        scopeResult,
        [requestParams]
    )
    print "Status: OK, Message: SIB JMS Queue %s has been created" % (resName)
elif api == 'SIB_Queue' and mode == 'edit':
    print "Editing SIB_Queue"
    actionResult = AdminTask.modifySIBJMSQueue(
        editResourceScope,
        [requestParams]
    )
    print "Status: OK, Message: SIB JMS Queue %s has been updated" % (resName)

else:
    print "unknown action"
    sys.exit(1)

AdminConfig.save()

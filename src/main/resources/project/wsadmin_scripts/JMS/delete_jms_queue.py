$[/myProject/wsadmin_scripts/preamble.py]

scope = '''
$[/myJobStep/tmpl/queueScope]
'''.strip()

# this parameter is responsible for flow
api = '''
$[/myJobStep/tmpl/wasApi]
'''.strip()

resName = '''
$[queueAdministrativeName]
'''.strip()

### checking resource
resourceScope = isResourceExists(scope, api, resName)
if resourceScope:
    print "Resource: %s exists. Deleting." %(resourceScope)
    if api == 'WMQ_Queue':
        actionResult = AdminTask.deleteWMQQueue(resourceScope)
        print "Status: OK, Message: WMQ JMS Queue %s has been deleted" % (resName)
    elif api == 'SIB_Queue':
        actionResult = AdminTask.deleteSIBJMSQueue(resourceScope)
        print "Status: OK, Message: SIB JMS Queue %s has been deleted" % (resName)
else:
    print "Resource %s with type %s does not exist, can't delete" % (resName, api)
    sys.exit(1)

AdminConfig.save()

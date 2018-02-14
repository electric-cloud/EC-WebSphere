$[/myProject/wsadmin_scripts/preamble.py]

scope = '''
$[topicScope]
'''.strip()

# this parameter is responsible for flow
api = '''
$[/myJobStep/tmpl/wasApi]
'''.strip()

resName = '''
$[topicAdministrativeName]
'''.strip()

### checking resource
resourceScope = isResourceExists(scope, api, resName)
if resourceScope:
    print "Resource: %s exists. Deleting." %(resourceScope)
    if api == 'WMQ_Topic':
        actionResult = AdminTask.deleteWMQTopic(resourceScope)
        print "Status: OK, Message: WMQ JMS Topic %s has been deleted" % (resName)
    elif api == 'SIB_Topic':
        actionResult = AdminTask.deleteSIBJMSTopic(resourceScope)
        print "Status: OK, Message: SIB JMS Topic %s has been deleted" % (resName)
else:
    print "Resource %s with type %s does not exist, can't delete" % (resName, api)
    sys.exit(1)

AdminConfig.save()

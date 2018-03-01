$[/myProject/wsadmin_scripts/preamble.py]

scope = '''
$[/myJobStep/tmpl/activationSpecScope]
'''.strip()

# this parameter is responsible for flow
api = '''
$[/myJobStep/tmpl/wasApi]
'''.strip()

resName = '''
$[activationSpecAdministrativeName]
'''.strip()

### checking resource
resourceScope = isResourceExists(scope, api, resName)
if resourceScope:
    print "Resource: %s exists. Deleting." %(resourceScope)
    if api == 'WMQ_ActivationSpec':
        actionResult = AdminTask.deleteWMQActivationSpec(resourceScope)
        print "Status: OK, Message: WMQ JMS ActivationSpec %s has been deleted" % (resName)
    elif api == 'SIB_ActivationSpec':
        actionResult = AdminTask.deleteSIBJMSActivationSpec(resourceScope)
        print "Status: OK, Message: SIB JMS ActivationSpec %s has been deleted" % (resName)
else:
    print "Resource %s with type %s does not exist, can't delete" % (resName, api)
    sys.exit(1)

AdminConfig.save()

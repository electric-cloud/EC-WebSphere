import sys

applicationName = r'''
$[applicationName]
'''.strip()

loadOrder = r'''
$[loadOrder]
'''.strip()

classLoaderPolicy = r'''
$[classLoaderPolicy]
'''.strip()

dep = AdminConfig.getid('/Deployment:' + applicationName + '/')
if not dep :
    print "No application was found by name provided: %s" % (applicationName)
    sys.exit(1);
print "Application: " + dep

depObject = AdminConfig.showAttribute(dep, 'deployedObject')
if not depObject:
    print "Deployed Object wasn't found"
    sys.exit(1);
print "DeployerObject: " + depObject;

classldr = AdminConfig.showAttribute(depObject, 'classloader')
if not classldr:
    print "ClassLoader %s wasn't found" % (classldr)
    sys.exit(1);
print "ClassLoader: " + classldr

print AdminConfig.showall(classldr)

# load order PARENT_LAST PARENT_FIRST
AdminConfig.modify(classldr, [['mode', loadOrder]])
# loading policy - SINGLE, MULTIPLE
AdminConfig.modify(depObject, [['warClassLoaderPolicy', classLoaderPolicy]])

AdminConfig.save()

# Obtain deployment manager MBean
dm = AdminControl.queryNames('type=DeploymentManager,*')

# Synchronization of configuration changes is only required in network deployment.not in standalone server environment.
if dm:
    print 'Synchronizing configuration repository with nodes. Please wait...'
    nodes=AdminControl.invoke(dm, "syncActiveNodes", "true")
    print 'The following nodes have been synchronized:'+str(nodes)
else:
    print 'Standalone server, no nodes to sync'


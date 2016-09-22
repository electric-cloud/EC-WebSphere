import sys

libraryName = '$[libraryName]'
applicationName = '$[applicationName]'

# library section
library = AdminConfig.getid('/Library:%s/' % (libraryName))
if not library :
    print "No library was found by name provided"
    sys.exit(1);
print "Library: " + library

# application section
deployment = AdminConfig.getid('/Deployment:%s/' % (applicationName))
if not deployment :
    print "No application was found by name provided"
    sys.exit(1);
print "Depoyment: " + deployment

# deployment section
appDeploy = AdminConfig.showAttribute(deployment, 'deployedObject')
if not appDeploy:
    print "DeployedObject was not found"
    sys.exit(1);
print "AppDeploy: " + appDeploy

# classloader
classLoad1 = AdminConfig.showAttribute(appDeploy, 'classloader')
if not classLoad1:
    print "ClassLoader was not found"
    sys.exit(1);
print "ClassLoader: " + classLoad1

# result
print AdminConfig.create('LibraryRef', classLoad1, [['libraryName', libraryName]])
AdminConfig.save()


libraryName = '$[libraryName]'
applicationName = '$[applicationName]'

library = AdminConfig.getid('/Library:%s/' % (libraryName))
print library
deployment = AdminConfig.getid('/Deployment:%s/' % (applicationName))
print deployment
appDeploy = AdminConfig.showAttribute(deployment, 'deployedObject')
print appDeploy
classLoad1 = AdminConfig.showAttribute(appDeploy, 'classloader')
print classLoad1
print AdminConfig.create('LibraryRef', classLoad1, [['libraryName', 'javalib']])
AdminConfig.save()


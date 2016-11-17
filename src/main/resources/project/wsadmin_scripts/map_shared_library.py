#
#  Copyright 2016 Electric Cloud, Inc.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

import sys

libraryName = r'''
$[libraryName]
'''.strip()

applicationName = r'''
$[applicationName]
'''.strip()

print "Mapping shared library: %s" % (libraryName)

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

# Obtain deployment manager MBean
dm = AdminControl.queryNames('type=DeploymentManager,*')

# Synchronization of configuration changes is only required in network deployment.not in standalone server environment.
if dm:
    print 'Synchronizing configuration repository with nodes. Please wait...'
    nodes=AdminControl.invoke(dm, "syncActiveNodes", "true")
    print 'The following nodes have been synchronized:'+str(nodes)
else:
    print 'Standalone server, no nodes to sync'

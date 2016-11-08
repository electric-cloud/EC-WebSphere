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

appName = r'''
$[appName]
'''.strip()
clusterName = r'''
$[clusterName]
'''.strip()
contentType = r'''
$[contentType]
'''.strip()
contentURI = r'''
$[contentURI]
'''.strip()
serverName = r'''
$[serverName]
'''.strip()


updateCommand = r'''
[-operation $[operation] -contents $[content]
'''.strip()
if contentURI:
    updateCommand + ' ' # just whitespace - as a delimeter
    updateCommand += r'''-contenturi $[contentURI]'''.strip()

additionalParams = r'''$[additionalParams]'''.strip()
if additionalParams:
    additionalParams = additionalParams.replace("\n", ' ')
    additionalParams = additionalParams.replace("\r", ' ')
    updateCommand += ' ' # another delimeter
    updateCommand += additionalParams

updateCommand += r''' ]'''.strip()

result = AdminApp.update(appName, contentType, updateCommand)
print result
AdminConfig.save()
result = AdminApp.isAppReady(appName)
print 'Is App Ready = ' + result
while result != 'true':
    result = AdminApp.isAppReady(appName)
    print 'Is App Ready = ' + result
    sleep(3)
print 'The application is ready to restart.'


if clusterName:
    result = AdminClusterManagement.checkIfClusterExists(clusterName)
    if result == "false":
        print 'Error : Cluster ' + clusterName + ' does not exist.'
        sys.exit(1)

    cluster = AdminControl.completeObjectName('type=Cluster,name=' + clusterName + ',*')
    print cluster
    print "Restarting every member of cluster after application is updated."
    AdminControl.invoke(cluster, 'rippleStart')
    status = AdminControl.getAttribute(cluster, 'state')
    desiredStatus = 'websphere.cluster.running'
    print 'Cluster status = ' + status
    while 1:
        status = AdminControl.getAttribute(cluster, 'state')
        print 'Cluster status = ' + status
        if status == desiredStatus:
            break
        else:
            sleep(3)
    print 'Application is UP!'

else:

    appManager = AdminControl.queryNames('type=ApplicationManager,process=' + serverName + ',*')
    print appManager
    result = AdminControl.invoke(appManager,'stopApplication', appName)
    print result
    result = AdminControl.invoke(appManager,'startApplication', appName)
    print result
    appstatus = AdminControl.completeObjectName('type=Application,name=' + appName + ',*')
    if appstatus:
        print 'Application is UP!'
    else:
        print 'Application is not UP.'

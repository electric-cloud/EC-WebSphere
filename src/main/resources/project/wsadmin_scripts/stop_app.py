#
#  Copyright 2015 Electric Cloud, Inc.
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

$[/myProject/wsadmin_scripts/preamble.py]

appName = '''
$[appname]
'''.strip()

serverName = '''
$[serverName]
'''.strip()

clusterName = '''
$[clusterName]
'''.strip()


def stopApplication(appName, serverName = None, clusterName = None):
    if clusterName:
        AdminApplication.stopApplicationOnCluster(appName, clusterName)
    elif serverName:
        nodeName, serverName = serverName.split('=')
        AdminApplication.stopApplicationOnSingleServer(appName, nodeName, serverName)
    else:
        appmgr = AdminControl.queryNames('name=ApplicationManager,*')
        AdminControl.invoke(appmgr, 'stopApplication', appName)


if isAppRunning(appName):
    stopApplication(appName, serverName=serverName, clusterName=clusterName)
else:
    print "WARNING: Application %s is already stopped" % appName


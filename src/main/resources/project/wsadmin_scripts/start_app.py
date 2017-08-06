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


def startApplication(appName, serverName = None, clusterName = None):
    if clusterName:
        AdminApplication.startApplicationOnCluster(appName, clusterName)
    elif serverName:
        nodeName, serverName = serverName.split('=')
        AdminApplication.startApplicationOnSingleServer(appName, nodeName, serverName)
    else:
        appmgr = AdminControl.queryNames('name=ApplicationManager,*')
        AdminControl.invoke(appmgr, 'startApplication', appName)

if isAppRunning(appName):
    print "WARNING: Application %s is already running" % appName
    sys.exit(0)
else:
    startApplication(appName, serverName=serverName, clusterName=clusterName)
    print "Application %s has been started" % appName


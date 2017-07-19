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
nodeName = r'''
$[node]
'''.strip()
cellName = r'''
$[cell]
'''.strip()

serverName = r'''
$[server]
'''.strip();

implClassName = r'''
$[implementationClassName]
'''.strip()

providerName = r'''
$[jdbcProvidername]
'''.strip()

inputClasspath = r'''
$[classpath]
'''.strip()
resourceId = ''

if cellName:
    resourceId += "/Cell:" + cellName

if nodeName:
    resourceId += "/Node:" + nodeName

if serverName:
    resourceId += "/Server:" + serverName

resourceId += "/"

print "ResourceID: " + resourceId

node = AdminConfig.getid(resourceId)
n1 = ['name', providerName]
implCN = ['implementationClassName', implClassName]
classPath = ['classpath', inputClasspath]
descr = ['description', '']
jdbcAttrs = [n1,  implCN, classPath, descr]

AdminConfig.create('JDBCProvider', node, jdbcAttrs)
AdminConfig.save()

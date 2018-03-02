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

import sys
import time
import re
import os

# Checks application readiness
def isAppReady(appName):
    ready = AdminApp.isAppReady(appName)
    if ready:
        return 1
    else:
        return 0

# Checks application for existance
def isAppExists(appName):
    deployment = AdminConfig.getid('/Deployment:%s/' % (appName));
    if deployment:
        return 1
    else:
        return 0

# Checks if the specified application is in running state
def isAppRunning(appName):
    serverStatus = AdminControl.completeObjectName('type=Application,name=%s,*' % appName)
    if serverStatus:
        return 1
    else:
        return 0

# this function will return high-level application status.
# Need to do some clarifications. There is application state transitions:
# EXISTS => READY => RUNNING
# it means, that if application is ready, it is exists.
# If application is running, it is ready and exists.
def getAppState(appName):
    status = 'NOT_EXISTS'
    if isAppExists(appName):
        status = 'EXISTS'
    else:
        status = 'NOT_EXISTS'
        return status
    if isAppReady(appName):
        status = 'READY'
    else:
        status = 'NOT_READY'
        return status
    if isAppRunning(appName):
        status = 'RUNNING'
    else:
        status = 'NOT_RUNNING'
        return status
    
    return status

def getStateMatrix():
    matrix = {
        "RUNNING": ['RUNNING', 'EXISTS', 'READY'],
        "READY": ['READY', 'EXISTS'],
        "EXISTS": ['EXISTS'],
        "NOT_EXISTS": ['NOT_EXISTS'],
        "NOT_READY": ['NOT_READY', 'EXISTS'],
        "NOT_RUNNING": ['NOT_RUNNING', 'EXISTS', 'READY']
    }
    return matrix

def isAppInDesiredState(appName, desiredState):
    applicationState = getAppState(appName)
    print "Application State: '%s'" % (applicationState)
    matrix = getStateMatrix()
    row = matrix[applicationState]
    if desiredState in row:
        # print "Condition met";
        return 1;
    else:
        # print "Condition is not met";
        return 0;

# this function parses output
def parseOutput(output):
    records = []
    for nextArgument in re.split('\n', output):
        if nextArgument:
            records.append(nextArgument)
    return records

def wsadminTaskToDict(inStr):
    map = {}
    if (len(inStr) > 0 and inStr[0] == '{' and inStr[-1] == '}'):
        inStr = inStr[1:-1]
    tmpList = inStr.split(",")
    for p in tmpList:
        pNameValue = p.strip().split("=")
        map[pNameValue[0]] = pNameValue[1]
    return map

def getWMQTopics(scope):
    result = AdminConfig.getid(scope)
    print result
    topics = AdminTask.listWMQTopics(result)
    print topics
    retval = []
    for topic in parseOutput(topics):
        t = wsadminTaskToDict(AdminTask.showWMQTopic(topic))
        t["__raw_resource_scope__"] = topic
        retval.append(t)
    return retval

def getWMQQueues(scope):
    result = AdminConfig.getid(scope)
    print result
    topics = AdminTask.listWMQQueues(result)
    print topics
    retval = []
    for topic in parseOutput(topics):
        t = wsadminTaskToDict(AdminTask.showWMQQueue(topic))
        t["__raw_resource_scope__"] = topic
        retval.append(t)
    return retval

def getWMQConnectionFactories(scope):
    result = AdminConfig.getid(scope)
    print result
    topics = AdminTask.listWMQConnectionFactories(result)
    print topics
    retval = []
    for topic in parseOutput(topics):
        t = wsadminTaskToDict(AdminTask.listWMQConnectionFactory(topic))
        t["__raw_resource_scope__"] = topic
        retval.append(t)
    return retval

def getWMQActivationSpecs(scope):
    result = AdminConfig.getid(scope)
    print result
    topics = AdminTask.listWMQActivationSpecs(result)
    print topics
    retval = []
    for topic in parseOutput(topics):
        t = wsadminTaskToDict(AdminTask.showWMQActivationSpec(topic))
        t["__raw_resource_scope__"] = topic
        retval.append(t)
    return retval

def getSIBJMSTopics(scope):
    result = AdminConfig.getid(scope)
    print result
    topics = AdminTask.listSIBJMSTopics(result)
    print topics
    retval = []
    for topic in parseOutput(topics):
        t = wsadminTaskToDict(AdminTask.showSIBJMSTopic(topic))
        t["__raw_resource_scope__"] = topic
        retval.append(t)
    return retval

def getSIBJMSQueues(scope):
    result = AdminConfig.getid(scope)
    print result
    topics = AdminTask.listSIBJMSQueues(result)
    print topics
    retval = []
    for topic in parseOutput(topics):
        t = wsadminTaskToDict(AdminTask.showSIBJMSQueue(topic))
        t["__raw_resource_scope__"] = topic
        retval.append(t)
    return retval

def isResourceExists(scope, resType, resName):
    result = AdminConfig.getid(scope)
    print result
    records = None
    if resType == 'WMQ_Topic':
        records = getWMQTopics(scope)
    elif resType == 'SIB_Topic':
        records = getSIBJMSTopics(scope)
    elif resType == 'WMQ_Queue':
        records = getWMQQueues(scope)
    elif resType == 'SIB_Queue':
        records = getSIBJMSQueues(scope)
    elif resType == 'WMQ_ConnectionFactory':
        records = getWMQConnectionFactories(scope)
    elif resType == 'WMQ_ActivationSpec':
        records = getWMQActivationSpecs(scope)
    else:
        print "Wrong resource type %s" % (resType)
        sys.exit(1)

    if records:
        for record in records:
            if record.get("name") == resName:
                return record.get("__raw_resource_scope__")
    return 0

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

# logging functions
def logWithLevel(level, logLine):
    print "[OUT][%s]: %s :[%s][OUT]"

def logInfo(logLine):
    logWithLevel("INFO", logLine)

def logWarning(logLine):
    logWithLevel("WARNING", logLine)

def logError(logLine):
    logWithLevel("ERROR", logLine)

def logSummary(logLine):
    logWithLevel("SUMMARY", logLine)

def logOutcome(logLine):
    logWithLevel("OUTCOME", logLine)

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
        "READY": ['READY', 'EXISTS', 'NOT_RUNNING'],
        "EXISTS": ['EXISTS', 'NOT_READY', 'NOT_RUNNING',],
        "NOT_EXISTS": ['NOT_EXISTS', 'NOT_READY', 'NOT_RUNNING'],
        "NOT_READY": ['NOT_READY', 'EXISTS', 'NOT_RUNNING'],
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
    for nextArgument in re.split('(?:\n|\r\n)', output):
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

def getWMQConnectionFactories(scope, resType):
    result = AdminConfig.getid(scope)
    print result
    params = "-type %s" % (resType)
    topics = AdminTask.listWMQConnectionFactories(result, [params])
    print topics
    # todo
    retval = []
    for topic in parseOutput(topics):
        t = wsadminTaskToDict(AdminTask.showWMQConnectionFactory(topic))
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

def getSIBJMSActivationSpecs(scope):
    result = AdminConfig.getid(scope)
    print result
    topics = AdminTask.listSIBJMSActivationSpecs(result)
    print topics
    retval = []
    for topic in parseOutput(topics):
        t = wsadminTaskToDict(AdminTask.showSIBJMSActivationSpec(topic))
        t["__raw_resource_scope__"] = topic
        retval.append(t)
    return retval

def getSIBJMSConnectionFactories(scope):
    result = AdminConfig.getid(scope)
    print result
    topics = AdminTask.listSIBJMSConnectionFactories(result, ["-type all"])
    print topics
    retval = []
    for topic in parseOutput(topics):
        t = wsadminTaskToDict(AdminTask.showSIBJMSConnectionFactory(topic))
        t["__raw_resource_scope__"] = topic
        retval.append(t)
    return retval

def getJMSProviderAtScope(providerName, scope):
    if not providerName:
        print "providerName parameter should be present and non-empty"
        sys.exit(1)
    providerScope = scope + "JMSProvider:" + providerName
    result = AdminConfig.getid(providerScope)
    return result

def deleteJMSProvider(providerId):
    AdminConfig.remove(providerId)

def isResourceExists(scope, resType, resName, fType = ''):
    result = AdminConfig.getid(scope)
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
        records = getWMQConnectionFactories(scope, fType)
    elif resType == 'SIB_ConnectionFactory':
        records = getSIBJMSConnectionFactories(scope)
    elif resType == 'WMQ_ActivationSpec':
        records = getWMQActivationSpecs(scope)
    elif resType == 'SIB_ActivationSpec':
        records = getSIBJMSActivationSpecs(scope)
    else:
        print "Wrong resource type %s" % (resType)
        sys.exit(1)

    if records:
        for record in records:
            if record.get("name") == resName:
                return record.get("__raw_resource_scope__")
    return 0


def showServerStatus(nodeName, serverName):
    beanId = AdminControl.completeObjectName("node=" + nodeName + ",process=" + serverName + ",name=" + serverName + ",j2eeType=J2EEServer,*")
    serverId = AdminConfig.getid("/Node:" + nodeName + "/Server:" + serverName + "/")
    if (serverId == "") :
        return "Unknown!"
    #endIf

    if (beanId == "") :
        state = "Stopped"
        pid = "N/A"
    else:
        state = AdminControl.getAttribute(beanId, "state")
        pid = AdminControl.getAttribute(beanId, "pid")

    beanId = AdminControl.completeObjectName("J2EEServer=" + serverName + ",name=JVM,*")

    if (beanId == ""):
        heapSize = 0
        freeMem = 0
        maxMem = 0
    else:
        heapSize = long(AdminControl.getAttribute(beanId, "heapSize")) / 1024 / 1024
        freeMem = long(AdminControl.getAttribute(beanId, "freeMemory")) / 1024 / 1024
        maxMem = long(AdminControl.getAttribute(beanId, "maxMemory")) / 1024 / 1024

    print "[%s] %s : %s: pid=%s, heap=%dMB, free=%dMB, max=%dMB" % (nodeName, serverName, state, pid, heapSize, freeMem, maxMem)
    return state

def getServersInNode(desiredNodeName, opts):
    retval = []
    for node in AdminConfig.list('Node').splitlines() :
        nodeName = AdminConfig.showAttribute(node, 'name')
        if desiredNodeName != nodeName :
            continue
        for server in AdminConfig.list('Server', node).splitlines() :
            serverName = AdminConfig.showAttribute(server, 'name')
            if ('ignoreNodeAgent') in opts.keys() and opts['ignoreNodeAgent'] == 1 and serverName == 'nodeagent':
                continue
            retval.append(serverName)
    return retval

# This function parses pairs in format nodename:servername into python dict.
def parseServerListAsDict(servers, opts):
    res = []
    for nextArgument in re.split(',', servers):
        res.append(nextArgument.strip())
    if 'filterUnique' in opts.keys() and opts['filterUnique'] == 1:
        tempSet = set(res)
        if len(tempSet) != len(res):
            print 'WARNING: Non-unique servers are detected'
        res = list(tempSet)
    retval = {}
    for serverString in res:
        server = re.split(':', serverString)
        if len(server) != 2:
            errorString = 'Expected nodename:servername record, got %s' % (serverString)
            raise ValueError(errorString)
        nodeName = unicode(server[0])
        serverName = unicode(server[1])
        if not nodeName in retval.keys():
            retval[nodeName] = []
        if 'expandStar' in opts.keys() and opts['expandStar'] == 1 and serverName == '*':
            retval[nodeName] = getServersInNode(nodeName, {'ignoreNodeAgent': 1})
        else:
            retval[nodeName].append(serverName)
    return retval

# this function par
def parseServerListAsList(servers, opts):
    serverList = parseServerListAsDict(servers, opts)
    retval = []
    for nodeName in serverList.keys():
        for serverName in serverList[nodeName]:
            retval.append({'Node': nodeName, 'Server': serverName});
    return retval

def uintOrZero(value):
    retval = 0
    try:
        retval = int(value)
    except ValueError:
        return retval
    if retval < 0:
        retval = 0
    return retval



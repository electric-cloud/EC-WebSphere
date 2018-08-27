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
import traceback
import string
import random

def genRandomString(l):
    result = ''
    for _ in range(l):
        result = result + random.choice('0123456789abcdef')

    return result

# logging functions
def logWithLevel(level, logLine):
    print "\n[OUT][%s]: %s :[%s][OUT]\n" % (level, logLine, level)

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

def forwardException(logLine):
    logWithLevel("EXCEPTION", logLine)

def setOutcome(outcome):
    logWithLevel("OUTCOME", logLine)

# Checks application readiness
def isAppReady(appName):
    ready = AdminApp.isAppReady(appName)
    if ready:
        return 1
    else:
        return 0

# exception functions:
def getExceptionMsg():
    errorType, errorMsg, errorTraceBack = sys.exc_info()
    retval = str(errorMsg).strip()
    return retval

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

def genUUID():
    length = [4, 4, 4, 12]
    retval = genRandomString(8)
    for i in length:
        retval = retval + '-' + genRandomString(i)
    return retval;


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
            logWarning('WARNING: Non-unique servers are detected')
        res = list(tempSet)
    retval = {}
    for serverString in res:
        server = re.split(':', serverString)
        if len(server) != 2 or server[0] == '' or server[1] == '':
            errorString = 'Expected nodename:servername record, got %s' % (serverString)
            logError(errorString)
            raise ValueError(errorString)
        nodeName = unicode(server[0])
        serverName = unicode(server[1])
        if not nodeName in retval.keys():
            retval[nodeName] = []
        if 'expandStar' in opts.keys() and opts['expandStar'] == 1 and serverName == '*':
            try:
                retval[nodeName] = getServersInNode(nodeName, {'ignoreNodeAgent': 1})
            except:
                forwardException(getExceptionMsg())
                sys.exit(1)
            if len(retval[nodeName]) == 0:
                logError('Node %s does not exist or does not have servers' % (nodeName))
                raise ValueError('Node %s does not exists or does not have servers' % (nodeName))
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


def toBoolean(value):
    if value.lower() == 'true' or value == '1':
        return 1

    return 0

def toBooleanString(value):
    if value.lower() == 'true' or value == '1':
        return 'true'
    return 'false'

# Synchronization of configuration changes is only required in network deployment.not in standalone server environment.
def syncActiveNodes():
    dm = AdminControl.queryNames('type=DeploymentManager,*')
    if dm:
        print 'Synchronizing configuration repository with nodes now.'
        nodes=AdminControl.invoke(dm, "syncActiveNodes", "true")
        print 'The following nodes have been synchronized: ' + str(nodes)
    else:
        print 'Standalone server, no nodes to sync'

def bailOut(msg):
    logError(msg)
    sys.exit(1)

def splitNodeServer(nodeServer):
    vals = re.split(':', nodeServer)
    if len(vals) != 2:
        bailOut("Expected nodename:servername, got %s" %(nodeServer))
    vals[0] = vals[0].strip()
    vals[1] = vals[1].strip()
    if not vals[0] or not vals[0]:
        bailOut("NodeName and ServerName should be present and not empty");
    retval = {
        'Node': vals[0],
        'Server': vals[1]
    }
    return retval

def createClusterMemberWrapper(params):
    print "createClusterMemberWrapper Params:"
    print params
    if not params['clusterName']:
        bailOut("Missing clusterName parameter")
    if not params['memberConfig']:
        bailOut("Missing memberConfig parameter")

    memberConfig = []
    for k in params['memberConfig'].keys():
        memberConfig.append(k)
        memberConfig.append(params['memberConfig'][k])
    additionParams = [
        '-clusterName',
        params['clusterName'],
        '-memberConfig',
        memberConfig
    ]
    if 'firstMember' in params.keys():
        additionParams.append('-firstMember')
        firstMember = []
        for k in params['firstMember'].keys():
            firstMember.append(k)
            firstMember.append(params['firstMember'][k])
        additionParams.append(firstMember)

    if 'memberWeight' in params and params['memberWeight']:
        additionParams['memberConfig']['-memberWeight'] = params['memberWeight']

    return AdminTask.createClusterMember(additionParams)
    
        
def createClusterMembers(params):
    if 'clusterName' not in params:
        raise ValueError('clusterName key is mandatory')
    if 'targetNode' not in params:
        raise ValueError('targetNode is mandatory')
    if 'targetName' not in params:
        raise ValueError('targetName is mandatory')
    
    creationParams = {
        'clusterName': params['clusterName'],
        'memberConfig': {
            '-memberName': params['targetName'],
            '-memberNode': params['targetNode']
        }
    }
    if 'memberWeight' in params and params['memberWeight']:
        creationParams['memberConfig']['-memberWeight'] = params['memberWeight']
    return createClusterMemberWrapper(creationParams)
    
    
def createFirstClusterMember(params):
    if 'creationPolicy' not in params.keys():
        bailOut("Creation Policy parameter is missing")
    if params['creationPolicy'] not in ['existing', 'template']:
        bailOut("Creation Policy should be existing or template")
    if 'clusterName' not in params.keys():
        bailOut('clusterName is mandatory')
    if 'targetNode' not in params.keys():
        bailOut('Missing Target Node for 1st cluster member creation')
    if 'targetServer' not in params.keys():
        bailOut('Missing Target Server for 1st cluster member creation')

    if params['creationPolicy'] == 'existing':
        if 'sourceNode' not in params:
            bailOut('Source Node is mandatory')
        if 'sourceServer' not in params:
            bailOut('Source Server is mandatory')
    elif params['creationPolicy'] == 'template' and 'templateName' not in params.keys():
        bailOut('TemplateName is mandatory when creationPolicty is set to template')

    if 'resourcesScope' not in params:
        bailOut("Missing resourcesScope parameter")
    creationPolicy = params['creationPolicy']
    additionParams = {
        'clusterName': clusterName,
        'memberConfig': {
            '-memberNode': params['targetNode'],
            '-memberName': params['targetServer']
        },
        'firstMember': {
            '-resourcesScope': params['resourcesScope']
        }
    }

    if 'memberWeight' in params and params['memberWeight']:
        additionParams['memberConfig']['-memberWeight'] = params['memberWeight']

    if 'genUniquePorts' in params:
        gup = params['genUniquePorts']
        if gup not in ['true', 'false']:
            gup = toBooleanString(gup)
            
        additionParams['memberConfig']['-genUniquePorts'] = gup
    if creationPolicy == 'template':
        additionParams['firstMember']['-templateName'] = params['templateName']
    else:
        additionParams['firstMember']['-templateServerNode'] = params['sourceNode']
        additionParams['firstMember']['-templateServerName'] = params['sourceServer']
    
    return createClusterMemberWrapper(additionParams)
# Commenting it out right now
# def getServerId(nodeName, serverName):
#     serverId = AdminConfig.getid('/Node:%s/Server:%s/' % (nodeName, serverName))
#     return serverId

# def deleteApplicationServer(nodeName, serverName):
#     serverId = getServerId(nodeName, serverName)
#     if not serverId:
#         logError("Server %s not found on node %s" % (serverName, nodeName))
#     print "Deleting server with id %s on node %s" % (serverName, nodeName)
#     AdminConfig.remove(serverId)

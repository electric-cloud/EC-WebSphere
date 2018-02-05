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
        "NOT_READY": ['NOT_READY'],
        "NOT_RUNNING": ['NOT_RUNNING']
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

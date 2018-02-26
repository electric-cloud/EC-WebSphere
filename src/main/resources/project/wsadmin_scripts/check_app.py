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

$[/myProject/wsadmin_scripts/preamble.py]

appName = r'''
$[appname]
'''.strip()

# EXISTS NOT_EXISTS READY NOT_READY RUNNING NOT_RUNNING
appStateChecked = r'''
$[appStateChecked]
'''.strip()

waitTime = r'''
$[waitTimeForState]
'''.strip()

if not waitTime:
    waitTime = '0'

print "WaitTime: %s" % (waitTime);
# TODO: review this line:
appName.replace(' ', '_')

isOk = 0
startTime = int(time.time())
waitTime = int(waitTime)
endTime = startTime + waitTime

stateMatrix = {
    "EXISTS": "is installed",
    "NOT_EXISTS": "is not installed",
    "READY": "is ready",
    "NOT_READY": "is not ready",
    "RUNNING": "is running",
    "NOT_RUNNING": "is not running"
}

while 1 :
    sleepTime = 5
    currentTime = int(time.time());
    if endTime < currentTime :
        print "Timed out."
        break
    if isAppInDesiredState(appName, appStateChecked):
        isOk = 1
        break
    else:
        print "Application %s %s, waiting" % (appName, stateMatrix[appStateChecked])
    time.sleep(sleepTime);

print "Application %s %s" % (appName, stateMatrix[appStateChecked])

if isOk:
    sys.exit(0)
else:
    sys.exit(1)



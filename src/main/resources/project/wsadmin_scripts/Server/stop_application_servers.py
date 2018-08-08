$[/myProject/wsadmin_scripts/preamble.py]

# stop application server jython script.

# properties retrieval section

serversList = '''
$[wasServersList]
'''.strip();

waitTime = '''
$[wasWaitTime]
'''.strip()

# values that defined for script
waitTime = uintOrZero(waitTime)
okServerStatus = 'Stopped'
sleepTime = 5
iterationsCount = waitTime / sleepTime
iterationsCount += 1

parsedServerList = parseServerListAsList(serversList, {"expandStar": 1})

# check server states section
for x in range (0, len(parsedServerList)):
    server = parsedServerList[x]
    serverStatus = showServerStatus(server['Node'], server['Server'])
    if serverStatus == okServerStatus:
        logWarning("Server %s on Node %s is already %s" % (server['Server'], server['Node'], okServerStatus))
        parsedServerList[x] = 0
    print serverStatus

parsedServerList = filter(lambda x: x, parsedServerList)

if len(parsedServerList) == 0:
    logWarning("Nothing to do, all servers are already %s" % (okServerStatus))
    os._exit(0)

# stopping the servers
for server in parsedServerList:
    params = '[-serverName "%s" -nodeName "%s"]' % (server['Server'], server['Node'])
    result = AdminTask.stopMiddlewareServer(params)
    print "Server stop result: ", result;

stopResults = []
# now we're checking that servers are stopped
stoppedServers = 0

for i in range(0, iterationsCount):
    for server in parsedServerList:
        serverStatus = showServerStatus(server['Node'], server['Server'])
        # Here we should handle exceptions.
        server['State'] = serverStatus
        if serverStatus == okServerStatus:
            stoppedServers += 1
            print serverStatus
    if stoppedServers >= len(parsedServerList):
        break
    time.sleep(sleepTime)

# handle procedure results
print "Procedure result:\n"
for server in parsedServerList:
    logSummary("Node: %s, Server: %s, State: %s" % (server['Node'], server['Server'], server['State']))
print "==="
print "Done."

if stoppedServers != len(parsedServerList):
    print "Error: Failed to stop servers"
    sys.exit(1)
# Partial Stop
# Stopped

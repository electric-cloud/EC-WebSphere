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
okServerStatus = 'STARTED'
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

# starting the servers
for server in parsedServerList:
    params = '[-serverName "%s" -nodeName "%s"]' % (server['Server'], server['Node'])
    result = AdminTask.startMiddlewareServer(params)
    print "Server start result: ", result;

startResults = []
# now we're checking that servers are start
startedServers = 0

for i in range(0, iterationsCount):
    for server in parsedServerList:
        serverStatus = showServerStatus(server['Node'], server['Server'])
        # Here we should handle exceptions.
        server['State'] = serverStatus
        if serverStatus == okServerStatus:
            startedServers += 1
            print serverStatus
    if startedServers >= len(parsedServerList):
        break
    time.sleep(sleepTime)

# handle procedure results
print "Procedure result:\n"
for server in parsedServerList:
    logSummary("Node: %s, Server: %s, State: %s" % (server['Node'], server['Server'], server['State']))
print "==="
print "Done."

if startedServers != len(parsedServerList):
    print "Error: Failed to start servers"
    sys.exit(1)


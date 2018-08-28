$[/myProject/wsadmin_scripts/preamble.py]

# start application server jython script.

# properties retrieval section

serversList = '''
$[wasServersList]
'''.strip();

waitTime = '''
$[wasWaitTime]
'''.strip()

# Mandatory parameters validation.
if not serversList:
    logError("Missing servers list to be started")
    sys.exit(1)

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
    serverStatus = ''
    try:
        serverStatus = showServerStatus(server['Node'], server['Server'])
    except:
        logSummary("Failed to check status of %s on node %s" % (server[Server], server['Node']))
        forwardException(getExceptionMsg())
        sys.exit(1)
    if serverStatus == okServerStatus:
        logWarning("Server %s on Node %s is already %s" % (server['Server'], server['Node'], okServerStatus))
        parsedServerList[x] = 0
    print serverStatus

parsedServerList = filter(lambda x: x, parsedServerList)

if len(parsedServerList) == 0:
    logWarning("Nothing to do, all servers are already %s" % (okServerStatus))
    logSummary("All servers are already %s" % (okServerStatus))
    os._exit(0)

# starting the servers
for server in parsedServerList:
    params = '[-serverName "%s" -nodeName "%s"]' % (server['Server'], server['Node'])
    try:
        if not is_8_0_0(server['Node']):
            result = AdminTask.startMiddlewareServer(params)
            print "Server start result: ", result
        else:
            print "Falling back to 8.0.0 mode"
            result = startApplicationServer8_0_0(server['Node'], server['Server'])
            print "Server start result: ", result
    except:
        forwardException(getExceptionMsg())
        logSummary("Failed to start server %s on node %s" % (server['Server'], server['Node']))
        sys.exit(1)

startResults = []
# now we're checking that servers are started
startedServers = 0

for i in range(0, iterationsCount):
    for server in parsedServerList:
        if 'State' in server.keys() and server['State'] == okServerStatus:
            continue
        serverStatus = ''
        try:
            serverStatus = showServerStatus(server['Node'], server['Server'])
        except:
            logSummary("Failed to check state of server %s on node %s" % (server['Server'], server['Node']))
            forwardException(getExceptionMsg())
            sys.exit(1)
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

if startedServers != len(parsedServerList):
    logError("Some servers are failed to start")
    sys.exit(1)


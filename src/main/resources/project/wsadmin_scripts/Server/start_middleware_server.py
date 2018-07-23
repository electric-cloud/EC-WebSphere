$[/myProject/wsadmin_scripts/preamble.py]

# stop middleware server jython script.

# properties retrieval section

parsedServerList = $[/myJobStep/tmpl/parsedServersList/]

errorHandling = '''
$[wasErrorHandling]
'''.strip()

waitTime = $[/myJobStep/tmpl/waitTime/]

# values that defined for script
okServerStatus = 'STARTED'
sleepTime = 5
iterationsCount = waitTime / sleepTime
iterationsCount += 1

# check server states section
for x in range (0, len(parsedServerList)):
    server = parsedServerList[x]
    serverStatus = showServerStatus(server['Node'], server['Server'])
    if serverStatus == okServerStatus:
        print "WARNING: Server %s on Node %s has already %s" % (server['Server'], server['Node'], okServerStatus)
        del parsedServerList[x]
        if errorHandling == 'FATAL':
            print "ERROR: All warnings are fatal errors"
            sys.exit(1)
    # Here we should handle exceptions.
    print serverStatus
# error handling section

if len(parsedServerList) == 0:
    print "WARNING: Nothing to do, all servers are already %s" % (okServerStatus)
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
    print "Node: %s, Server: %s, State: %s" % (server['Node'], server['Server'], server['State'])
print "==="
print "Done."

if startedServers != len(parsedServerList):
    print "Error: Failed to start servers"
    sys.exit(1)


$[/myProject/wsadmin_scripts/preamble.py]

# stop middleware server jython script.

# properties retrieval section

parsedServerList = $[/myJobStep/tmpl/parsedServersList/]

errorHandling = '''
$[wasErrorHandling]
'''.strip()

waitTime = $[/myJobStep/tmpl/waitTime/]

# values that defined for script
okServerStatus = 'Stopped'
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
    print "Node: %s, Server: %s, State: %s" % (server['Node'], server['Server'], server['State'])
print "==="
print "Done."

if stoppedServers != len(parsedServerList):
    print "Error: Failed to stop servers"
    sys.exit(1)
# Partial Stop
# Stopped

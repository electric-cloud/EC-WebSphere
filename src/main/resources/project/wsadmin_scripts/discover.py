# Get list of Cells
cells = AdminConfig.list('Cell').split()

for cell in cells:
 # Get list of Nodes
 nodes = AdminConfig.list('Node', cell).split()
 for node in nodes:
  cname = AdminConfig.showAttribute(cell, 'name')
  nname = AdminConfig.showAttribute(node, 'name')

  # Get list of servers on node. We only need servers of type APPLICATION
  servers = AdminTask.listServers('[-serverType APPLICATION_SERVER -nodeName %s]' % nname).split()


  apps = []
  for server in servers:
   server_name = AdminConfig.showAttribute(server, "name")
   apps +=[ (app, server_name) for app in AdminApp.list("WebSphere:cell=%s,node=%s,server=%s" % (cname, nname, server_name)).split()]

  print '[',
  apps[:] = [ '{ "%s": {"serverName": "%s"}}' % (app, server_name) for (app, server_name) in apps]
  print ",".join(apps),
  print ']'


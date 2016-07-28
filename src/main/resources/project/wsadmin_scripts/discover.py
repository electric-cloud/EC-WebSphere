# Get list of Cells
cells = AdminConfig.list('Cell').splitlines()

for cell in cells:
 cname = AdminConfig.showAttribute(cell, 'name')

 cluster_entries = []
 server_entries = []

 # Get list of Nodes
 nodes = AdminConfig.list('Node', cell).splitlines()
 for node in nodes:
  nname = AdminConfig.showAttribute(node, 'name')
  print 'Node: ' + nname

  # Get list of servers on node. We only need servers of type APPLICATION
  servers = AdminTask.listServers('[-serverType APPLICATION_SERVER -nodeName %s]' % nname).splitlines()

  for server in servers:
   server_name = AdminConfig.showAttribute(server, "name")
   server_apps = AdminApp.list("WebSphere:cell=%s,node=%s,server=%s" % (cname, nname, server_name)).splitlines()

   print '\tServer: ' + server_name
   print '\t\t', server_apps
   server_entries += ['"%s=%s": [ %s ]' % (nname, server_name, ','.join([ '"%s"' % app for app in server_apps]))]

  clusters = AdminConfig.list('ServerCluster').splitlines()

  for cluster in clusters:
   cluster_name = AdminConfig.showAttribute(cluster, "name")
   print '\tCluster: ' + cluster_name
   cluster_apps = AdminApp.list("WebSphere:cell=%s,node=%s,cluster=%s" % (cname, nname, cluster_name)).splitlines()
   print '\t\t', cluster_apps
   cluster_entries += ['"%s": [ %s ]' % (cluster_name, ','.join([ '"%s"' % app for app in cluster_apps]))]

print '{"servers": {%s}, "clusters": {%s}}' % (','.join(server_entries), ','.join(cluster_entries))

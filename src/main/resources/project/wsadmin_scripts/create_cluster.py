import time
result = AdminClusterManagement.createClusterWithoutMember('$[cluster_name]')
print result
result = AdminClusterManagement.createClusterMember("$[cluster_name]", "$[cluster_members]", "")
print result
print 'Deploying an application $[application_name] on $[cluster_name].'
result = AdminApp.install('$[application_path]','[-usedefaultbindings -contextroot $[context_root] -appname $[application_name] -cluster $[cluster_name]]')
print result
AdminConfig.save()

# sync nodes here
# oneone is cluster members
Sync1 = AdminControl.completeObjectName('type=NodeSync,node=oneone,*')
AdminControl.invoke(Sync1, 'sync')

print "\nStarting the cluster $[cluster_name].\n"
cluster = AdminControl.completeObjectName('cell=$[cell_name],type=Cluster,name=$[cluster_name],*')
print cluster
AdminControl.invoke(cluster, 'start')
status = AdminControl.getAttribute(cluster, 'state')
desiredStatus = 'websphere.cluster.running'
print 'Cluster status = ' + status
waitTime = 3
while 1:
    status = AdminControl.getAttribute(cluster, 'state')
    print 'Cluster status = ' + status
    if status==desiredStatus:
        break
    else:
        println 'Waiting for ' + `waitTime` + ' sec.'
        sleep(waitTime)
        waitTime = waitTime + 3


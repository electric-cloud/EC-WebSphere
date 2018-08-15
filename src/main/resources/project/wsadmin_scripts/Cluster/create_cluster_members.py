$[/myProject/wsadmin_scripts/preamble.py]

clusterName = '''
$[wasClusterName]
'''.strip()

genUniquePorts = '''
$[wasClusterMembersGenUniquePorts]
'''.strip()

clusterMemberWeight = '''
$[wasClusterMemberWeight]
'''.strip()

clusterMemberWeight = '''
$[wasClusterMembersList]
'''.strip()

syncNodes = '''
$[wasSyncNodes]
'''.strip()

print "Hello world\n";

$[/myProject/wsadmin_scripts/preamble.py]

clusterName = '''
$[wasClusterName]
'''.strip()

firstMemberName = '''
$[wasFirstClusterMemberName]
'''.strip()

firstMemberWeight = '''
$[wasFirstClusterMemberWeight]
'''.strip()

firstMemberGenUniquePorts = '''
$[wasFirstClusterMemberGenUniquePorts]
'''.strip()

promotionPolicy = '''
$[wasServerResourcesPromotionPolicy]
'''.strip()

creationPolicy = '''
$[wasFirstClusterMemberCreationPolicy]
'''.strip()

templateName = '''
$[wasFirstClusterMemberTemplateName]
'''.strip()

sourceServerName = '''
$[wasSourceServerName]
'''.strip()

syncNodes = '''
$[wasSyncNodes]
'''.strip()

print "Hello World"

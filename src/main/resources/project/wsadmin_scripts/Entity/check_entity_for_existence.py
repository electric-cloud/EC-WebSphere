$[/myProject/wsadmin_scripts/preamble.py]

### Script input parameters
entityType     = '$[wasEntityType]'
entityName     = '$[wasEntityName]'
entityScope    = '$[wasEntityScope]'
handlingLogic  = '$[wasProcedureHandlingLogic]'
outputProperty = '$[wasOutputPropertyPath]'
### End of input parameters


allowedEntities = ['DataSource', 'Library', 'JavaVirtualMachine']
allowedLogics = ['failIfExists', 'failIfDoesNotExist', 'pass']

if entityType not in allowedEntities:
    logError("Entity type %s is not allowed. Allowed types are: %s") % (entityType, allowedEntities)
    os._exit(1)

if handlingLogic not in allowedLogics:
    logError("Logic %s is not allowed. Allowed handling logics are: %s") % (handlingLogic, allowedLogics)
    os._exit(1)

try:
    entityExists = checkEntityForExistence(entityScope, entityType, entityName)
    forwardData("Entity exists: " + str(entityExists))
except:
    forwardException(getExceptionMsg())
    sys.exit(1)

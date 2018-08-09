package dsl

def projName = args.projName
def resName = args.resName
def params = args.params
def procName = args.procName

project projName, {
    procedure procName, {
        resourceName = resName

        step procName, {
            description = ''
            subprocedure = procName
            subproject = '/plugins/EC-WebSphere/project'

            params.each { name, defValue ->
                actualParameter name, '$[' + name + ']'
            }
        }

        params.each {name, defValue ->
        if (name != 'credential' && name != 'dataSourceConnectionCredentials') {
          formalParameter name, defaultValue: defValue, {
            type = 'textarea'
          }
        }
        else {
          hasCredentials = true
          formalParameter name, defaultValue: defValue, {
            type = 'credential'
             }
            }
        }
    }
}

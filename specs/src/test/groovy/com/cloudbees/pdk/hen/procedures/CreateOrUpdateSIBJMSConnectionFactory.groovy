package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateOrUpdateSIBJMSConnectionFactory extends Procedure {

    static CreateOrUpdateSIBJMSConnectionFactory create(Plugin plugin) {
        return new CreateOrUpdateSIBJMSConnectionFactory(procedureName: 'CreateOrUpdateSIBJMSConnectionFactory', plugin: plugin, )
    }


    CreateOrUpdateSIBJMSConnectionFactory flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateOrUpdateSIBJMSConnectionFactory additionalOptions(String additionalOptions) {
        this.addParam('additionalOptions', additionalOptions)
        return this
    }
    
    
    CreateOrUpdateSIBJMSConnectionFactory busName(String busName) {
        this.addParam('busName', busName)
        return this
    }
    
    
    CreateOrUpdateSIBJMSConnectionFactory configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateOrUpdateSIBJMSConnectionFactory factoryAdministrativeDescription(String factoryAdministrativeDescription) {
        this.addParam('factoryAdministrativeDescription', factoryAdministrativeDescription)
        return this
    }
    
    
    CreateOrUpdateSIBJMSConnectionFactory factoryAdministrativeName(String factoryAdministrativeName) {
        this.addParam('factoryAdministrativeName', factoryAdministrativeName)
        return this
    }
    
    
    CreateOrUpdateSIBJMSConnectionFactory factoryScope(String factoryScope) {
        this.addParam('factoryScope', factoryScope)
        return this
    }
    
    
    CreateOrUpdateSIBJMSConnectionFactory factoryType(String factoryType) {
        this.addParam('factoryType', factoryType)
        return this
    }
    
    
    CreateOrUpdateSIBJMSConnectionFactory jndiName(String jndiName) {
        this.addParam('jndiName', jndiName)
        return this
    }
    
    
    
    
}
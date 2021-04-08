package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateOrUpdateSIBJMSActivationSpec extends Procedure {

    static CreateOrUpdateSIBJMSActivationSpec create(Plugin plugin) {
        return new CreateOrUpdateSIBJMSActivationSpec(procedureName: 'CreateOrUpdateSIBJMSActivationSpec', plugin: plugin, )
    }


    CreateOrUpdateSIBJMSActivationSpec flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateOrUpdateSIBJMSActivationSpec additionalOptions(String additionalOptions) {
        this.addParam('additionalOptions', additionalOptions)
        return this
    }
    
    
    CreateOrUpdateSIBJMSActivationSpec configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateOrUpdateSIBJMSActivationSpec destinationJndiName(String destinationJndiName) {
        this.addParam('destinationJndiName', destinationJndiName)
        return this
    }
    
    
    CreateOrUpdateSIBJMSActivationSpec destinationType(String destinationType) {
        this.addParam('destinationType', destinationType)
        return this
    }
    
    
    CreateOrUpdateSIBJMSActivationSpec jndiName(String jndiName) {
        this.addParam('jndiName', jndiName)
        return this
    }
    
    
    CreateOrUpdateSIBJMSActivationSpec messageSelector(String messageSelector) {
        this.addParam('messageSelector', messageSelector)
        return this
    }
    
    
    CreateOrUpdateSIBJMSActivationSpec specAdministrativeDescription(String specAdministrativeDescription) {
        this.addParam('specAdministrativeDescription', specAdministrativeDescription)
        return this
    }
    
    
    CreateOrUpdateSIBJMSActivationSpec specAdministrativeName(String specAdministrativeName) {
        this.addParam('specAdministrativeName', specAdministrativeName)
        return this
    }
    
    
    CreateOrUpdateSIBJMSActivationSpec specScope(String specScope) {
        this.addParam('specScope', specScope)
        return this
    }
    
    
    
    
}
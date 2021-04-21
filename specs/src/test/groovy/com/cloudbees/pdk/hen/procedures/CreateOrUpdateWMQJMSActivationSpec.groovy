package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateOrUpdateWMQJMSActivationSpec extends Procedure {

    static CreateOrUpdateWMQJMSActivationSpec create(Plugin plugin) {
        return new CreateOrUpdateWMQJMSActivationSpec(procedureName: 'CreateOrUpdateWMQJMSActivationSpec', plugin: plugin, )
    }


    CreateOrUpdateWMQJMSActivationSpec flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateOrUpdateWMQJMSActivationSpec additionalOptions(String additionalOptions) {
        this.addParam('additionalOptions', additionalOptions)
        return this
    }
    
    
    CreateOrUpdateWMQJMSActivationSpec clientChannelDefinitionQueueManager(String clientChannelDefinitionQueueManager) {
        this.addParam('clientChannelDefinitionQueueManager', clientChannelDefinitionQueueManager)
        return this
    }
    
    
    CreateOrUpdateWMQJMSActivationSpec clientChannelDefinitionUrl(String clientChannelDefinitionUrl) {
        this.addParam('clientChannelDefinitionUrl', clientChannelDefinitionUrl)
        return this
    }
    
    
    CreateOrUpdateWMQJMSActivationSpec configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateOrUpdateWMQJMSActivationSpec destinationJndiName(String destinationJndiName) {
        this.addParam('destinationJndiName', destinationJndiName)
        return this
    }
    
    
    CreateOrUpdateWMQJMSActivationSpec destinationJndiType(String destinationJndiType) {
        this.addParam('destinationJndiType', destinationJndiType)
        return this
    }
    
    
    CreateOrUpdateWMQJMSActivationSpec jndiName(String jndiName) {
        this.addParam('jndiName', jndiName)
        return this
    }
    
    
    CreateOrUpdateWMQJMSActivationSpec specAdministrativeDescription(String specAdministrativeDescription) {
        this.addParam('specAdministrativeDescription', specAdministrativeDescription)
        return this
    }
    
    
    CreateOrUpdateWMQJMSActivationSpec specAdministrativeName(String specAdministrativeName) {
        this.addParam('specAdministrativeName', specAdministrativeName)
        return this
    }
    
    
    CreateOrUpdateWMQJMSActivationSpec specScope(String specScope) {
        this.addParam('specScope', specScope)
        return this
    }
    
    
    
    
}
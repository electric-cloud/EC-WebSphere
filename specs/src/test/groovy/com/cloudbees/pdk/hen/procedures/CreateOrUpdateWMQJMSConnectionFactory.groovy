package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateOrUpdateWMQJMSConnectionFactory extends Procedure {

    static CreateOrUpdateWMQJMSConnectionFactory create(Plugin plugin) {
        return new CreateOrUpdateWMQJMSConnectionFactory(procedureName: 'CreateOrUpdateWMQJMSConnectionFactory', plugin: plugin, )
    }


    CreateOrUpdateWMQJMSConnectionFactory flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateOrUpdateWMQJMSConnectionFactory additionalOptions(String additionalOptions) {
        this.addParam('additionalOptions', additionalOptions)
        return this
    }
    
    
    CreateOrUpdateWMQJMSConnectionFactory clientChannelDefinitionQueueManager(String clientChannelDefinitionQueueManager) {
        this.addParam('clientChannelDefinitionQueueManager', clientChannelDefinitionQueueManager)
        return this
    }
    
    
    CreateOrUpdateWMQJMSConnectionFactory clientChannelDefinitionUrl(String clientChannelDefinitionUrl) {
        this.addParam('clientChannelDefinitionUrl', clientChannelDefinitionUrl)
        return this
    }
    
    
    CreateOrUpdateWMQJMSConnectionFactory configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateOrUpdateWMQJMSConnectionFactory factoryAdministrativeDescription(String factoryAdministrativeDescription) {
        this.addParam('factoryAdministrativeDescription', factoryAdministrativeDescription)
        return this
    }
    
    
    CreateOrUpdateWMQJMSConnectionFactory factoryAdministrativeName(String factoryAdministrativeName) {
        this.addParam('factoryAdministrativeName', factoryAdministrativeName)
        return this
    }
    
    
    CreateOrUpdateWMQJMSConnectionFactory factoryScope(String factoryScope) {
        this.addParam('factoryScope', factoryScope)
        return this
    }
    
    
    CreateOrUpdateWMQJMSConnectionFactory factoryType(String factoryType) {
        this.addParam('factoryType', factoryType)
        return this
    }
    
    
    CreateOrUpdateWMQJMSConnectionFactory jndiName(String jndiName) {
        this.addParam('jndiName', jndiName)
        return this
    }
    
    
    
    
}
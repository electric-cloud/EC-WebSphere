package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateOrUpdateJMSQueue extends Procedure {

    static CreateOrUpdateJMSQueue create(Plugin plugin) {
        return new CreateOrUpdateJMSQueue(procedureName: 'CreateOrUpdateJMSQueue', plugin: plugin, )
    }


    CreateOrUpdateJMSQueue flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateOrUpdateJMSQueue additionalOptions(String additionalOptions) {
        this.addParam('additionalOptions', additionalOptions)
        return this
    }
    
    
    CreateOrUpdateJMSQueue configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateOrUpdateJMSQueue jndiName(String jndiName) {
        this.addParam('jndiName', jndiName)
        return this
    }
    
    
    CreateOrUpdateJMSQueue messagingSystemType(String messagingSystemType) {
        this.addParam('messagingSystemType', messagingSystemType)
        return this
    }
    
    CreateOrUpdateJMSQueue messagingSystemType(MessagingSystemTypeOptions messagingSystemType) {
        this.addParam('messagingSystemType', messagingSystemType.toString())
        return this
    }
    
    
    CreateOrUpdateJMSQueue queueAdministrativeDescription(String queueAdministrativeDescription) {
        this.addParam('queueAdministrativeDescription', queueAdministrativeDescription)
        return this
    }
    
    
    CreateOrUpdateJMSQueue queueAdministrativeName(String queueAdministrativeName) {
        this.addParam('queueAdministrativeName', queueAdministrativeName)
        return this
    }
    
    
    CreateOrUpdateJMSQueue queueManagerName(String queueManagerName) {
        this.addParam('queueManagerName', queueManagerName)
        return this
    }
    
    
    CreateOrUpdateJMSQueue queueName(String queueName) {
        this.addParam('queueName', queueName)
        return this
    }
    
    
    CreateOrUpdateJMSQueue queueScope(String queueScope) {
        this.addParam('queueScope', queueScope)
        return this
    }
    
    
    
    
    enum MessagingSystemTypeOptions {
    
    WMQ("WMQ"),
    
    SIB("SIB")
    
    private String value
    MessagingSystemTypeOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}
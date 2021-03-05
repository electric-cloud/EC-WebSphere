package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateOrUpdateJMSTopic extends Procedure {

    static CreateOrUpdateJMSTopic create(Plugin plugin) {
        return new CreateOrUpdateJMSTopic(procedureName: 'CreateOrUpdateJMSTopic', plugin: plugin, )
    }


    CreateOrUpdateJMSTopic flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateOrUpdateJMSTopic additionalOptions(String additionalOptions) {
        this.addParam('additionalOptions', additionalOptions)
        return this
    }
    
    
    CreateOrUpdateJMSTopic configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateOrUpdateJMSTopic jndiName(String jndiName) {
        this.addParam('jndiName', jndiName)
        return this
    }
    
    
    CreateOrUpdateJMSTopic messagingSystemType(String messagingSystemType) {
        this.addParam('messagingSystemType', messagingSystemType)
        return this
    }
    
    CreateOrUpdateJMSTopic messagingSystemType(MessagingSystemTypeOptions messagingSystemType) {
        this.addParam('messagingSystemType', messagingSystemType.toString())
        return this
    }
    
    
    CreateOrUpdateJMSTopic topicAdministrativeDescription(String topicAdministrativeDescription) {
        this.addParam('topicAdministrativeDescription', topicAdministrativeDescription)
        return this
    }
    
    
    CreateOrUpdateJMSTopic topicAdministrativeName(String topicAdministrativeName) {
        this.addParam('topicAdministrativeName', topicAdministrativeName)
        return this
    }
    
    
    CreateOrUpdateJMSTopic topicName(String topicName) {
        this.addParam('topicName', topicName)
        return this
    }
    
    
    CreateOrUpdateJMSTopic topicScope(String topicScope) {
        this.addParam('topicScope', topicScope)
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
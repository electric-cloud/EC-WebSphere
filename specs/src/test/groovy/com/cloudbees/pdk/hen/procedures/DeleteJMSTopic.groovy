package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeleteJMSTopic extends Procedure {

    static DeleteJMSTopic create(Plugin plugin) {
        return new DeleteJMSTopic(procedureName: 'DeleteJMSTopic', plugin: plugin, )
    }


    DeleteJMSTopic flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeleteJMSTopic configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    DeleteJMSTopic messagingSystemType(String messagingSystemType) {
        this.addParam('messagingSystemType', messagingSystemType)
        return this
    }
    
    DeleteJMSTopic messagingSystemType(MessagingSystemTypeOptions messagingSystemType) {
        this.addParam('messagingSystemType', messagingSystemType.toString())
        return this
    }
    
    
    DeleteJMSTopic topicAdministrativeName(String topicAdministrativeName) {
        this.addParam('topicAdministrativeName', topicAdministrativeName)
        return this
    }
    
    
    DeleteJMSTopic topicScope(String topicScope) {
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
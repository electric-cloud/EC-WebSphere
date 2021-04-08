package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeleteJMSQueue extends Procedure {

    static DeleteJMSQueue create(Plugin plugin) {
        return new DeleteJMSQueue(procedureName: 'DeleteJMSQueue', plugin: plugin, )
    }


    DeleteJMSQueue flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeleteJMSQueue configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    DeleteJMSQueue messagingSystemType(String messagingSystemType) {
        this.addParam('messagingSystemType', messagingSystemType)
        return this
    }
    
    DeleteJMSQueue messagingSystemType(MessagingSystemTypeOptions messagingSystemType) {
        this.addParam('messagingSystemType', messagingSystemType.toString())
        return this
    }
    
    
    DeleteJMSQueue queueAdministrativeName(String queueAdministrativeName) {
        this.addParam('queueAdministrativeName', queueAdministrativeName)
        return this
    }
    
    
    DeleteJMSQueue queueScope(String queueScope) {
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
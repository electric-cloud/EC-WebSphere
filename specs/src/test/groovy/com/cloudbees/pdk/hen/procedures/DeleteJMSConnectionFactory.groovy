package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeleteJMSConnectionFactory extends Procedure {

    static DeleteJMSConnectionFactory create(Plugin plugin) {
        return new DeleteJMSConnectionFactory(procedureName: 'DeleteJMSConnectionFactory', plugin: plugin, )
    }


    DeleteJMSConnectionFactory flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeleteJMSConnectionFactory configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    DeleteJMSConnectionFactory factoryAdministrativeName(String factoryAdministrativeName) {
        this.addParam('factoryAdministrativeName', factoryAdministrativeName)
        return this
    }
    
    
    DeleteJMSConnectionFactory factoryScope(String factoryScope) {
        this.addParam('factoryScope', factoryScope)
        return this
    }
    
    
    DeleteJMSConnectionFactory messagingSystemType(String messagingSystemType) {
        this.addParam('messagingSystemType', messagingSystemType)
        return this
    }
    
    DeleteJMSConnectionFactory messagingSystemType(MessagingSystemTypeOptions messagingSystemType) {
        this.addParam('messagingSystemType', messagingSystemType.toString())
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
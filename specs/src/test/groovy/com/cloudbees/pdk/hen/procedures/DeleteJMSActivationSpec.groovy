package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeleteJMSActivationSpec extends Procedure {

    static DeleteJMSActivationSpec create(Plugin plugin) {
        return new DeleteJMSActivationSpec(procedureName: 'DeleteJMSActivationSpec', plugin: plugin, )
    }


    DeleteJMSActivationSpec flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeleteJMSActivationSpec activationSpecAdministrativeName(String activationSpecAdministrativeName) {
        this.addParam('activationSpecAdministrativeName', activationSpecAdministrativeName)
        return this
    }
    
    
    DeleteJMSActivationSpec activationSpecScope(String activationSpecScope) {
        this.addParam('activationSpecScope', activationSpecScope)
        return this
    }
    
    
    DeleteJMSActivationSpec configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    DeleteJMSActivationSpec messagingSystemType(String messagingSystemType) {
        this.addParam('messagingSystemType', messagingSystemType)
        return this
    }
    
    DeleteJMSActivationSpec messagingSystemType(MessagingSystemTypeOptions messagingSystemType) {
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
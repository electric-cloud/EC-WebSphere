package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateMailSession extends Procedure {

    static CreateMailSession create(Plugin plugin) {
        return new CreateMailSession(procedureName: 'CreateMailSession', plugin: plugin, credentials: [
            
            'mailStoreUser': null,
            
            'mailTransportUser': null,
            
        ])
    }


    CreateMailSession flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateMailSession category(String category) {
        this.addParam('category', category)
        return this
    }
    
    
    CreateMailSession configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    CreateMailSession debug(String debug) {
        this.addParam('debug', debug)
        return this
    }
    
    
    CreateMailSession mailFrom(String mailFrom) {
        this.addParam('mailFrom', mailFrom)
        return this
    }
    
    
    CreateMailSession mailSessionDesc(String mailSessionDesc) {
        this.addParam('mailSessionDesc', mailSessionDesc)
        return this
    }
    
    
    CreateMailSession mailSessionJNDIName(String mailSessionJNDIName) {
        this.addParam('mailSessionJNDIName', mailSessionJNDIName)
        return this
    }
    
    
    CreateMailSession mailSessionName(String mailSessionName) {
        this.addParam('mailSessionName', mailSessionName)
        return this
    }
    
    
    CreateMailSession mailStoreHost(String mailStoreHost) {
        this.addParam('mailStoreHost', mailStoreHost)
        return this
    }
    
    
    CreateMailSession mailStorePort(String mailStorePort) {
        this.addParam('mailStorePort', mailStorePort)
        return this
    }
    
    
    CreateMailSession mailStoreProtocol(String mailStoreProtocol) {
        this.addParam('mailStoreProtocol', mailStoreProtocol)
        return this
    }
    
    CreateMailSession mailStoreProtocol(MailStoreProtocolOptions mailStoreProtocol) {
        this.addParam('mailStoreProtocol', mailStoreProtocol.toString())
        return this
    }
    
    
    CreateMailSession mailTransportHost(String mailTransportHost) {
        this.addParam('mailTransportHost', mailTransportHost)
        return this
    }
    
    
    CreateMailSession mailTransportPort(String mailTransportPort) {
        this.addParam('mailTransportPort', mailTransportPort)
        return this
    }
    
    
    CreateMailSession mailTransportProtocol(String mailTransportProtocol) {
        this.addParam('mailTransportProtocol', mailTransportProtocol)
        return this
    }
    
    CreateMailSession mailTransportProtocol(MailTransportProtocolOptions mailTransportProtocol) {
        this.addParam('mailTransportProtocol', mailTransportProtocol.toString())
        return this
    }
    
    
    CreateMailSession scope(String scope) {
        this.addParam('scope', scope)
        return this
    }
    
    
    CreateMailSession strict(String strict) {
        this.addParam('strict', strict)
        return this
    }
    
    
    CreateMailSession wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    CreateMailSession mailStoreUser(String user, String password) {
        this.addCredential('mailStoreUser', user, password)
        return this
    }

    CreateMailSession mailStoreUserReference(String path) {
        this.addCredentialReference('mailStoreUser', path)
        return this
    }
    
    CreateMailSession mailTransportUser(String user, String password) {
        this.addCredential('mailTransportUser', user, password)
        return this
    }

    CreateMailSession mailTransportUserReference(String path) {
        this.addCredentialReference('mailTransportUser', path)
        return this
    }
    
    
    enum MailStoreProtocolOptions {
    
    IMAP("imap(?!s)"),
    
    IMAP_SSL("imaps"),
    
    POP3("pop3(?!s)"),
    
    POP3_SSL("pop3s")
    
    private String value
    MailStoreProtocolOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
    enum MailTransportProtocolOptions {
    
    SMTP("smtp(?!s)"),
    
    SMTP_SSL("smtps")
    
    private String value
    MailTransportProtocolOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}
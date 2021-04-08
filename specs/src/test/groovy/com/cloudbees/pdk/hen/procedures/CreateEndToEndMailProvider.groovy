package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateEndToEndMailProvider extends Procedure {

    static CreateEndToEndMailProvider create(Plugin plugin) {
        return new CreateEndToEndMailProvider(procedureName: 'CreateEndToEndMailProvider', plugin: plugin, credentials: [
            
            'mailStoreUser': null,
            
            'mailTransportUser': null,
            
        ])
    }


    CreateEndToEndMailProvider flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateEndToEndMailProvider category(String category) {
        this.addParam('category', category)
        return this
    }
    
    
    CreateEndToEndMailProvider className(String className) {
        this.addParam('className', className)
        return this
    }
    
    
    CreateEndToEndMailProvider classpath(String classpath) {
        this.addParam('classpath', classpath)
        return this
    }
    
    
    CreateEndToEndMailProvider configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    CreateEndToEndMailProvider custPropName(String custPropName) {
        this.addParam('custPropName', custPropName)
        return this
    }
    
    
    CreateEndToEndMailProvider custPropValue(String custPropValue) {
        this.addParam('custPropValue', custPropValue)
        return this
    }
    
    
    CreateEndToEndMailProvider debug(String debug) {
        this.addParam('debug', debug)
        return this
    }
    
    
    CreateEndToEndMailProvider isolatedClassLoader(String isolatedClassLoader) {
        this.addParam('isolatedClassLoader', isolatedClassLoader)
        return this
    }
    
    
    CreateEndToEndMailProvider mailProviderDesc(String mailProviderDesc) {
        this.addParam('mailProviderDesc', mailProviderDesc)
        return this
    }
    
    
    CreateEndToEndMailProvider mailProviderName(String mailProviderName) {
        this.addParam('mailProviderName', mailProviderName)
        return this
    }
    
    
    CreateEndToEndMailProvider mailSessionDesc(String mailSessionDesc) {
        this.addParam('mailSessionDesc', mailSessionDesc)
        return this
    }
    
    
    CreateEndToEndMailProvider mailSessionJNDIName(String mailSessionJNDIName) {
        this.addParam('mailSessionJNDIName', mailSessionJNDIName)
        return this
    }
    
    
    CreateEndToEndMailProvider mailSessionName(String mailSessionName) {
        this.addParam('mailSessionName', mailSessionName)
        return this
    }
    
    
    CreateEndToEndMailProvider mailStoreHost(String mailStoreHost) {
        this.addParam('mailStoreHost', mailStoreHost)
        return this
    }
    
    
    CreateEndToEndMailProvider mailStorePort(String mailStorePort) {
        this.addParam('mailStorePort', mailStorePort)
        return this
    }
    
    
    CreateEndToEndMailProvider mailTransportHost(String mailTransportHost) {
        this.addParam('mailTransportHost', mailTransportHost)
        return this
    }
    
    
    CreateEndToEndMailProvider mailTransportPort(String mailTransportPort) {
        this.addParam('mailTransportPort', mailTransportPort)
        return this
    }
    
    
    CreateEndToEndMailProvider protocolProviderName(String protocolProviderName) {
        this.addParam('protocolProviderName', protocolProviderName)
        return this
    }
    
    
    CreateEndToEndMailProvider protocolProviderType(String protocolProviderType) {
        this.addParam('protocolProviderType', protocolProviderType)
        return this
    }
    
    CreateEndToEndMailProvider protocolProviderType(ProtocolProviderTypeOptions protocolProviderType) {
        this.addParam('protocolProviderType', protocolProviderType.toString())
        return this
    }
    
    
    CreateEndToEndMailProvider scope(String scope) {
        this.addParam('scope', scope)
        return this
    }
    
    
    CreateEndToEndMailProvider strict(String strict) {
        this.addParam('strict', strict)
        return this
    }
    
    
    CreateEndToEndMailProvider wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    CreateEndToEndMailProvider mailStoreUser(String user, String password) {
        this.addCredential('mailStoreUser', user, password)
        return this
    }

    CreateEndToEndMailProvider mailStoreUserReference(String path) {
        this.addCredentialReference('mailStoreUser', path)
        return this
    }
    
    CreateEndToEndMailProvider mailTransportUser(String user, String password) {
        this.addCredential('mailTransportUser', user, password)
        return this
    }

    CreateEndToEndMailProvider mailTransportUserReference(String path) {
        this.addCredentialReference('mailTransportUser', path)
        return this
    }
    
    
    enum ProtocolProviderTypeOptions {
    
    STORE("STORE"),
    
    TRANSPORT("TRANSPORT")
    
    private String value
    ProtocolProviderTypeOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}
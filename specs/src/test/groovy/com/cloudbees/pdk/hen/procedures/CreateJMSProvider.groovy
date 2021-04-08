package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateJMSProvider extends Procedure {

    static CreateJMSProvider create(Plugin plugin) {
        return new CreateJMSProvider(procedureName: 'CreateJMSProvider', plugin: plugin, )
    }


    CreateJMSProvider flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateJMSProvider classpath(String classpath) {
        this.addParam('classpath', classpath)
        return this
    }
    
    
    CreateJMSProvider configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    CreateJMSProvider description(String description) {
        this.addParam('description', description)
        return this
    }
    
    
    CreateJMSProvider extContextFactory(String extContextFactory) {
        this.addParam('extContextFactory', extContextFactory)
        return this
    }
    
    
    CreateJMSProvider extProviderURL(String extProviderURL) {
        this.addParam('extProviderURL', extProviderURL)
        return this
    }
    
    
    CreateJMSProvider isolatedClassLoader(String isolatedClassLoader) {
        this.addParam('isolatedClassLoader', isolatedClassLoader)
        return this
    }
    
    
    CreateJMSProvider jmsProvider(String jmsProvider) {
        this.addParam('jmsProvider', jmsProvider)
        return this
    }
    
    
    CreateJMSProvider nativepath(String nativepath) {
        this.addParam('nativepath', nativepath)
        return this
    }
    
    
    CreateJMSProvider propertySet(String propertySet) {
        this.addParam('propertySet', propertySet)
        return this
    }
    
    
    CreateJMSProvider providerType(String providerType) {
        this.addParam('providerType', providerType)
        return this
    }
    
    
    CreateJMSProvider scope(String scope) {
        this.addParam('scope', scope)
        return this
    }
    
    
    CreateJMSProvider supportsASF(String supportsASF) {
        this.addParam('supportsASF', supportsASF)
        return this
    }
    
    
    CreateJMSProvider wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    
}
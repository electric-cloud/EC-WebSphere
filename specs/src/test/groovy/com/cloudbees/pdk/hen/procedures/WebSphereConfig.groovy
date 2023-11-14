package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

//generated
class WebSphereConfig extends Procedure {

    static WebSphereConfig create(Plugin plugin) {
        return new WebSphereConfig(procedureName: 'CreateConfiguration', plugin: plugin, credentials: [
            
            'credential': null,
            
        ])
    }


    WebSphereConfig flush() {
        this.flushParams()
        this.contextUser = null
        return this
    }

    WebSphereConfig withUser(User user) {
        this.contextUser = user
        return this
    }


    WebSphereConfig clone() {
        WebSphereConfig cloned = new WebSphereConfig(procedureName: 'CreateConfiguration', plugin: plugin, credentials: [
                    
                    'credential': null,
                    
                ])
        cloned.parameters = this.parameters.clone()
        return cloned
    }

    //Generated
    
    WebSphereConfig config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    WebSphereConfig conntype(String conntype) {
        this.addParam('conntype', conntype)
        return this
    }
    
    
    WebSphereConfig debug(boolean debug) {
        this.addParam('debug', debug)
        return this
    }


    WebSphereConfig debug(String debug) {
        this.addParam('debug', debug)
        return this
    }


    WebSphereConfig testconnection(boolean testconnection) {
        this.addParam('test_connection', testconnection)
        return this
    }
    
    
    WebSphereConfig testconnectionres(String testconnectionres) {
        this.addParam('test_connection_res', testconnectionres)
        return this
    }
    
    
    WebSphereConfig websphereport(String websphereport) {
        this.addParam('websphere_port', websphereport)
        return this
    }
    
    
    WebSphereConfig websphereurl(String websphereurl) {
        this.addParam('websphere_url', websphereurl)
        return this
    }
    
    
    WebSphereConfig wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    WebSphereConfig credential(String user, String password) {
        this.addCredential('credential', user, password)
        return this
    }

    WebSphereConfig credentialReference(String path) {
        this.addCredentialReference('credential', path)
        return this
    }
    
    
}
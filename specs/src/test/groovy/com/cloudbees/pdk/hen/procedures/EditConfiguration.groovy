package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class EditConfiguration extends Procedure {

    static EditConfiguration create(Plugin plugin) {
        return new EditConfiguration(procedureName: 'EditConfiguration', plugin: plugin, credentials: [
            
            'credential': null,
            
        ])
    }


    EditConfiguration flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    EditConfiguration config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    EditConfiguration conntype(String conntype) {
        this.addParam('conntype', conntype)
        return this
    }
    
    
    EditConfiguration debug(boolean debug) {
        this.addParam('debug', debug)
        return this
    }


    EditConfiguration debug(String debug) {
        this.addParam('debug', debug)
        return this
    }


    EditConfiguration testconnection(boolean testconnection) {
        this.addParam('test_connection', testconnection)
        return this
    }
    
    
    EditConfiguration testconnectionres(String testconnectionres) {
        this.addParam('test_connection_res', testconnectionres)
        return this
    }
    
    
    EditConfiguration websphereport(String websphereport) {
        this.addParam('websphere_port', websphereport)
        return this
    }
    
    
    EditConfiguration websphereurl(String websphereurl) {
        this.addParam('websphere_url', websphereurl)
        return this
    }
    
    
    EditConfiguration wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    EditConfiguration credential(String user, String password) {
        this.addCredential('credential', user, password)
        return this
    }

    EditConfiguration credentialReference(String path) {
        this.addCredentialReference('credential', path)
        return this
    }
    
    
}
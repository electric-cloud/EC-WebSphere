package com.cloudbees.pdk.hen.procedures

import com.cloudbees.pdk.hen.*

//generated
class TestConfiguration extends Procedure {

    static TestConfiguration create(Plugin plugin) {
        return new TestConfiguration(procedureName: 'TestConfiguration', plugin: plugin, credentials: [
            
            'credential': null,
            
        ])
    }


    TestConfiguration flush() {
        this.flushParams()
        this.contextUser = null
        return this
    }


    TestConfiguration withUser(User user) {
        this.contextUser = user
        return this
    }


    TestConfiguration clone() {
        TestConfiguration cloned = new TestConfiguration(procedureName: 'TestConfiguration', plugin: plugin, credentials: [
                    
                    'credential': null,
                    
                ])
        cloned.parameters = this.parameters.clone()
        return cloned
    }

    //Generated
    
    TestConfiguration conntype(String conntype) {
        this.addParam('conntype', conntype)
        return this
    }
    
    
    TestConfiguration debug(boolean debug) {
        this.addParam('debug', debug)
        return this
    }
    
    
    TestConfiguration testconnectionres(String testconnectionres) {
        this.addParam('test_connection_res', testconnectionres)
        return this
    }
    
    
    TestConfiguration websphereport(String websphereport) {
        this.addParam('websphere_port', websphereport)
        return this
    }
    
    
    TestConfiguration websphereurl(String websphereurl) {
        this.addParam('websphere_url', websphereurl)
        return this
    }
    
    
    TestConfiguration wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    TestConfiguration credential(String user, String password) {
        this.addCredential('credential', user, password)
        return this
    }


    TestConfiguration credentialReference(String path) {
        this.addCredentialReference('credential', path)
        return this
    }
    
}
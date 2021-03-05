package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CheckPageStatus extends Procedure {

    static CheckPageStatus create(Plugin plugin) {
        return new CheckPageStatus(procedureName: 'CheckPageStatus', plugin: plugin, credentials: [
            
            'credentialName': null,
            
        ])
    }


    CheckPageStatus flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CheckPageStatus maxelapsedtime(String maxelapsedtime) {
        this.addParam('maxelapsedtime', maxelapsedtime)
        return this
    }
    
    
    CheckPageStatus successcriteria(String successcriteria) {
        this.addParam('successcriteria', successcriteria)
        return this
    }
    
    CheckPageStatus successcriteria(SuccesscriteriaOptions successcriteria) {
        this.addParam('successcriteria', successcriteria.toString())
        return this
    }
    
    
    CheckPageStatus targeturl(String targeturl) {
        this.addParam('targeturl', targeturl)
        return this
    }
    
    
    
    CheckPageStatus credentialName(String user, String password) {
        this.addCredential('credentialName', user, password)
        return this
    }

    CheckPageStatus credentialNameReference(String path) {
        this.addCredentialReference('credentialName', path)
        return this
    }
    
    
    enum SuccesscriteriaOptions {
    
    PAGE_FOUND("pagefound"),
    
    PAGE_NOT_FOUND("pagenotfound")
    
    private String value
    SuccesscriteriaOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}
package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CheckServerStatus extends Procedure {

    static CheckServerStatus create(Plugin plugin) {
        return new CheckServerStatus(procedureName: 'CheckServerStatus', plugin: plugin, )
    }


    CheckServerStatus flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CheckServerStatus configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CheckServerStatus maxelapsedtime(String maxelapsedtime) {
        this.addParam('maxelapsedtime', maxelapsedtime)
        return this
    }
    
    
    CheckServerStatus successcriteria(String successcriteria) {
        this.addParam('successcriteria', successcriteria)
        return this
    }
    
    CheckServerStatus successcriteria(SuccesscriteriaOptions successcriteria) {
        this.addParam('successcriteria', successcriteria.toString())
        return this
    }
    
    
    
    
    enum SuccesscriteriaOptions {
    
    SERVER_RESPONDS("response"),
    
    SERVER_DOESN_T_RESPOND("noresponse")
    
    private String value
    SuccesscriteriaOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}
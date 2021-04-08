package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StopServer extends Procedure {

    static StopServer create(Plugin plugin) {
        return new StopServer(procedureName: 'StopServer', plugin: plugin, )
    }


    StopServer flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StopServer additionalcommands(String additionalcommands) {
        this.addParam('additionalcommands', additionalcommands)
        return this
    }
    
    
    StopServer configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    StopServer instancename(String instancename) {
        this.addParam('instancename', instancename)
        return this
    }
    
    
    StopServer scriptlocation(String scriptlocation) {
        this.addParam('scriptlocation', scriptlocation)
        return this
    }
    
    
    
    
}
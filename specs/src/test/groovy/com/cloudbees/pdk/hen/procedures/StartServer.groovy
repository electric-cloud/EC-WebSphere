package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StartServer extends Procedure {

    static StartServer create(Plugin plugin) {
        return new StartServer(procedureName: 'StartServer', plugin: plugin, )
    }


    StartServer flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StartServer additionalcommands(String additionalcommands) {
        this.addParam('additionalcommands', additionalcommands)
        return this
    }
    
    
    StartServer instancename(String instancename) {
        this.addParam('instancename', instancename)
        return this
    }
    
    
    StartServer scriptlocation(String scriptlocation) {
        this.addParam('scriptlocation', scriptlocation)
        return this
    }
    
    
    
    
}
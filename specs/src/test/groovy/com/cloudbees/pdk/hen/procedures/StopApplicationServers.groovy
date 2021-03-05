package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StopApplicationServers extends Procedure {

    static StopApplicationServers create(Plugin plugin) {
        return new StopApplicationServers(procedureName: 'StopApplicationServers', plugin: plugin, )
    }


    StopApplicationServers flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StopApplicationServers configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    StopApplicationServers wasServersList(String wasServersList) {
        this.addParam('wasServersList', wasServersList)
        return this
    }
    
    
    StopApplicationServers wasWaitTime(String wasWaitTime) {
        this.addParam('wasWaitTime', wasWaitTime)
        return this
    }
    
    
    
    
}
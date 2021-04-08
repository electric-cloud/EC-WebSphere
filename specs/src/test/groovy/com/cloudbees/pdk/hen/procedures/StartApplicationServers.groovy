package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StartApplicationServers extends Procedure {

    static StartApplicationServers create(Plugin plugin) {
        return new StartApplicationServers(procedureName: 'StartApplicationServers', plugin: plugin, )
    }


    StartApplicationServers flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StartApplicationServers configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    StartApplicationServers wasServersList(String wasServersList) {
        this.addParam('wasServersList', wasServersList)
        return this
    }
    
    
    StartApplicationServers wasWaitTime(String wasWaitTime) {
        this.addParam('wasWaitTime', wasWaitTime)
        return this
    }
    
    
    
    
}
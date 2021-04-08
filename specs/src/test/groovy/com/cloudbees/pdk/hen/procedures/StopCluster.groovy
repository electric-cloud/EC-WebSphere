package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StopCluster extends Procedure {

    static StopCluster create(Plugin plugin) {
        return new StopCluster(procedureName: 'StopCluster', plugin: plugin, )
    }


    StopCluster flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StopCluster configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    StopCluster wasClusterName(String wasClusterName) {
        this.addParam('wasClusterName', wasClusterName)
        return this
    }
    
    
    StopCluster wasRippleStart(String wasRippleStart) {
        this.addParam('wasRippleStart', wasRippleStart)
        return this
    }
    
    
    StopCluster wasTimeout(String wasTimeout) {
        this.addParam('wasTimeout', wasTimeout)
        return this
    }
    
    
    
    
}
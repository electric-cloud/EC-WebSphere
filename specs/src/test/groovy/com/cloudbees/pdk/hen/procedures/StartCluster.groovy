package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StartCluster extends Procedure {

    static StartCluster create(Plugin plugin) {
        return new StartCluster(procedureName: 'StartCluster', plugin: plugin, )
    }


    StartCluster flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StartCluster configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    StartCluster wasClusterName(String wasClusterName) {
        this.addParam('wasClusterName', wasClusterName)
        return this
    }
    
    
    StartCluster wasTimeout(String wasTimeout) {
        this.addParam('wasTimeout', wasTimeout)
        return this
    }
    
    
    
    
}
package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class RemoveClusterMembers extends Procedure {

    static RemoveClusterMembers create(Plugin plugin) {
        return new RemoveClusterMembers(procedureName: 'RemoveClusterMembers', plugin: plugin, )
    }


    RemoveClusterMembers flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    RemoveClusterMembers configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    RemoveClusterMembers wasClusterMembers(String wasClusterMembers) {
        this.addParam('wasClusterMembers', wasClusterMembers)
        return this
    }
    
    
    RemoveClusterMembers wasClusterName(String wasClusterName) {
        this.addParam('wasClusterName', wasClusterName)
        return this
    }
    
    
    RemoveClusterMembers wasSyncNodes(String wasSyncNodes) {
        this.addParam('wasSyncNodes', wasSyncNodes)
        return this
    }
    
    
    
    
}
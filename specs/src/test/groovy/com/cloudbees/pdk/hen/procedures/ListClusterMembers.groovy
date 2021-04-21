package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class ListClusterMembers extends Procedure {

    static ListClusterMembers create(Plugin plugin) {
        return new ListClusterMembers(procedureName: 'ListClusterMembers', plugin: plugin, )
    }


    ListClusterMembers flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    ListClusterMembers configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    ListClusterMembers wasClusterName(String wasClusterName) {
        this.addParam('wasClusterName', wasClusterName)
        return this
    }
    
    
    ListClusterMembers wasOutputPropertyPath(String wasOutputPropertyPath) {
        this.addParam('wasOutputPropertyPath', wasOutputPropertyPath)
        return this
    }
    
    
    
    
}
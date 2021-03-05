package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeleteCluster extends Procedure {

    static DeleteCluster create(Plugin plugin) {
        return new DeleteCluster(procedureName: 'DeleteCluster', plugin: plugin, )
    }


    DeleteCluster flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeleteCluster configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    DeleteCluster wasClusterName(String wasClusterName) {
        this.addParam('wasClusterName', wasClusterName)
        return this
    }
    
    
    DeleteCluster wasSyncNodes(String wasSyncNodes) {
        this.addParam('wasSyncNodes', wasSyncNodes)
        return this
    }
    
    
    
    
}
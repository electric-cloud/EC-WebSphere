package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class SyncNodes extends Procedure {

    static SyncNodes create(Plugin plugin) {
        return new SyncNodes(procedureName: 'SyncNodes', plugin: plugin, )
    }


    SyncNodes flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    SyncNodes configurationName(String configurationName) {
        this.addParam('configurationName', configurationName)
        return this
    }
    
    
    SyncNodes wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    
}
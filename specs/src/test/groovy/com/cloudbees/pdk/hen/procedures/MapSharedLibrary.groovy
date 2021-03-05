package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class MapSharedLibrary extends Procedure {

    static MapSharedLibrary create(Plugin plugin) {
        return new MapSharedLibrary(procedureName: 'MapSharedLibrary', plugin: plugin, )
    }


    MapSharedLibrary flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    MapSharedLibrary applicationName(String applicationName) {
        this.addParam('applicationName', applicationName)
        return this
    }
    
    
    MapSharedLibrary configurationName(String configurationName) {
        this.addParam('configurationName', configurationName)
        return this
    }
    
    
    MapSharedLibrary libraryName(String libraryName) {
        this.addParam('libraryName', libraryName)
        return this
    }
    
    
    MapSharedLibrary wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    
}
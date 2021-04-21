package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DiscoverResource extends Procedure {

    static DiscoverResource create(Plugin plugin) {
        return new DiscoverResource(procedureName: 'DiscoverResource', plugin: plugin, )
    }


    DiscoverResource flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DiscoverResource configurationName(String configurationName) {
        this.addParam('configurationName', configurationName)
        return this
    }
    
    
    
    
}
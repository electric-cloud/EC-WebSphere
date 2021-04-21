package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class Discover extends Procedure {

    static Discover create(Plugin plugin) {
        return new Discover(procedureName: 'Discover', plugin: plugin, )
    }


    Discover flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    Discover configurationName(String configurationName) {
        this.addParam('configurationName', configurationName)
        return this
    }
    
    
    Discover resourceNames(String resourceNames) {
        this.addParam('resourceNames', resourceNames)
        return this
    }
    
    
    
    
}
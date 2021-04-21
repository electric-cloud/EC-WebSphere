package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StartNode extends Procedure {

    static StartNode create(Plugin plugin) {
        return new StartNode(procedureName: 'StartNode', plugin: plugin, )
    }


    StartNode flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StartNode configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    StartNode wasAdditionalParameters(String wasAdditionalParameters) {
        this.addParam('wasAdditionalParameters', wasAdditionalParameters)
        return this
    }
    
    
    StartNode wasLogFileLocation(String wasLogFileLocation) {
        this.addParam('wasLogFileLocation', wasLogFileLocation)
        return this
    }
    
    
    StartNode wasNodeName(String wasNodeName) {
        this.addParam('wasNodeName', wasNodeName)
        return this
    }
    
    
    StartNode wasNodeProfile(String wasNodeProfile) {
        this.addParam('wasNodeProfile', wasNodeProfile)
        return this
    }
    
    
    StartNode wasStartNodeLocation(String wasStartNodeLocation) {
        this.addParam('wasStartNodeLocation', wasStartNodeLocation)
        return this
    }
    
    
    StartNode wasStartServers(String wasStartServers) {
        this.addParam('wasStartServers', wasStartServers)
        return this
    }
    
    
    StartNode wasTimeout(String wasTimeout) {
        this.addParam('wasTimeout', wasTimeout)
        return this
    }
    
    
    
    
}
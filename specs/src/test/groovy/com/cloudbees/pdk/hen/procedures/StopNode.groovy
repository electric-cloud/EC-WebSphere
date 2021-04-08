package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StopNode extends Procedure {

    static StopNode create(Plugin plugin) {
        return new StopNode(procedureName: 'StopNode', plugin: plugin, )
    }


    StopNode flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StopNode configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    StopNode wasAdditionalParameters(String wasAdditionalParameters) {
        this.addParam('wasAdditionalParameters', wasAdditionalParameters)
        return this
    }
    
    
    StopNode wasLogFileLocation(String wasLogFileLocation) {
        this.addParam('wasLogFileLocation', wasLogFileLocation)
        return this
    }
    
    
    StopNode wasNodeProfile(String wasNodeProfile) {
        this.addParam('wasNodeProfile', wasNodeProfile)
        return this
    }
    
    
    StopNode wasStopNodeLocation(String wasStopNodeLocation) {
        this.addParam('wasStopNodeLocation', wasStopNodeLocation)
        return this
    }
    
    
    StopNode wasStopNodePolicy(String wasStopNodePolicy) {
        this.addParam('wasStopNodePolicy', wasStopNodePolicy)
        return this
    }
    
    
    StopNode wasTimeout(String wasTimeout) {
        this.addParam('wasTimeout', wasTimeout)
        return this
    }
    
    
    
    
}
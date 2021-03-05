package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StopDeploymentManager extends Procedure {

    static StopDeploymentManager create(Plugin plugin) {
        return new StopDeploymentManager(procedureName: 'StopDeploymentManager', plugin: plugin, )
    }


    StopDeploymentManager flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StopDeploymentManager configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    StopDeploymentManager wasAdditionalParameters(String wasAdditionalParameters) {
        this.addParam('wasAdditionalParameters', wasAdditionalParameters)
        return this
    }
    
    
    StopDeploymentManager wasDeploymentManagerProfile(String wasDeploymentManagerProfile) {
        this.addParam('wasDeploymentManagerProfile', wasDeploymentManagerProfile)
        return this
    }
    
    
    StopDeploymentManager wasLogFileLocation(String wasLogFileLocation) {
        this.addParam('wasLogFileLocation', wasLogFileLocation)
        return this
    }
    
    
    StopDeploymentManager wasStopManagerLocation(String wasStopManagerLocation) {
        this.addParam('wasStopManagerLocation', wasStopManagerLocation)
        return this
    }
    
    
    StopDeploymentManager wasTimeout(String wasTimeout) {
        this.addParam('wasTimeout', wasTimeout)
        return this
    }
    
    
    
    
}
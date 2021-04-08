package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StartDeploymentManager extends Procedure {

    static StartDeploymentManager create(Plugin plugin) {
        return new StartDeploymentManager(procedureName: 'StartDeploymentManager', plugin: plugin, )
    }


    StartDeploymentManager flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StartDeploymentManager configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    StartDeploymentManager wasAdditionalParameters(String wasAdditionalParameters) {
        this.addParam('wasAdditionalParameters', wasAdditionalParameters)
        return this
    }
    
    
    StartDeploymentManager wasDeploymentManagerProfile(String wasDeploymentManagerProfile) {
        this.addParam('wasDeploymentManagerProfile', wasDeploymentManagerProfile)
        return this
    }
    
    
    StartDeploymentManager wasLogFileLocation(String wasLogFileLocation) {
        this.addParam('wasLogFileLocation', wasLogFileLocation)
        return this
    }
    
    
    StartDeploymentManager wasStartManagerLocation(String wasStartManagerLocation) {
        this.addParam('wasStartManagerLocation', wasStartManagerLocation)
        return this
    }
    
    
    StartDeploymentManager wasTimeout(String wasTimeout) {
        this.addParam('wasTimeout', wasTimeout)
        return this
    }
    
    
    
    
}
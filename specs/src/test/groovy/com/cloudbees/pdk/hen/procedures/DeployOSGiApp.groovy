package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeployOSGiApp extends Procedure {

    static DeployOSGiApp create(Plugin plugin) {
        return new DeployOSGiApp(procedureName: 'DeployOSGiApp', plugin: plugin, )
    }


    DeployOSGiApp flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeployOSGiApp addExternalRepo(String addExternalRepo) {
        this.addParam('addExternalRepo', addExternalRepo)
        return this
    }
    
    
    DeployOSGiApp appName(String appName) {
        this.addParam('appName', appName)
        return this
    }
    
    
    DeployOSGiApp configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    DeployOSGiApp deployUnit(String deployUnit) {
        this.addParam('deployUnit', deployUnit)
        return this
    }
    
    
    DeployOSGiApp ebaPath(String ebaPath) {
        this.addParam('ebaPath', ebaPath)
        return this
    }
    
    
    DeployOSGiApp externalRepoName(String externalRepoName) {
        this.addParam('externalRepoName', externalRepoName)
        return this
    }
    
    
    DeployOSGiApp externalRepoURL(String externalRepoURL) {
        this.addParam('externalRepoURL', externalRepoURL)
        return this
    }
    
    
    DeployOSGiApp localRepoBundleList(String localRepoBundleList) {
        this.addParam('localRepoBundleList', localRepoBundleList)
        return this
    }
    
    
    DeployOSGiApp wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    
}
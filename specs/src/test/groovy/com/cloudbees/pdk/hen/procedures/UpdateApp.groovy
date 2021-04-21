package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class UpdateApp extends Procedure {

    static UpdateApp create(Plugin plugin) {
        return new UpdateApp(procedureName: 'UpdateApp', plugin: plugin, )
    }


    UpdateApp flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    UpdateApp additionalParams(String additionalParams) {
        this.addParam('additionalParams', additionalParams)
        return this
    }
    
    
    UpdateApp appName(String appName) {
        this.addParam('appName', appName)
        return this
    }
    
    
    UpdateApp clusterName(String clusterName) {
        this.addParam('clusterName', clusterName)
        return this
    }
    
    
    UpdateApp configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    UpdateApp content(String content) {
        this.addParam('content', content)
        return this
    }
    
    
    UpdateApp contentType(String contentType) {
        this.addParam('contentType', contentType)
        return this
    }
    
    
    UpdateApp contentURI(String contentURI) {
        this.addParam('contentURI', contentURI)
        return this
    }
    
    
    UpdateApp operation(String operation) {
        this.addParam('operation', operation)
        return this
    }
    
    
    UpdateApp serverName(String serverName) {
        this.addParam('serverName', serverName)
        return this
    }
    
    
    UpdateApp wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    
}
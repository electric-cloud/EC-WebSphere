package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class ConfigureSession extends Procedure {

    static ConfigureSession create(Plugin plugin) {
        return new ConfigureSession(procedureName: 'ConfigureSession', plugin: plugin, )
    }


    ConfigureSession flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    ConfigureSession accessSessionOnTimeout(String accessSessionOnTimeout) {
        this.addParam('accessSessionOnTimeout', accessSessionOnTimeout)
        return this
    }
    
    
    ConfigureSession allowOverflow(String allowOverflow) {
        this.addParam('allowOverflow', allowOverflow)
        return this
    }
    
    
    ConfigureSession appName(String appName) {
        this.addParam('appName', appName)
        return this
    }
    
    
    ConfigureSession configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    ConfigureSession enableCookie(String enableCookie) {
        this.addParam('enableCookie', enableCookie)
        return this
    }
    
    
    ConfigureSession enableProtocolSwitching(String enableProtocolSwitching) {
        this.addParam('enableProtocolSwitching', enableProtocolSwitching)
        return this
    }
    
    
    ConfigureSession enableSerializedSession(String enableSerializedSession) {
        this.addParam('enableSerializedSession', enableSerializedSession)
        return this
    }
    
    
    ConfigureSession enableSSLTracking(String enableSSLTracking) {
        this.addParam('enableSSLTracking', enableSSLTracking)
        return this
    }
    
    
    ConfigureSession enableURLRewriting(String enableURLRewriting) {
        this.addParam('enableURLRewriting', enableURLRewriting)
        return this
    }
    
    
    ConfigureSession invalidTimeout(String invalidTimeout) {
        this.addParam('invalidTimeout', invalidTimeout)
        return this
    }
    
    
    ConfigureSession maxInMemorySessionCount(String maxInMemorySessionCount) {
        this.addParam('maxInMemorySessionCount', maxInMemorySessionCount)
        return this
    }
    
    
    ConfigureSession maxWaitTime(String maxWaitTime) {
        this.addParam('maxWaitTime', maxWaitTime)
        return this
    }
    
    
    ConfigureSession sessionPersistMode(String sessionPersistMode) {
        this.addParam('sessionPersistMode', sessionPersistMode)
        return this
    }
    
    
    ConfigureSession wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    
}
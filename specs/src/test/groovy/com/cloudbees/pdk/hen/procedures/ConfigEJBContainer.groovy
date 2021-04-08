package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class ConfigEJBContainer extends Procedure {

    static ConfigEJBContainer create(Plugin plugin) {
        return new ConfigEJBContainer(procedureName: 'ConfigEJBContainer', plugin: plugin, )
    }


    ConfigEJBContainer flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    ConfigEJBContainer cacheSize(String cacheSize) {
        this.addParam('cacheSize', cacheSize)
        return this
    }
    
    
    ConfigEJBContainer cellName(String cellName) {
        this.addParam('cellName', cellName)
        return this
    }
    
    
    ConfigEJBContainer cleanupInterval(String cleanupInterval) {
        this.addParam('cleanupInterval', cleanupInterval)
        return this
    }
    
    
    ConfigEJBContainer configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    ConfigEJBContainer dataReplicationMode(String dataReplicationMode) {
        this.addParam('dataReplicationMode', dataReplicationMode)
        return this
    }
    
    ConfigEJBContainer dataReplicationMode(DataReplicationModeOptions dataReplicationMode) {
        this.addParam('dataReplicationMode', dataReplicationMode.toString())
        return this
    }
    
    
    ConfigEJBContainer enableSFSBFailover(String enableSFSBFailover) {
        this.addParam('enableSFSBFailover', enableSFSBFailover)
        return this
    }
    
    
    ConfigEJBContainer inactivePoolCleanupInterval(String inactivePoolCleanupInterval) {
        this.addParam('inactivePoolCleanupInterval', inactivePoolCleanupInterval)
        return this
    }
    
    
    ConfigEJBContainer messageBrokerDomainName(String messageBrokerDomainName) {
        this.addParam('messageBrokerDomainName', messageBrokerDomainName)
        return this
    }
    
    
    ConfigEJBContainer nodeName(String nodeName) {
        this.addParam('nodeName', nodeName)
        return this
    }
    
    
    ConfigEJBContainer passivationDirectory(String passivationDirectory) {
        this.addParam('passivationDirectory', passivationDirectory)
        return this
    }
    
    
    ConfigEJBContainer serverName(String serverName) {
        this.addParam('serverName', serverName)
        return this
    }
    
    
    ConfigEJBContainer wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    
    enum DataReplicationModeOptions {
    
    CLIENT_ONLY("client"),
    
    SERVER_ONLY("server"),
    
    BOTH_CLIENT_AND_SERVER("both")
    
    private String value
    DataReplicationModeOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}
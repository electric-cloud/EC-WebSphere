package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class ExportApplicationServer extends Procedure {

    static ExportApplicationServer create(Plugin plugin) {
        return new ExportApplicationServer(procedureName: 'ExportApplicationServer', plugin: plugin, )
    }


    ExportApplicationServer flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    ExportApplicationServer configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    ExportApplicationServer wasAppServerName(String wasAppServerName) {
        this.addParam('wasAppServerName', wasAppServerName)
        return this
    }
    
    
    ExportApplicationServer wasArchivePath(String wasArchivePath) {
        this.addParam('wasArchivePath', wasArchivePath)
        return this
    }
    
    
    ExportApplicationServer wasNodeName(String wasNodeName) {
        this.addParam('wasNodeName', wasNodeName)
        return this
    }
    
    
    
    
}
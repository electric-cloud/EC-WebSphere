package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeleteApplicationServer extends Procedure {

    static DeleteApplicationServer create(Plugin plugin) {
        return new DeleteApplicationServer(procedureName: 'DeleteApplicationServer', plugin: plugin, )
    }


    DeleteApplicationServer flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeleteApplicationServer configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    DeleteApplicationServer wasAppServerName(String wasAppServerName) {
        this.addParam('wasAppServerName', wasAppServerName)
        return this
    }
    
    
    DeleteApplicationServer wasNodeName(String wasNodeName) {
        this.addParam('wasNodeName', wasNodeName)
        return this
    }
    
    
    DeleteApplicationServer wasSyncNodes(String wasSyncNodes) {
        this.addParam('wasSyncNodes', wasSyncNodes)
        return this
    }
    
    
    
    
}
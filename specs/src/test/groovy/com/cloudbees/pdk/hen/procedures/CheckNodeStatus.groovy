package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CheckNodeStatus extends Procedure {

    static CheckNodeStatus create(Plugin plugin, String resourceName) {
        if (resourceName) {
            return new CheckNodeStatus(procedureName: 'CheckNodeStatus', plugin: plugin, resourceName: resourceName)
        }
        return new CheckNodeStatus(procedureName: 'CheckNodeStatus', plugin: plugin, )
    }


    CheckNodeStatus flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CheckNodeStatus configurationName(String configurationName) {
        this.addParam('configurationName', configurationName)
        return this
    }
    
    
    CheckNodeStatus nodeName(String nodeName) {
        this.addParam('nodeName', nodeName)
        return this
    }
    
    
    CheckNodeStatus successCriteria(String successCriteria) {
        this.addParam('successCriteria', successCriteria)
        return this
    }
    
    CheckNodeStatus successCriteria(SuccessCriteriaOptions successCriteria) {
        this.addParam('successCriteria', successCriteria.toString())
        return this
    }
    
    
    CheckNodeStatus wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    
    enum SuccessCriteriaOptions {
    
    ALL_NODE_SERVERS_ARE_RUNNING_("ALL_RUNNING"),
    
    ALL_NODE_SERVERS_ARE_NOT_RUNNING_("ALL_STOPPED"),
    
    NODEAGENT_IS_RUNNING_("NODEAGENT_RUNNING")
    
    private String value
    SuccessCriteriaOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}
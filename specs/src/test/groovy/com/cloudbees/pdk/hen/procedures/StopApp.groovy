package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StopApp extends Procedure {

    static StopApp create(Plugin plugin) {
        return new StopApp(procedureName: 'StopApp', plugin: plugin, )
    }


    StopApp flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StopApp additionalcommands(String additionalcommands) {
        this.addParam('additionalcommands', additionalcommands)
        return this
    }
    
    
    StopApp appname(String appname) {
        this.addParam('appname', appname)
        return this
    }
    
    
    StopApp classpath(String classpath) {
        this.addParam('classpath', classpath)
        return this
    }
    
    
    StopApp clusterName(String clusterName) {
        this.addParam('clusterName', clusterName)
        return this
    }
    
    
    StopApp commands(String commands) {
        this.addParam('commands', commands)
        return this
    }
    
    
    StopApp configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    StopApp javaparams(String javaparams) {
        this.addParam('javaparams', javaparams)
        return this
    }
    
    
    StopApp serverName(String serverName) {
        this.addParam('serverName', serverName)
        return this
    }
    
    
    StopApp wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    
}
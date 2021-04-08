package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class StartApp extends Procedure {

    static StartApp create(Plugin plugin) {
        return new StartApp(procedureName: 'StartApp', plugin: plugin, )
    }


    StartApp flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    StartApp additionalcommands(String additionalcommands) {
        this.addParam('additionalcommands', additionalcommands)
        return this
    }
    
    
    StartApp appname(String appname) {
        this.addParam('appname', appname)
        return this
    }
    
    
    StartApp classpath(String classpath) {
        this.addParam('classpath', classpath)
        return this
    }
    
    
    StartApp clusterName(String clusterName) {
        this.addParam('clusterName', clusterName)
        return this
    }
    
    
    StartApp commands(String commands) {
        this.addParam('commands', commands)
        return this
    }
    
    
    StartApp configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    StartApp javaparams(String javaparams) {
        this.addParam('javaparams', javaparams)
        return this
    }
    
    
    StartApp serverName(String serverName) {
        this.addParam('serverName', serverName)
        return this
    }
    
    
    StartApp wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    
}
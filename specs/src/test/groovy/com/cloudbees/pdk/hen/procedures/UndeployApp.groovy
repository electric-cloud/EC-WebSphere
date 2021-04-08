package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class UndeployApp extends Procedure {

    static UndeployApp create(Plugin plugin) {
        return new UndeployApp(procedureName: 'UndeployApp', plugin: plugin, )
    }


    UndeployApp flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    UndeployApp additionalcommands(String additionalcommands) {
        this.addParam('additionalcommands', additionalcommands)
        return this
    }
    
    
    UndeployApp appname(String appname) {
        this.addParam('appname', appname)
        return this
    }
    
    
    UndeployApp classpath(String classpath) {
        this.addParam('classpath', classpath)
        return this
    }
    
    
    UndeployApp commands(String commands) {
        this.addParam('commands', commands)
        return this
    }
    
    
    UndeployApp configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    UndeployApp javaparams(String javaparams) {
        this.addParam('javaparams', javaparams)
        return this
    }
    
    
    UndeployApp wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    
}
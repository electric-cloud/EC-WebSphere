package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeployApp extends Procedure {

    static DeployApp create(Plugin plugin) {
        return new DeployApp(procedureName: 'DeployApp', plugin: plugin, )
    }


    DeployApp flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeployApp additionalcommands(String additionalcommands) {
        this.addParam('additionalcommands', additionalcommands)
        return this
    }
    
    
    DeployApp appname(String appname) {
        this.addParam('appname', appname)
        return this
    }
    
    
    DeployApp apppath(String apppath) {
        this.addParam('apppath', apppath)
        return this
    }
    
    
    DeployApp classpath(String classpath) {
        this.addParam('classpath', classpath)
        return this
    }
    
    
    DeployApp commands(String commands) {
        this.addParam('commands', commands)
        return this
    }
    
    
    DeployApp configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    DeployApp javaparams(String javaparams) {
        this.addParam('javaparams', javaparams)
        return this
    }
    
    
    DeployApp wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    
}
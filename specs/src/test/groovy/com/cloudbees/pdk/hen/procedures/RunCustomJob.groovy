package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class RunCustomJob extends Procedure {

    static RunCustomJob create(Plugin plugin) {
        return new RunCustomJob(procedureName: 'RunCustomJob', plugin: plugin, )
    }


    RunCustomJob flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    RunCustomJob additionalcommands(String additionalcommands) {
        this.addParam('additionalcommands', additionalcommands)
        return this
    }
    
    
    RunCustomJob classpath(String classpath) {
        this.addParam('classpath', classpath)
        return this
    }
    
    
    RunCustomJob commands(String commands) {
        this.addParam('commands', commands)
        return this
    }
    
    
    RunCustomJob configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    RunCustomJob javaparams(String javaparams) {
        this.addParam('javaparams', javaparams)
        return this
    }
    
    
    RunCustomJob scriptfile(String scriptfile) {
        this.addParam('scriptfile', scriptfile)
        return this
    }
    
    
    RunCustomJob scriptfileabspath(String scriptfileabspath) {
        this.addParam('scriptfileabspath', scriptfileabspath)
        return this
    }
    
    
    RunCustomJob scriptfilesource(String scriptfilesource) {
        this.addParam('scriptfilesource', scriptfilesource)
        return this
    }
    
    RunCustomJob scriptfilesource(ScriptfilesourceOptions scriptfilesource) {
        this.addParam('scriptfilesource', scriptfilesource.toString())
        return this
    }
    
    
    RunCustomJob wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    
    enum ScriptfilesourceOptions {
    
    SUPPLIED_FILE("suppliedfile"),
    
    NEW_SCRIPT_FILE("newscriptfile")
    
    private String value
    ScriptfilesourceOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}
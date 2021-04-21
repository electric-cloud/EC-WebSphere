package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CheckApp extends Procedure {

    static CheckApp create(Plugin plugin) {
        return new CheckApp(procedureName: 'CheckApp', plugin: plugin, )
    }


    CheckApp flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CheckApp appname(String appname) {
        this.addParam('appname', appname)
        return this
    }
    
    
    CheckApp appStateChecked(String appStateChecked) {
        this.addParam('appStateChecked', appStateChecked)
        return this
    }
    
    CheckApp appStateChecked(AppStateCheckedOptions appStateChecked) {
        this.addParam('appStateChecked', appStateChecked.toString())
        return this
    }
    
    
    CheckApp configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CheckApp waitTimeForState(String waitTimeForState) {
        this.addParam('waitTimeForState', waitTimeForState)
        return this
    }
    
    
    CheckApp wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    
    enum AppStateCheckedOptions {
    
    APPLICATION_IS_INSTALLED("EXISTS"),
    
    APPLICATION_IS_NOT_INSTALLED("NOT_EXISTS"),
    
    APPLICATION_IS_READY("READY"),
    
    APPLICATION_IS_NOT_READY("NOT_READY"),
    
    APPLICATION_IS_RUNNING("RUNNING"),
    
    APPLICATION_IS_NOT_RUNNING("NOT_RUNNING")
    
    private String value
    AppStateCheckedOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}
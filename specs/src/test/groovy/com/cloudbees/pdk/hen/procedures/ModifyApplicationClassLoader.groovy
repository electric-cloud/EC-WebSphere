package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class ModifyApplicationClassLoader extends Procedure {

    static ModifyApplicationClassLoader create(Plugin plugin) {
        return new ModifyApplicationClassLoader(procedureName: 'ModifyApplicationClassLoader', plugin: plugin, )
    }


    ModifyApplicationClassLoader flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    ModifyApplicationClassLoader applicationName(String applicationName) {
        this.addParam('applicationName', applicationName)
        return this
    }
    
    
    ModifyApplicationClassLoader classLoaderPolicy(String classLoaderPolicy) {
        this.addParam('classLoaderPolicy', classLoaderPolicy)
        return this
    }
    
    ModifyApplicationClassLoader classLoaderPolicy(ClassLoaderPolicyOptions classLoaderPolicy) {
        this.addParam('classLoaderPolicy', classLoaderPolicy.toString())
        return this
    }
    
    
    ModifyApplicationClassLoader configurationName(String configurationName) {
        this.addParam('configurationName', configurationName)
        return this
    }
    
    
    ModifyApplicationClassLoader loadOrder(String loadOrder) {
        this.addParam('loadOrder', loadOrder)
        return this
    }
    
    ModifyApplicationClassLoader loadOrder(LoadOrderOptions loadOrder) {
        this.addParam('loadOrder', loadOrder.toString())
        return this
    }
    
    
    ModifyApplicationClassLoader wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    
    enum ClassLoaderPolicyOptions {
    
    MULTIPLE("MULTIPLE"),
    
    SINGLE("SINGLE")
    
    private String value
    ClassLoaderPolicyOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
    enum LoadOrderOptions {
    
    PARENT_FIRST("PARENT_FIRST"),
    
    PARENT_LAST("PARENT_LAST")
    
    private String value
    LoadOrderOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}
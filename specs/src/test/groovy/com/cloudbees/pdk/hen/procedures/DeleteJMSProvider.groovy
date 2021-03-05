package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeleteJMSProvider extends Procedure {

    static DeleteJMSProvider create(Plugin plugin) {
        return new DeleteJMSProvider(procedureName: 'DeleteJMSProvider', plugin: plugin, )
    }


    DeleteJMSProvider flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeleteJMSProvider configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    DeleteJMSProvider providerName(String providerName) {
        this.addParam('providerName', providerName)
        return this
    }
    
    
    DeleteJMSProvider providerScope(String providerScope) {
        this.addParam('providerScope', providerScope)
        return this
    }
    
    
    
    
}
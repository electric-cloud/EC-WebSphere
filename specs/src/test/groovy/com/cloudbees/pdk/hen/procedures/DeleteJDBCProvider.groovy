package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeleteJDBCProvider extends Procedure {

    static DeleteJDBCProvider create(Plugin plugin) {
        return new DeleteJDBCProvider(procedureName: 'DeleteJDBCProvider', plugin: plugin, )
    }


    DeleteJDBCProvider flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeleteJDBCProvider configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    DeleteJDBCProvider jdbcProvidername(String jdbcProvidername) {
        this.addParam('jdbcProvidername', jdbcProvidername)
        return this
    }
    
    
    DeleteJDBCProvider wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    
}
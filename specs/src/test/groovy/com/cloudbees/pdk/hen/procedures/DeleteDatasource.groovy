package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeleteDatasource extends Procedure {

    static DeleteDatasource create(Plugin plugin) {
        return new DeleteDatasource(procedureName: 'DeleteDatasource', plugin: plugin, )
    }


    DeleteDatasource flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeleteDatasource configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    DeleteDatasource datasourceName(String datasourceName) {
        this.addParam('datasourceName', datasourceName)
        return this
    }
    
    
    DeleteDatasource wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    
}
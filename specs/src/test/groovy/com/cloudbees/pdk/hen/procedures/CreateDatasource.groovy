package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateDatasource extends Procedure {

    static CreateDatasource create(Plugin plugin) {
        return new CreateDatasource(procedureName: 'CreateDatasource', plugin: plugin, )
    }


    CreateDatasource flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateDatasource authAliasName(String authAliasName) {
        this.addParam('authAliasName', authAliasName)
        return this
    }
    
    
    CreateDatasource configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateDatasource datasourceDescription(String datasourceDescription) {
        this.addParam('datasourceDescription', datasourceDescription)
        return this
    }
    
    
    CreateDatasource datasourceHelperClassname(String datasourceHelperClassname) {
        this.addParam('datasourceHelperClassname', datasourceHelperClassname)
        return this
    }
    
    
    CreateDatasource datasourceJNDIName(String datasourceJNDIName) {
        this.addParam('datasourceJNDIName', datasourceJNDIName)
        return this
    }
    
    
    CreateDatasource datasourceName(String datasourceName) {
        this.addParam('datasourceName', datasourceName)
        return this
    }
    
    
    CreateDatasource jdbcProvider(String jdbcProvider) {
        this.addParam('jdbcProvider', jdbcProvider)
        return this
    }
    
    
    CreateDatasource statementCacheSize(String statementCacheSize) {
        this.addParam('statementCacheSize', statementCacheSize)
        return this
    }
    
    
    CreateDatasource wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    
}
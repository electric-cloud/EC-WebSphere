package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateJDBCProvider extends Procedure {

    static CreateJDBCProvider create(Plugin plugin) {
        return new CreateJDBCProvider(procedureName: 'CreateJDBCProvider', plugin: plugin, )
    }


    CreateJDBCProvider flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateJDBCProvider cell(String cell) {
        this.addParam('cell', cell)
        return this
    }
    
    
    CreateJDBCProvider classpath(String classpath) {
        this.addParam('classpath', classpath)
        return this
    }
    
    
    CreateJDBCProvider cluster(String cluster) {
        this.addParam('cluster', cluster)
        return this
    }
    
    
    CreateJDBCProvider configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateJDBCProvider description(String description) {
        this.addParam('description', description)
        return this
    }
    
    
    CreateJDBCProvider implementationClassName(String implementationClassName) {
        this.addParam('implementationClassName', implementationClassName)
        return this
    }
    
    
    CreateJDBCProvider jdbcProvidername(String jdbcProvidername) {
        this.addParam('jdbcProvidername', jdbcProvidername)
        return this
    }
    
    
    CreateJDBCProvider node(String node) {
        this.addParam('node', node)
        return this
    }
    
    
    CreateJDBCProvider server(String server) {
        this.addParam('server', server)
        return this
    }
    
    
    CreateJDBCProvider wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    
}
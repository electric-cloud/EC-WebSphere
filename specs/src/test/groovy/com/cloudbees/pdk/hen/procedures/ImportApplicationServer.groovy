package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class ImportApplicationServer extends Procedure {

    static ImportApplicationServer create(Plugin plugin) {
        return new ImportApplicationServer(procedureName: 'ImportApplicationServer', plugin: plugin, )
    }


    ImportApplicationServer flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    ImportApplicationServer configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    ImportApplicationServer wasAppServerName(String wasAppServerName) {
        this.addParam('wasAppServerName', wasAppServerName)
        return this
    }
    
    
    ImportApplicationServer wasAppServerNameInArchive(String wasAppServerNameInArchive) {
        this.addParam('wasAppServerNameInArchive', wasAppServerNameInArchive)
        return this
    }
    
    
    ImportApplicationServer wasArchivePath(String wasArchivePath) {
        this.addParam('wasArchivePath', wasArchivePath)
        return this
    }
    
    
    ImportApplicationServer wasCoreGroup(String wasCoreGroup) {
        this.addParam('wasCoreGroup', wasCoreGroup)
        return this
    }
    
    
    ImportApplicationServer wasNodeName(String wasNodeName) {
        this.addParam('wasNodeName', wasNodeName)
        return this
    }
    
    
    ImportApplicationServer wasNodeNameInArchive(String wasNodeNameInArchive) {
        this.addParam('wasNodeNameInArchive', wasNodeNameInArchive)
        return this
    }
    
    
    ImportApplicationServer wasSyncNodes(String wasSyncNodes) {
        this.addParam('wasSyncNodes', wasSyncNodes)
        return this
    }
    
    
    
    
}
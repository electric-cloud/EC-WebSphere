package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeleteApplicationServerTemplate extends Procedure {

    static DeleteApplicationServerTemplate create(Plugin plugin) {
        return new DeleteApplicationServerTemplate(procedureName: 'DeleteApplicationServerTemplate', plugin: plugin, )
    }


    DeleteApplicationServerTemplate flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeleteApplicationServerTemplate configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    DeleteApplicationServerTemplate wasSyncNodes(String wasSyncNodes) {
        this.addParam('wasSyncNodes', wasSyncNodes)
        return this
    }
    
    
    DeleteApplicationServerTemplate wasTemplateName(String wasTemplateName) {
        this.addParam('wasTemplateName', wasTemplateName)
        return this
    }
    
    
    
    
}
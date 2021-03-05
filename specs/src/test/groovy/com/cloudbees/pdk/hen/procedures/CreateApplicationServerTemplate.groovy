package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateApplicationServerTemplate extends Procedure {

    static CreateApplicationServerTemplate create(Plugin plugin) {
        return new CreateApplicationServerTemplate(procedureName: 'CreateApplicationServerTemplate', plugin: plugin, )
    }


    CreateApplicationServerTemplate flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateApplicationServerTemplate configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateApplicationServerTemplate wasAppServerName(String wasAppServerName) {
        this.addParam('wasAppServerName', wasAppServerName)
        return this
    }
    
    
    CreateApplicationServerTemplate wasNodeName(String wasNodeName) {
        this.addParam('wasNodeName', wasNodeName)
        return this
    }
    
    
    CreateApplicationServerTemplate wasSyncNodes(String wasSyncNodes) {
        this.addParam('wasSyncNodes', wasSyncNodes)
        return this
    }
    
    
    CreateApplicationServerTemplate wasTemplateDescription(String wasTemplateDescription) {
        this.addParam('wasTemplateDescription', wasTemplateDescription)
        return this
    }
    
    
    CreateApplicationServerTemplate wasTemplateLocation(String wasTemplateLocation) {
        this.addParam('wasTemplateLocation', wasTemplateLocation)
        return this
    }
    
    
    CreateApplicationServerTemplate wasTemplateName(String wasTemplateName) {
        this.addParam('wasTemplateName', wasTemplateName)
        return this
    }
    
    
    
    
}
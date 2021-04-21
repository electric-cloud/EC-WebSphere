package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateApplicationServer extends Procedure {

    static CreateApplicationServer create(Plugin plugin) {
        return new CreateApplicationServer(procedureName: 'CreateApplicationServer', plugin: plugin, )
    }


    CreateApplicationServer flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateApplicationServer configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateApplicationServer wasAppServerName(String wasAppServerName) {
        this.addParam('wasAppServerName', wasAppServerName)
        return this
    }
    
    
    CreateApplicationServer wasGenUniquePorts(String wasGenUniquePorts) {
        this.addParam('wasGenUniquePorts', wasGenUniquePorts)
        return this
    }
    
    
    CreateApplicationServer wasNodeName(String wasNodeName) {
        this.addParam('wasNodeName', wasNodeName)
        return this
    }
    
    
    CreateApplicationServer wasSourceServerName(String wasSourceServerName) {
        this.addParam('wasSourceServerName', wasSourceServerName)
        return this
    }
    
    
    CreateApplicationServer wasSourceType(String wasSourceType) {
        this.addParam('wasSourceType', wasSourceType)
        return this
    }
    
    
    CreateApplicationServer wasSyncNodes(String wasSyncNodes) {
        this.addParam('wasSyncNodes', wasSyncNodes)
        return this
    }
    
    
    CreateApplicationServer wasTemplateLocation(String wasTemplateLocation) {
        this.addParam('wasTemplateLocation', wasTemplateLocation)
        return this
    }
    
    
    CreateApplicationServer wasTemplateName(String wasTemplateName) {
        this.addParam('wasTemplateName', wasTemplateName)
        return this
    }
    
    
    
    
}
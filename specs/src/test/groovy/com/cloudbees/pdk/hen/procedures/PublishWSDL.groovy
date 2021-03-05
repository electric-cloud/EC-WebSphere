package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class PublishWSDL extends Procedure {

    static PublishWSDL create(Plugin plugin) {
        return new PublishWSDL(procedureName: 'PublishWSDL', plugin: plugin, )
    }


    PublishWSDL flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    PublishWSDL additionalcommands(String additionalcommands) {
        this.addParam('additionalcommands', additionalcommands)
        return this
    }
    
    
    PublishWSDL appname(String appname) {
        this.addParam('appname', appname)
        return this
    }
    
    
    PublishWSDL classpath(String classpath) {
        this.addParam('classpath', classpath)
        return this
    }
    
    
    PublishWSDL commands(String commands) {
        this.addParam('commands', commands)
        return this
    }
    
    
    PublishWSDL configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    PublishWSDL javaparams(String javaparams) {
        this.addParam('javaparams', javaparams)
        return this
    }
    
    
    PublishWSDL publishlocation(String publishlocation) {
        this.addParam('publish_location', publishlocation)
        return this
    }
    
    
    PublishWSDL soapprefix(String soapprefix) {
        this.addParam('soap_prefix', soapprefix)
        return this
    }
    
    
    PublishWSDL wsadminabspath(String wsadminabspath) {
        this.addParam('wsadminabspath', wsadminabspath)
        return this
    }
    
    
    
    
}
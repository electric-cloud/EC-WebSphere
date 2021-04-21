package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateClusterMembers extends Procedure {

    static CreateClusterMembers create(Plugin plugin) {
        return new CreateClusterMembers(procedureName: 'CreateClusterMembers', plugin: plugin, )
    }


    CreateClusterMembers flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateClusterMembers configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateClusterMembers wasClusterMembersGenUniquePorts(String wasClusterMembersGenUniquePorts) {
        this.addParam('wasClusterMembersGenUniquePorts', wasClusterMembersGenUniquePorts)
        return this
    }
    
    
    CreateClusterMembers wasClusterMembersList(String wasClusterMembersList) {
        this.addParam('wasClusterMembersList', wasClusterMembersList)
        return this
    }
    
    
    CreateClusterMembers wasClusterMemberWeight(String wasClusterMemberWeight) {
        this.addParam('wasClusterMemberWeight', wasClusterMemberWeight)
        return this
    }
    
    
    CreateClusterMembers wasClusterName(String wasClusterName) {
        this.addParam('wasClusterName', wasClusterName)
        return this
    }
    
    
    CreateClusterMembers wasSyncNodes(String wasSyncNodes) {
        this.addParam('wasSyncNodes', wasSyncNodes)
        return this
    }
    
    
    
    
}
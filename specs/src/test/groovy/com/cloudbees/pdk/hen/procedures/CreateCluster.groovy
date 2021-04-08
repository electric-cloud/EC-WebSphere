package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateCluster extends Procedure {

    static CreateCluster create(Plugin plugin) {
        return new CreateCluster(procedureName: 'CreateCluster', plugin: plugin, )
    }


    CreateCluster flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateCluster configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateCluster wasAddClusterMembers(String wasAddClusterMembers) {
        this.addParam('wasAddClusterMembers', wasAddClusterMembers)
        return this
    }
    
    
    CreateCluster wasClusterMembersGenUniquePorts(boolean wasClusterMembersGenUniquePorts) {
        this.addParam('wasClusterMembersGenUniquePorts', wasClusterMembersGenUniquePorts)
        return this
    }
    
    
    CreateCluster wasClusterMembersList(String wasClusterMembersList) {
        this.addParam('wasClusterMembersList', wasClusterMembersList)
        return this
    }
    
    
    CreateCluster wasClusterMemberWeight(String wasClusterMemberWeight) {
        this.addParam('wasClusterMemberWeight', wasClusterMemberWeight)
        return this
    }
    
    
    CreateCluster wasClusterName(String wasClusterName) {
        this.addParam('wasClusterName', wasClusterName)
        return this
    }
    
    
    CreateCluster wasCreateFirstClusterMember(String wasCreateFirstClusterMember) {
        this.addParam('wasCreateFirstClusterMember', wasCreateFirstClusterMember)
        return this
    }
    
    
    CreateCluster wasFirstClusterMemberCreationPolicy(String wasFirstClusterMemberCreationPolicy) {
        this.addParam('wasFirstClusterMemberCreationPolicy', wasFirstClusterMemberCreationPolicy)
        return this
    }
    
    
    CreateCluster wasFirstClusterMemberGenUniquePorts(boolean wasFirstClusterMemberGenUniquePorts) {
        this.addParam('wasFirstClusterMemberGenUniquePorts', wasFirstClusterMemberGenUniquePorts)
        return this
    }
    
    
    CreateCluster wasFirstClusterMemberName(String wasFirstClusterMemberName) {
        this.addParam('wasFirstClusterMemberName', wasFirstClusterMemberName)
        return this
    }
    
    
    CreateCluster wasFirstClusterMemberNode(String wasFirstClusterMemberNode) {
        this.addParam('wasFirstClusterMemberNode', wasFirstClusterMemberNode)
        return this
    }
    
    
    CreateCluster wasFirstClusterMemberTemplateName(String wasFirstClusterMemberTemplateName) {
        this.addParam('wasFirstClusterMemberTemplateName', wasFirstClusterMemberTemplateName)
        return this
    }
    
    
    CreateCluster wasFirstClusterMemberWeight(String wasFirstClusterMemberWeight) {
        this.addParam('wasFirstClusterMemberWeight', wasFirstClusterMemberWeight)
        return this
    }
    
    
    CreateCluster wasPreferLocal(boolean wasPreferLocal) {
        this.addParam('wasPreferLocal', wasPreferLocal)
        return this
    }
    
    
    CreateCluster wasServerResourcesPromotionPolicy(String wasServerResourcesPromotionPolicy) {
        this.addParam('wasServerResourcesPromotionPolicy', wasServerResourcesPromotionPolicy)
        return this
    }
    
    
    CreateCluster wasSourceServerName(String wasSourceServerName) {
        this.addParam('wasSourceServerName', wasSourceServerName)
        return this
    }
    
    
    CreateCluster wasSyncNodes(String wasSyncNodes) {
        this.addParam('wasSyncNodes', wasSyncNodes)
        return this
    }
    
    
    
    
}
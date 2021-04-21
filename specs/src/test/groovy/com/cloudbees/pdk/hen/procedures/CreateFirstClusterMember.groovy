package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class CreateFirstClusterMember extends Procedure {

    static CreateFirstClusterMember create(Plugin plugin) {
        return new CreateFirstClusterMember(procedureName: 'CreateFirstClusterMember', plugin: plugin, )
    }


    CreateFirstClusterMember flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    CreateFirstClusterMember configname(String configname) {
        this.addParam('configname', configname)
        return this
    }
    
    
    CreateFirstClusterMember wasClusterName(String wasClusterName) {
        this.addParam('wasClusterName', wasClusterName)
        return this
    }
    
    
    CreateFirstClusterMember wasFirstClusterMemberCreationPolicy(String wasFirstClusterMemberCreationPolicy) {
        this.addParam('wasFirstClusterMemberCreationPolicy', wasFirstClusterMemberCreationPolicy)
        return this
    }
    
    CreateFirstClusterMember wasFirstClusterMemberCreationPolicy(WasFirstClusterMemberCreationPolicyOptions wasFirstClusterMemberCreationPolicy) {
        this.addParam('wasFirstClusterMemberCreationPolicy', wasFirstClusterMemberCreationPolicy.toString())
        return this
    }
    
    
    CreateFirstClusterMember wasFirstClusterMemberGenUniquePorts(String wasFirstClusterMemberGenUniquePorts) {
        this.addParam('wasFirstClusterMemberGenUniquePorts', wasFirstClusterMemberGenUniquePorts)
        return this
    }
    
    
    CreateFirstClusterMember wasFirstClusterMemberName(String wasFirstClusterMemberName) {
        this.addParam('wasFirstClusterMemberName', wasFirstClusterMemberName)
        return this
    }
    
    
    CreateFirstClusterMember wasFirstClusterMemberNode(String wasFirstClusterMemberNode) {
        this.addParam('wasFirstClusterMemberNode', wasFirstClusterMemberNode)
        return this
    }
    
    
    CreateFirstClusterMember wasFirstClusterMemberTemplateName(String wasFirstClusterMemberTemplateName) {
        this.addParam('wasFirstClusterMemberTemplateName', wasFirstClusterMemberTemplateName)
        return this
    }
    
    
    CreateFirstClusterMember wasFirstClusterMemberWeight(String wasFirstClusterMemberWeight) {
        this.addParam('wasFirstClusterMemberWeight', wasFirstClusterMemberWeight)
        return this
    }
    
    
    CreateFirstClusterMember wasServerResourcesPromotionPolicy(String wasServerResourcesPromotionPolicy) {
        this.addParam('wasServerResourcesPromotionPolicy', wasServerResourcesPromotionPolicy)
        return this
    }
    
    CreateFirstClusterMember wasServerResourcesPromotionPolicy(WasServerResourcesPromotionPolicyOptions wasServerResourcesPromotionPolicy) {
        this.addParam('wasServerResourcesPromotionPolicy', wasServerResourcesPromotionPolicy.toString())
        return this
    }
    
    
    CreateFirstClusterMember wasSourceServerName(String wasSourceServerName) {
        this.addParam('wasSourceServerName', wasSourceServerName)
        return this
    }
    
    
    CreateFirstClusterMember wasSyncNodes(String wasSyncNodes) {
        this.addParam('wasSyncNodes', wasSyncNodes)
        return this
    }
    
    
    
    
    enum WasFirstClusterMemberCreationPolicyOptions {
    
    USE_EXISTING_SERVER_AS_TEMPLATE("existing"),
    
    USING_PRE_CREATED_APPLICATION_SERVER_TEMPLATE("template")
    
    private String value
    WasFirstClusterMemberCreationPolicyOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
    enum WasServerResourcesPromotionPolicyOptions {
    
    CLUSTER("cluster"),
    
    SERVER("server"),
    
    BOTH("both")
    
    private String value
    WasServerResourcesPromotionPolicyOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}
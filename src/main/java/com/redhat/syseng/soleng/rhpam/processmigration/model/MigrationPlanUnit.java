package com.redhat.syseng.soleng.rhpam.processmigration.model;

import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MigrationPlanUnit {

    private String containerId;
    private String targetContainerId;
    private String targetProcessId;

    private Map<String, String> nodeMapping ;

    
    
    public Map<String, String> getNodeMapping() {
        return nodeMapping;
    }

    public void setNodeMapping(Map<String, String> nodeMapping) {
        this.nodeMapping = nodeMapping;
    }
    
    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getTargetContainerId() {
        return targetContainerId;
    }

    public void setTargetContainerId(String targetContainerId) {
        this.targetContainerId = targetContainerId;
    }

    public String getTargetProcessId() {
        return targetProcessId;
    }

    public void setTargetProcessId(String targetProcessId) {
        this.targetProcessId = targetProcessId;
    }

    @Override
    public String toString() {
        return "MigrationPlanUnit [containerId=" + containerId + ", targetContainerId=" + targetContainerId
                + ", targetProcessId=" + targetProcessId 
                + ", nodeMapping=" + nodeMapping + "]";
    }
}

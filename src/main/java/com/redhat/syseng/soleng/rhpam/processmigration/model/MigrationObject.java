/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.syseng.soleng.rhpam.processmigration.model;

import java.util.List;

/**
 *
 * @author czhu
 */
public class MigrationObject {
    private String migrationId;
    private String planId;
    private List<Long> processInstancesId;    
    private Execution execution;
    
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public List<Long> getProcessInstancesId() {
        return processInstancesId;
    }

    public void setProcessInstancesId(List<Long> processInstancesId) {
        this.processInstancesId = processInstancesId;
    }

    public String getMigrationId() {
        return migrationId;
    }

    public void setMigrationId(String migrationId) {
        this.migrationId = migrationId;
    }

    public Execution getExecution() {
        return execution;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }
    
    @Override
    public String toString() {
        return "MigrationObject [planId=" + planId 
                + ", processInstanceId=" + processInstancesId
                + ", execution=" + execution + "]";
    }    
}

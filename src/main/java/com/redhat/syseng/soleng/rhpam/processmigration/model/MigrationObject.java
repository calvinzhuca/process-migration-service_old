/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.syseng.soleng.rhpam.processmigration.model;

/**
 *
 * @author czhu
 */
public class MigrationObject {
    String migrationId;
    String planId;
    
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @Override
    public String toString() {
        return "MigrationObject [planId=" + planId + "]";
    }    
}

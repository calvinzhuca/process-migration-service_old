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
    int planId;
    
    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    @Override
    public String toString() {
        return "MigrationObject [planId=" + planId + "]";
    }    
}

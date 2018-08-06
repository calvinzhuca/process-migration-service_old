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
public class MigrationPlanTableObject {
    String planId;
    MigrationPlan migrationPlan;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public MigrationPlan getMigrationPlan() {
        return migrationPlan;
    }

    public void setMigrationPlan(MigrationPlan migrationPlan) {
        this.migrationPlan = migrationPlan;
    }


    @Override
    public String toString() {
        return "MigrationPlan [planId=" + planId  + ", migrationPlan=" + migrationPlan+ "]";
    }    
    
}

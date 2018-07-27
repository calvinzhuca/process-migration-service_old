package com.redhat.syseng.soleng.rhpam.processmigration.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MigrationPlan {

    private int planId;
    private String name;
    private String description;
    private String strategy;
    private boolean async;      
    private MigrationPlanUnit migrationPlanUnit;

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String Description) {
        this.description = Description;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public MigrationPlanUnit getMigrationPlanUnit() {
        return migrationPlanUnit;
    }

    public void setMigrationPlanUnit(MigrationPlanUnit migrationPlanUnit) {
        this.migrationPlanUnit = migrationPlanUnit;
    }
    
    

    @Override
    public String toString() {
        return "MigrationPlan [planId=" + planId + ", name=" + name
                + ", Description=" + description + ", strategy=" + strategy 
                + ", async=" + async + ", migrationPlanUnit=" + migrationPlanUnit+ "]";
    }
}

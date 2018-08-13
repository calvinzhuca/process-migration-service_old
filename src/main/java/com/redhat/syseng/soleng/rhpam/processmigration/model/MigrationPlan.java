package com.redhat.syseng.soleng.rhpam.processmigration.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MigrationPlan {


    private String name;
    private String description;
    private boolean rest;      
    private MigrationPlanUnit migrationPlanUnit;

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

    public boolean isRest() {
        return rest;
    }

    public void setRest(boolean rest) {
        this.rest = rest;
    }


    public MigrationPlanUnit getMigrationPlanUnit() {
        return migrationPlanUnit;
    }

    public void setMigrationPlanUnit(MigrationPlanUnit migrationPlanUnit) {
        this.migrationPlanUnit = migrationPlanUnit;
    }
    
    

    @Override
    public String toString() {
        return "MigrationPlan [name=" + name
                + ", Description=" + description 
                + ", rest=" + rest + ", migrationPlanUnit=" + migrationPlanUnit+ "]";
    }
}

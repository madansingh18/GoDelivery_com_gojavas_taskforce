package com.gojavas.taskforce.entity;

/**
 * Created by MadanS on 10/24/2016.
 */
public class Relation {
    @Override
    public String toString() {
        return getRelationName();
    }
    private String RelationID;

    public String getRelationName() {
        return RelationName;
    }

    public void setRelationName(String relationName) {
        RelationName = relationName;
    }

    public String getRelationID() {
        return RelationID;
    }

    public void setRelationID(String relationID) {
        RelationID = relationID;
    }

    private String RelationName;
}

package com.gojavas.taskforce.entity;

/**
 * Created by MadanS on 10/26/2016.
 */
public class Proof {

    @Override
    public String toString() {
        return getProofName();
    }

    public String getProofID() {
        return ProofID;
    }

    public void setProofID(String proofID) {
        ProofID = proofID;
    }

    public String getProofName() {
        return ProofName;
    }

    public void setProofName(String proofName) {
        ProofName = proofName;
    }

    public String getProofDetailRequired() {
        return ProofDetailRequired;
    }

    public void setProofDetailRequired(String proofDetailRequired) {
        ProofDetailRequired = proofDetailRequired;
    }

    private String ProofID;
    private String ProofName;
    private String ProofDetailRequired;

}

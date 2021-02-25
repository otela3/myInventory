package com.satrken.myinventory;

import java.util.List;

public class Ofs {
    private String documentId;
    List<String> of;

    public Ofs(){
        //public no-arg contructor needed
    }

    public Ofs(String documentId, List<String> of) {
        this.documentId = documentId;
        this.of = of;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public List<String> getOf() {
        return of;
    }
}

package com.satrken.myinventory;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Ofs {
    private String documentId;
    List<String> of;

    public Ofs(String documentId, List<String> of) {
        this.documentId = documentId;
        this.of = of;
    }

    public Ofs() {
    }

    @Exclude
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

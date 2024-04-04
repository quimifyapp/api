package com.quimify.api.notfoundquery;

import javax.persistence.*;

// This class represents not found queries.

@Entity
@Table(name = "not_found_query")
class NotFoundQueryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob // Large object
    @Column(nullable = false)
    private String query;

    @Column(nullable = false)
    private String location;

    // Constructors:

    NotFoundQueryModel(String query, String location) {
        this.query = query;
        this.location = location;
    }

    protected NotFoundQueryModel() {} // Needed by JPA, don't touch

}
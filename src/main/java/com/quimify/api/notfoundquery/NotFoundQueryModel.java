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

    // TODO frequency?

    // Getters and setters:

    Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    String getQuery() {
        return query;
    }

    void setQuery(String input) {
        this.query = input;
    }

    String getLocation() {
        return location;
    }

    void setLocation(String location) {
        this.location = location;
    }

}
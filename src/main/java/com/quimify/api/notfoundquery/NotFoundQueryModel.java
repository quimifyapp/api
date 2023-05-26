package com.quimify.api.notfoundquery;

import javax.persistence.*;

// This class represents not found queries.

@Entity
@Table(name = "not_found_query")
class NotFoundQueryModel {

    // Non-nullable:

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Lob // Large objects
    @Column(nullable = false)
    private String query;

    @Column(nullable = false)
    private String location;

    // TODO frequency

    // Getters and setters:

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String input) {
        this.query = input;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
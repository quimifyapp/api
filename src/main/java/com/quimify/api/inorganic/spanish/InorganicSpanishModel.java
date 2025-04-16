package com.quimify.api.inorganic.spanish;

import com.quimify.api.inorganic.InorganicModel;
import org.hibernate.annotations.Check;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*; // Added import
import java.util.ArrayList; // Added import
import java.util.List; // Added import
import java.util.stream.Collectors; // Added import

@Entity
@Table(name = "inorganic_sp")
@Check(constraints = "(stock_name IS NOT NULL OR systematic_name IS NOT NULL OR traditional_name IS NOT NULL)")
public class InorganicSpanishModel extends InorganicModel {

    @OneToMany(mappedBy = "inorganicSpanishModel", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<InorganicSpanishSearchTagModel> inorganicSearchTags = new ArrayList<>();

    public InorganicSpanishModel() {
        super();
    }

    @Override
    public List<String> getSearchTags() {
        return inorganicSearchTags.stream()
                .map(InorganicSpanishSearchTagModel::getNormalizedText)
                .collect(Collectors.toList());
    }

    // Optional: Add methods to manage search tags if needed
    public void addSearchTag(InorganicSpanishSearchTagModel tag) {
        inorganicSearchTags.add(tag);
    }

    public void removeSearchTag(InorganicSpanishSearchTagModel tag) {
        inorganicSearchTags.remove(tag);
    }
}

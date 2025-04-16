package com.quimify.api.inorganic.english;

import com.quimify.api.inorganic.InorganicModel;
import org.hibernate.annotations.Check;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*; // Added import
import java.util.ArrayList; // Added import
import java.util.List; // Added import
import java.util.stream.Collectors; // Added import

@Entity
@Table(name = "inorganic_en")
@Check(constraints = "(stock_name IS NOT NULL OR systematic_name IS NOT NULL OR traditional_name IS NOT NULL)")
public class InorganicEnglishModel extends InorganicModel {

    @OneToMany(mappedBy = "inorganicEnglishModel", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<InorganicEnglishSearchTagModel> inorganicSearchTags = new ArrayList<>();

    public InorganicEnglishModel() {
        super();
    }

    @Override
    public List<String> getSearchTags() {
        return inorganicSearchTags.stream()
                .map(InorganicEnglishSearchTagModel::getNormalizedText)
                .collect(Collectors.toList());
    }

    // Optional: Add methods to manage search tags if needed
    public void addSearchTag(InorganicEnglishSearchTagModel tag) {
        inorganicSearchTags.add(tag);
    }

    public void removeSearchTag(InorganicEnglishSearchTagModel tag) {
        inorganicSearchTags.remove(tag);
    }
}

package com.quimify.api.inorganic.english;

import com.quimify.api.inorganic.InorganicSearchTagModel;

import javax.persistence.*;

@Entity
@Table(name = "inorganic_en_search_tag")
public class InorganicEnglishSearchTagModel extends InorganicSearchTagModel {

    @ManyToOne
    @JoinColumn(name = "inorganic_en_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_inorganic_en_search_tag"))
    private InorganicEnglishModel inorganicEnglishModel;

    // Needed by JPA
    public InorganicEnglishSearchTagModel() {
    }

    public InorganicEnglishSearchTagModel(String normalizedText, InorganicEnglishModel inorganicEnglishModel) {
        this.setNormalizedText(normalizedText);
        this.inorganicEnglishModel = inorganicEnglishModel;
    }

}

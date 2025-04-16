package com.quimify.api.inorganic.spanish;

import com.quimify.api.inorganic.InorganicSearchTagModel;

import javax.persistence.*;

@Entity
@Table(name = "inorganic_sp_search_tag")
public class InorganicSpanishSearchTagModel extends InorganicSearchTagModel {

    @ManyToOne
    @JoinColumn(name = "inorganic_sp_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_inorganic_sp_search_tag"))
    private InorganicSpanishModel inorganicSpanishModel;

    // Needed by JPA
    public InorganicSpanishSearchTagModel() {
    }

    public InorganicSpanishSearchTagModel(String normalizedText, InorganicSpanishModel inorganicSpanishModel) {
        this.setNormalizedText(normalizedText);
        this.inorganicSpanishModel = inorganicSpanishModel;
    }

}


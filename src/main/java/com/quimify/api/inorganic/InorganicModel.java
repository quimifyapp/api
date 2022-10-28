package com.quimify.api.inorganic;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Esta clase representa los compuestos inorgánicos.

@Entity // Es un modelo real
@Table(name = "inorganic") // En la tabla 'inorganico' de la DB
public class InorganicModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    // Necesarias:

    @Column(nullable = false)
    private String formula; // "MgH2"
    @Column(nullable = false)
    private String name;  // "hidruro de magnesio"

    // Con valor predeterminado:

    @Column(columnDefinition = "INT default 1", nullable = false)
    private Integer searchCount = 1;

    // Con valor predeterminado 'null':

    private String alternativeName; // "dihidruro de magnesio"

    // De tipo 'String' porque no se realizan cálculos con ellos:

    private Float molecularMass;        // (g)
    private String density;    // (g/cm3)
    private String meltingPoint;      // (K)
    private String boilingPoint;  // (K)

    // Referencia externa (one to many):

    @OneToMany(cascade = CascadeType.ALL) // Se crean de la mano y se borran de la mano
    @JoinColumn(name = "inorganic_id") // Cada etiqueta lleva asociado el inorgánico al que pertenece
    private Set<SearchTagModel> searchTags = new HashSet<>(); // "hidruromagnesico" | "aguaoxigenada" | ...

    // --------------------------------------------------------------------------------

    public void registrarBusqueda() {
        searchCount++;
    }

    public Set<String> getEtiquetasString() {
        return searchTags.stream().map(SearchTagModel::getNormalizedText).collect(Collectors.toSet());
    }

    public void addTagOf(String tag) {
        searchTags.add(new SearchTagModel(tag));
    }

    @Override
    public String toString() {
        List<String> words = new ArrayList<>();

        words.add(String.valueOf(id));

        words.add(formula);
        words.add(name);

        if(alternativeName != null)
            words.add(alternativeName);

        return words.toString();
    }

    // Getters y setters:

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getName() {
        return name;
    }

    public void setName(String nombre) {
        this.name = nombre;
    }

    public Integer getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(Integer busquedas) {
        this.searchCount = busquedas;
    }

    public String getAlternativeName() {
        return alternativeName;
    }

    public void setAlternativeName(String alternativo) {
        this.alternativeName = alternativo;
    }

    public Float getMolecularMass() {
        return molecularMass;
    }

    public void setMolecularMass(Float molecularMass) {
        this.molecularMass = molecularMass;
    }

    public String getDensity() {
        return density;
    }

    public void setDensity(String densidad) {
        this.density = densidad;
    }

    public String getMeltingPoint() {
        return meltingPoint;
    }

    public void setMeltingPoint(String fusion) {
        this.meltingPoint = fusion;
    }

    public String getBoilingPoint() {
        return boilingPoint;
    }

    public void setBoilingPoint(String ebullicion) {
        this.boilingPoint = ebullicion;
    }

    public Set<SearchTagModel> getSearchTags() {
        return searchTags;
    }

    public void setSearchTags(Set<SearchTagModel> etiquetas) {
        this.searchTags = etiquetas;
    }
}

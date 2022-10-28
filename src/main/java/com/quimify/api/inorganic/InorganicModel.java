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
class InorganicModel {

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

    protected void registrarBusqueda() {
        searchCount++;
    }

    protected Set<String> getEtiquetasString() {
        return searchTags.stream().map(SearchTagModel::getNormalizedText).collect(Collectors.toSet());
    }

    protected void addTagOf(String tag) {
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

    protected Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    protected String getFormula() {
        return formula;
    }

    protected void setFormula(String formula) {
        this.formula = formula;
    }

    protected String getName() {
        return name;
    }

    protected void setName(String nombre) {
        this.name = nombre;
    }

    protected Integer getSearchCount() {
        return searchCount;
    }

    protected void setSearchCount(Integer busquedas) {
        this.searchCount = busquedas;
    }

    protected String getAlternativeName() {
        return alternativeName;
    }

    protected void setAlternativeName(String alternativo) {
        this.alternativeName = alternativo;
    }

    protected Float getMolecularMass() {
        return molecularMass;
    }

    protected void setMolecularMass(Float molecularMass) {
        this.molecularMass = molecularMass;
    }

    protected String getDensity() {
        return density;
    }

    protected void setDensity(String densidad) {
        this.density = densidad;
    }

    protected String getMeltingPoint() {
        return meltingPoint;
    }

    protected void setMeltingPoint(String fusion) {
        this.meltingPoint = fusion;
    }

    protected String getBoilingPoint() {
        return boilingPoint;
    }

    protected void setBoilingPoint(String ebullicion) {
        this.boilingPoint = ebullicion;
    }

    protected Set<SearchTagModel> getSearchTags() {
        return searchTags;
    }

    protected void setSearchTags(Set<SearchTagModel> etiquetas) {
        this.searchTags = etiquetas;
    }
}

package com.quimify.api.cliente;

import javax.persistence.*;

// Esta clase representa la configuración de cada cliente según su versión.

@Entity // Es un modelo real
@Table(name = "cliente") // En la tabla 'cliente' de la DB
public class ClienteModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Integer version; // Se corresponde con la versión del cliente

	// Bienvenida:

	@Column(nullable = false)
	private Boolean actualizacion_disponible;
	@Column()
	private Boolean actualizacion_obligatoria;
	@Column()
	private String actualizacion_detalles;
	@Column(nullable = false)
	private Boolean mensaje_presente;
	@Column()
	private String mensaje_titulo;
	@Column()
	private String mensaje_detalles;
	@Column()
	private Boolean mensaje_enlace_presente;
	@Column()
	private String mensaje_enlace_nombre;
	@Column()
	private String mensaje_enlace;

	// --------------------------------------------------------------------------------

	// Getters y setters:

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Boolean getActualizacion_disponible() {
		return actualizacion_disponible;
	}

	public void setActualizacion_disponible(Boolean actualizacion_disponible) {
		this.actualizacion_disponible = actualizacion_disponible;
	}

	public Boolean getActualizacion_obligatoria() {
		return actualizacion_obligatoria;
	}

	public void setActualizacion_obligatoria(Boolean actualizacion_obligatoria) {
		this.actualizacion_obligatoria = actualizacion_obligatoria;
	}

	public String getActualizacion_detalles() {
		return actualizacion_detalles;
	}

	public void setActualizacion_detalles(String actualizacion_detalles) {
		this.actualizacion_detalles = actualizacion_detalles;
	}

	public Boolean getMensaje_presente() {
		return mensaje_presente;
	}

	public void setMensaje_presente(Boolean mensaje_presente) {
		this.mensaje_presente = mensaje_presente;
	}

	public String getMensaje_titulo() {
		return mensaje_titulo;
	}

	public void setMensaje_titulo(String mensaje_titulo) {
		this.mensaje_titulo = mensaje_titulo;
	}

	public String getMensaje_detalles() {
		return mensaje_detalles;
	}

	public void setMensaje_detalles(String mensaje_detalles) {
		this.mensaje_detalles = mensaje_detalles;
	}

	public Boolean getMensaje_enlace_presente() {
		return mensaje_enlace_presente;
	}

	public void setMensaje_enlace_presente(Boolean mensaje_enlace_presente) {
		this.mensaje_enlace_presente = mensaje_enlace_presente;
	}

	public String getMensaje_enlace_nombre() {
		return mensaje_enlace_nombre;
	}

	public void setMensaje_enlace_nombre(String mensaje_enlace_nombre) {
		this.mensaje_enlace_nombre = mensaje_enlace_nombre;
	}

	public String getMensaje_enlace() {
		return mensaje_enlace;
	}

	public void setMensaje_enlace(String mensaje_enlace) {
		this.mensaje_enlace = mensaje_enlace;
	}

}

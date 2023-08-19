package com.example.examen_pmi_iiip;

public class Contacto {
    private String descripcion;
    private String periodista;
    private String fecha;
    private String archivo;
    private String id;
    private String orden;
    private String foto;


    public Contacto() { }
    public Contacto(String id, String orden, String descripcion, String periodista, String fecha, String foto, String archivo) {
        this.id = id;
        this.orden = orden;
        this.descripcion = descripcion;
        this.periodista = periodista;
        this.fecha = fecha;
        this.foto = foto;
        this.archivo = archivo;
    }

    public Contacto(String orden, String descripcion, String periodista, String fecha, String foto, String archivo) {
        this.orden = orden;
        this.descripcion = descripcion;
        this.periodista = periodista;
        this.fecha = fecha;
        this.foto = foto;
        this.archivo = archivo;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setdescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getperiodista() {
        return periodista;
    }

    public void setPeriodista(String periodista) {
        this.periodista = periodista;
    }

    public String getfecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }
}


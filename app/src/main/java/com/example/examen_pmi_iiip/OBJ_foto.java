package com.example.examen_pmi_iiip;

import android.graphics.Bitmap;

public class OBJ_foto {
    String id;
    String orden;
    String descripcion;
    String periodista;
    Bitmap imagen;

    public OBJ_foto(Bitmap imagen, String orden)
    {
        this.imagen = imagen;
        this.orden = orden;
        this.descripcion = "";
        this.periodista = "";
        this.id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public void setorden(String orden) {
        this.orden = orden;
    }

    public Bitmap getImagen()
    {
        return imagen;
    }

    public String getorden()
    {
        return orden;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPeriodista() {
        return periodista;
    }

    public void setPeriodista(String periodista) {
        this.periodista = periodista;
    }
}

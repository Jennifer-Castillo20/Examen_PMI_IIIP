package com.example.examen_pmi_iiip;

public class RestApiMethods {
    public static final String baseApiUrl = "http://192.168.0.7/Examen_IIIP_PM1/";
    public static final String separador = "/";
    public static final String ApiPost = baseApiUrl + "Crear.php";
    public static final String ApiGet = baseApiUrl + "Listado.php";
    public static final String ApiUpd = baseApiUrl + "Actualizar.php";

    public static final String ApiDel = baseApiUrl + "Eliminar.php?id=";

    public static final String ApiGetID = baseApiUrl + "Info.php?id=";
}

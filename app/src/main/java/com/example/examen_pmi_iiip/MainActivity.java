package com.example.examen_pmi_iiip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

public class MainActivity extends AppCompatActivity {
    ImageView imgFoto, Prueba;
    Button Btn_Grabar, Btn_Lista, Btn_Guardar;//
    TextView Txt_Orden, Txt_Descripcion, Txt_Pediodista, Txt_Fecha;
    ImageButton btntomafoto;


    Uri outputFileUri;
    String currentfotopath;
    String respuesta="";

    Contacto contacto;
    byte[] img_array;


    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_CAMERA = 1;
    static final int PETICION_ACCESO_PERMISOS = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btntomafoto = (ImageButton) findViewById(R.id.Btn_Foto1);
        Btn_Guardar = (Button) findViewById(R.id.Btn_Guardar);
        Btn_Lista = (Button) findViewById(R.id.Btn_Lista);

        contacto = new Contacto("","","","","","", "");


        imgFoto = (ImageView) findViewById(R.id.Iv_Foto);
        Txt_Orden = (EditText) findViewById(R.id.Txt_Orden);
        Txt_Descripcion = (EditText) findViewById(R.id.Txt_Descripcion);
        Txt_Pediodista = (EditText) findViewById(R.id.Txt_Periodista);
        Txt_Fecha = (TextView) findViewById(R.id.Txt_Fecha);
        imgFoto.setImageResource(R.drawable.usuario);

        Btn_Lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListaActivity.class));
            }
        });



        btntomafoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {permisos(); }
        });



        Btn_Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregar();
            }
        });


    }



    //dialogo de tomar foto o galeria
    private void selectImage() {
        final CharSequence[] items = {"Selecciona de Galeria",
                "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Añadir Foto");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Tomar Foto")) {
//
                    File photoFile = null;
                    Intent tomarfoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri photoURI = FileProvider.getUriForFile(MainActivity.this,"com.example.examen_pmi_iiip.provider",photoFile);
                    if(tomarfoto.resolveActivity(getPackageManager())!=null){
                        tomarfoto.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(tomarfoto,REQUEST_CAMERA);
                    }
                } else if (items[item].equals("Selecciona de Galeria")) {
                    if (Build.VERSION.SDK_INT <= 19) {
                        Intent i = new Intent();
                        i.setType("image/*");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(i, SELECT_PICTURE);
                    } else if (Build.VERSION.SDK_INT > 19) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, SELECT_PICTURE);
                    }
                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //crear el fecha de la foto
    private File createImageFile() throws IOException {
        // Crear el archivo
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentfotopath = image.getAbsolutePath();
        return image;
    }

    //resultado de seleccionar de foto de la camara o galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File imgFile = new  File(currentfotopath);
                Uri selectedImageUri;
                try {
                    selectedImageUri = outputFileUri;
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    btmapOptions.inSampleSize = 2;
                    bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),btmapOptions);
                    bm = Bitmap.createScaledBitmap(bm, 300, 300, true);

                    imgFoto.setImageBitmap(bm);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    img_array = stream.toByteArray();

                    String encode = Base64.encodeToString(img_array, Base64.DEFAULT);//luego a base64
                    Calendar calendar = Calendar.getInstance();
                    DateFormat actual_date = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);//pasarle la fecha que se tomo
                    SimpleTimeZone yx = new SimpleTimeZone(0,"CST");
                    calendar.setTimeZone(yx);
                    contacto.setFoto(encode);
                    contacto.setArchivo(actual_date.format(calendar.getTime()));
                    obtenerLocalizacion();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
//                String selectedImagePath = getRealPathFromURI(selectedImageUri);
                String tempPath = getPath(selectedImageUri, this);

//                BitmapFactory.Options btmapOptions;
                Bitmap bm = BitmapFactory.decodeFile(tempPath);
                try {
                    bm = BitmapFactory.decodeStream(MainActivity.this.getContentResolver().openInputStream(selectedImageUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imgFoto.setImageBitmap(bm);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                img_array = stream.toByteArray();

                String encode = Base64.encodeToString(img_array, Base64.DEFAULT);//luego a base64
                Calendar calendar = Calendar.getInstance();
                DateFormat actual_date = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);//pasarle la fecha que se tomo
                SimpleTimeZone yx = new SimpleTimeZone(0,"CST");
                calendar.setTimeZone(yx);
                contacto.setFoto(encode);
                contacto.setArchivo(actual_date.format(calendar.getTime()));
                obtenerLocalizacion();
            }
        }
    }

    //metodo obtener path de imagen
    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //permisos de camara y ubicacion
    private void permisos() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PETICION_ACCESO_PERMISOS);
        } else {
            selectImage();
        }
    }

    ///////////////////////////////////////
    //METODOS DE UBICACION
    ///////////////////////////////////////
    //obtener la localizacion exacta del cel
    private void obtenerLocalizacion() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        encontrarUbicacion(getApplicationContext(), lm);
    }
    //metodo completo de encontrar ubicacion
    public void encontrarUbicacion(Context contexto, LocationManager locationManager) {
        String location_context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) contexto.getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                            String txtlongitud = String.valueOf(location.getLongitude());
                            String txtlatitud = String.valueOf(location.getLatitude());
                            //contacto.setLongitud(txtlongitud);
                            //contacto.setLatitud(txtlatitud);
                            //ongitud.setText(txtlongitud);
                            //Latitud.setText(txtlatitud);
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                String txtlongitud = String.valueOf(location.getLongitude());
                String txtlatitud = String.valueOf(location.getLatitude());
                //contacto.setLongitud(txtlongitud);
                //contacto.setLatitud(txtlatitud);
                //Longitud.setText(txtlongitud);
                //Latitud.setText(txtlatitud);
            }
        }
    }

    //subir la info
    private void agregar() {
        //validar los campos
        int comprobaciones = 0;
        int numeros = 0;
        if(Txt_Orden.getText().toString().isEmpty() || Txt_Descripcion.getText().toString().isEmpty()) {
            dialog_empty_fields();
            comprobaciones = 1;
        }
        if(contacto.getFoto() == "" && comprobaciones == 0) {
            dialog_foto_nofund();
            comprobaciones = 1;
        }
        if(comprobaciones == 0) {
            contacto.setPeriodista(Txt_Pediodista.getText().toString());
            contacto.setFecha(Txt_Fecha.getText().toString());
            JSONObject object = new JSONObject();
            String url = RestApiMethods.ApiPost;
            try
            {
                object.put("orden",contacto.getOrden());
                object.putOpt("descripcion",contacto.getDescripcion());
                object.putOpt("periodista",contacto.getperiodista());
                object.putOpt("fecha",contacto.getfecha());
                object.putOpt("imagen",contacto.getFoto());
                object.putOpt("archivo",contacto.getArchivo());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String Error = response.getString("httpStatus");
                                if (Error.equals("")||Error.equals(null)){
                                }else if(Error.equals("OK")){
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Toast.makeText(AddActivity.this,"ERROR"+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(getApplicationContext(),"Contacto Guardado",Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                    Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
            Limpiar_Fields();
        }
    }

    private void Limpiar_Fields() {
        img_array = new byte[0];
        imgFoto.setImageResource(R.drawable.usuario);
        contacto.setOrden("");
        contacto.setdescripcion("");
        contacto.setPeriodista("");
        contacto.setFecha("");
        contacto.setFoto("");
        contacto.setArchivo("");
        Txt_Orden.setText("");
        Txt_Descripcion.setText("");
        Txt_Pediodista.setText("");
        Txt_Fecha.setText("");
    }
    private void dialog_empty_fields() {
        new AlertDialog.Builder(this)
                .setTitle("CAMPOS VACIOS")
                .setMessage("Se requiere llenar todos los campos")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }

    private void dialog_foto_nofund() {
        new AlertDialog.Builder(this)
                .setTitle("Ingrese una IMAGEN")
                .setMessage("INGRESE UNA FOTOGRAFIA")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }



//El ultimo }
}
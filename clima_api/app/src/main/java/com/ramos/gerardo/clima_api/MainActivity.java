package com.ramos.gerardo.clima_api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static TextView ciudad;
    static TextView farenheit;
    static TextView centigrados;
    static TextView erro;
    static TextView estadoclima;
    GPSTracker gps;
    String Direccion;
    private static LocationManager locManager;
    private String Latitud;
    private String Longitud;
    boolean permisos = false;
    private static LocationListener locListener;
    Context context;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    Weather getData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;


        farenheit = (TextView)findViewById(R.id.t_Temperatura);
        centigrados = (TextView)findViewById(R.id.t_cent);
        estadoclima = (TextView)findViewById(R.id.t_EstadoClima);
        permisos = checkAndRequestPermissions();



        getData = new Weather();


        Geocoder geocoder = null;
        LocationManager manager = (LocationManager) getSystemService(MainActivity.this.LOCATION_SERVICE);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            comenzarLocalizacion();

        }
    }

    private void buildAlertMessageNoGps() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Pemite que la aplicación use GPS para definir su ubicación?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void comenzarLocalizacion() {


        try {

            //Obtenemos la �ltima posici�n conocida
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //Mostramos la �ltima posici�n conocida
            Actualizar_Posicion(loc);

            //Nos registramos para recibir actualizaciones de la posici�n
            locListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Log.i("mostrarPosicion", "mostrarPosicion");
                    Actualizar_Posicion(location);
                }

                public void onProviderDisabled(String provider) {
                    //estado = "Provider OFF";
                    //Log.i("", "Provider OFF");

                }

                public void onProviderEnabled(String provider) {
                    //estado = "Provider ON";
                    //Log.i("", "Provider ON");

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    //Log.i("", "Provider Status: " + status);
                    //estado2 = "Provider Status: " + status;
                }
            };

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locListener);
        }
        catch(Exception k)
        {
            Toast.makeText(context, "comenzarLocalizacion: \n" + k, Toast.LENGTH_LONG).show();
        }
    }
    public void Actualizar_Posicion(Location loc) {

            try {

                    try {

                        List<Address> list2 = null;
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        list2 = geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1);

                        if (!list2.isEmpty()) {
                            Address address = list2.get(0);
                            Direccion = address.getAddressLine(0);
                        }

                    } catch (IOException e) {
                    }
                    Latitud = loc.getLatitude() + "";
                    Longitud = loc.getLongitude() + "";
                    //Toast.makeText(context, "Ciudad: \n" + Direccion, Toast.LENGTH_LONG).show();

                     ciudad = (TextView)findViewById(R.id.t_Ciudad) ;
                    String[] valorLeidodata2 = Direccion.split(",");

                    ciudad.setText(valorLeidodata2[1]+"\n"+valorLeidodata2[2]);
                    SharedPreferences prefs = context.getSharedPreferences("mi_ubicacion", MainActivity.this.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    String dataconf = Latitud + ";" + Longitud + ";" + Direccion;
                    editor.putString("data", dataconf);
                    editor.commit();

                    getData.execute("https://api.darksky.net/forecast/9f8801c845bf1ddad1831f2c94bf9cf6/"+Latitud+","+Longitud);
                    //getData.execute("https://api.darksky.net/forecast/9f8801c845bf1ddad1831f2c94bf9cf6/4.6113582,-74.1907839");

            } catch (Exception k) {
                //Toast.makeText(context, "mostrarPosicion: \n" + k, Toast.LENGTH_LONG).show();
            }


    }

    private  boolean checkAndRequestPermissions() {
        int readPhoneState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int accessWifiState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE);
        int accessCoarseLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_SETTINGS);

        List<String> listPermissionsNeeded = new ArrayList<>();

//-------OTROS PERMISOS----------
//        WRITE_EXTERNAL_STORAGE
//        ACCESS_NETWORK_STATE
//        CHANGE_WIFI_STATE
//        INTERNET
//-------------------------------

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_SETTINGS);
        }
        if (accessWifiState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        }
        return true;
    }
}

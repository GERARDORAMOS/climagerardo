package com.ramos.gerardo.clima_api;

/**
 * Created by ASUS X450L on 01/03/2018.
 */
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Vamshi on 5/14/2017.
 */

public class Weather extends AsyncTask<String,Void,String> {

    String result;
    @Override
    protected String doInBackground(String... urls) {
        result = "";
        URL link;
        HttpURLConnection myconnection = null;

        try {
            link = new URL(urls[0]);
            myconnection = (HttpURLConnection)link.openConnection();
            InputStream in = myconnection.getInputStream();
            InputStreamReader myStreamReader = new InputStreamReader(in);
            int data = myStreamReader.read();
            while(data!= -1){
                char current = (char)data;
                result+= current;
                data = myStreamReader.read();
            }
            return result;
        } catch (Exception e) {
            MainActivity.erro.setText("Error:"+e+"");
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {

            JSONObject myObject = new JSONObject(result);
            JSONObject main = new JSONObject(myObject.getString("currently"));
            //JSONObject sumary = new JSONObject(myObject.getString("minutely"));

            String temperature = main.getString("temperature");
            String estado_clima = main.getString("summary");

            float _temperatura = (float) Math.round(Double.parseDouble(temperature));




            float _temperatura_cen = _temperatura - 32;
            _temperatura_cen = (float) (_temperatura_cen * 0.55);



            MainActivity.farenheit.setText(_temperatura  + " farenheit");
            MainActivity.centigrados.setText(_temperatura_cen  + "°");
            MainActivity.estadoclima.setText(estado_clima);


        } catch (Exception e) {
            MainActivity.erro.setText("Error:"+e+"");

        }
    }
}
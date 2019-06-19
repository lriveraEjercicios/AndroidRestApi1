package com.example.restapi1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView txtConnection;
    ProgressDialog dialog;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtConnection = findViewById(R.id.txtConnection);
        progressbar = findViewById(R.id.progressbar);
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void checkConnection(View view) {
        if (isConnected()) {

            //Mostrar alert de Loading:
            //dialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
            progressbar.setVisibility(View.VISIBLE);
            HttpAsyncTask tarea = new HttpAsyncTask();

            tarea.execute("https://jsonplaceholder.typicode.com/users");
        } else {
            txtConnection.setText("No hay conexión :(");
        }
    }

    public void getFromRetrofit(View view) {

        MyService service = RetrofitClientInstance.getRetrofitInstance().create(MyService.class);

        Call<Joke> call = service.getJoke();
        call.enqueue(new Callback<Joke>() {
            @Override
            public void onResponse(Call<Joke> call, Response<Joke> response) {
                String value = response.body().getValue();
                txtConnection.setText(value);
            }

            @Override
            public void onFailure(Call<Joke> call, Throwable t) {
                txtConnection.setText("Error de Chuck Norris");
            }
        });
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

           /*Solución para la API REST https://api.chucknorris.io/jokes/random
            try {
                //Pasar result a JSONObject:
                JSONObject jsonObj = new JSONObject(result);
                //Recoger el campo que queremos, value
                txtConnection.setText(jsonObj.getString("value"));
            } catch (JSONException e) {
                e.printStackTrace();
                txtConnection.setText("Error:Formato incorrecto!");
            }
            */

            /*Solución para la API REST https://jsonplaceholder.typicode.com/users (listado)*/
            try {
                JSONArray jsonArray = new JSONArray(result);
                String sName = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    sName += jsonArray.getJSONObject(i).getString("name") + " ";
                }
                txtConnection.setText(sName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Quitar el loading
            //dialog.dismiss();
            progressbar.setVisibility(View.GONE);


        }
    }

    public static String GET(String sUrl) {
        URL url;
        String result = "";
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(sUrl);
            urlConnection = (HttpURLConnection) url.openConnection(); //abrir la conexión

            //Leer el resultado
            InputStream in = urlConnection.getInputStream();
            result = convertInputStreamToString(in);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        //Devuelve el resultado (lo recogeremos en el onPostExecute)
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}

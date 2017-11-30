package com.example.tomas.webservicealumnos;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button consultar;
    Button consultarporid;
    Button insertar;
    Button borrar;
    Button actualizar;

    EditText identificador;
    EditText nombre;
    EditText direccion;

    TextView resultado;

    final String  IP="http://192.168.0.92/webservicesalumnos";
    final String GET=IP+"/obtener_alumnos.php";
    final String GET_BY_ID=IP+"/obtener_alumno_por_id.php";
    final String UPDATE=IP+"/actualizar_alumno.php";
    final String DELETE=IP+"/borrar_alumno.php";
    final String INSERT=IP+"/insertar_alumno.php";

    ObtenerWebService hiloconexion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consultar=(Button)findViewById(R.id.consultar);
        consultarporid=(Button)findViewById(R.id.consultarid);
        insertar=(Button)findViewById(R.id.insertar);
        borrar=(Button)findViewById(R.id.borrar);
        actualizar=(Button)findViewById(R.id.actualizar);

        identificador=(EditText)findViewById(R.id.eid);
        nombre=(EditText)findViewById(R.id.enombre);
        direccion=(EditText)findViewById(R.id.edireccion);

        resultado=(TextView)findViewById(R.id.resultado);

        consultar.setOnClickListener(this);
        consultarporid.setOnClickListener(this);
        insertar.setOnClickListener(this);
        borrar.setOnClickListener(this);
        actualizar.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.consultar:
                hiloconexion=new ObtenerWebService();
                hiloconexion.execute(GET,"1");
                break;
            case R.id.consultarid:
                break;
            case R.id.insertar:
                break;
            case R.id.borrar:
                break;
            case R.id.actualizar:
                break;

        }

    }

    public class ObtenerWebService extends AsyncTask<String,Void,String>{
        public ObtenerWebService() {
            super();
        }

        @Override
        protected void onPostExecute(String s) {
            resultado.setText(s);
        }

        @Override
        protected String doInBackground(String... params) {
            String cadena=params[0]; //URL donde pedimos la info
            URL url=null;
            String devuelve="";

            if(params[1]=="1"){ //consulta de todos los alumnos
                try {
                    url=new URL(cadena);
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    int respuesta=connection.getResponseCode();
                    StringBuilder result= new StringBuilder();
                    if(respuesta==HttpURLConnection.HTTP_OK){
                        InputStream in=new BufferedInputStream(connection.getInputStream());
                        BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                        String line;
                        line=reader.readLine();
                        while(line!=null){
                            result.append(line);
                            line=reader.readLine();
                        }
                        JSONObject respuestaJSON=new JSONObject(result.toString());
                        String resultJSON=respuestaJSON.getString("estado");
                        if(resultJSON.equals("1")){
                            JSONArray alumnosJSON=respuestaJSON.getJSONArray("alumnos");
                            for(int i=0;i<alumnosJSON.length();i++){
                                devuelve=devuelve+alumnosJSON.getJSONObject(i).getString("idalumnos")+" " +
                                        alumnosJSON.getJSONObject(i).getString("nombre")+" " +
                                        alumnosJSON.getJSONObject(i).getString("direccion")+"\n";
                            }
                        }else{
                            devuelve="no hay alumnos";
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return devuelve;

            }else if(params[1]=="2"){
                try {
                    url = new URL(cadena);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                    int respuesta = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();
                    if (respuesta == HttpURLConnection.HTTP_OK){
                        InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);        // Paso toda la entrada al StringBuilder
                        }

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                        JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                        //Accedemos al vector de resultados

                        String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                        if (resultJSON.equals("1")){      // hay un alumno que mostrar
                            devuelve = devuelve + respuestaJSON.getJSONObject("alumno").getString("idAlumno") + " " +
                                    respuestaJSON.getJSONObject("alumno").getString("nombre") + " " +
                                    respuestaJSON.getJSONObject("alumno").getString("direccion");

                        }
                        else if (resultJSON.equals("2")){
                            devuelve = "No hay alumnos";
                        }

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return devuelve;

            }


            return devuelve;
        }
    }
}

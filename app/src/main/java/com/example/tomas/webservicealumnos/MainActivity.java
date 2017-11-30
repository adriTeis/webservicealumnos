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
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

    final String IP = "http://192.168.0.92/webservicesalumnos";
    final String GET = IP + "/obtener_alumnos.php";
    final String GET_BY_ID = IP + "/obtener_alumno_por_id.php";
    final String UPDATE = IP + "/actualizar_alumno.php";
    final String DELETE = IP + "/borrar_alumno.php";
    final String INSERT = IP + "/insertar_alumno.php";

    MetodoAsinkTask hiloconexion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consultar = (Button) findViewById(R.id.consultar);
        consultarporid = (Button) findViewById(R.id.consultarid);
        insertar = (Button) findViewById(R.id.insertar);
        borrar = (Button) findViewById(R.id.borrar);
        actualizar = (Button) findViewById(R.id.actualizar);

        identificador = (EditText) findViewById(R.id.eid);
        nombre = (EditText) findViewById(R.id.enombre);
        direccion = (EditText) findViewById(R.id.edireccion);

        resultado = (TextView) findViewById(R.id.resultado);

        consultar.setOnClickListener(this);
        consultarporid.setOnClickListener(this);
        insertar.setOnClickListener(this);
        borrar.setOnClickListener(this);
        actualizar.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.consultar:
                hiloconexion = new MetodoAsinkTask();
                hiloconexion.execute(GET, "1");
                break;
            case R.id.consultarid:
                break;
            case R.id.insertar:
                //Creo un objeto conexion
                hiloconexion = new MetodoAsinkTask();
                //le paso el tipo de parametros que el el insert que creamos arriba
                //el identificador que es el 3 y los valores recogidos.
                hiloconexion.execute(INSERT, "3", nombre.getText().toString(), direccion.getText().toString());
                break;
            case R.id.borrar:
                break;
            case R.id.actualizar:
                break;

        }

    }

    public class MetodoAsinkTask extends AsyncTask<String, Void, String> {
        public MetodoAsinkTask() {
            super();
        }

        @Override
        protected void onPostExecute(String s) {
            resultado.setText(s);
        }

        @Override
        protected String doInBackground(String... params) {
            String cadena = params[0]; //URL donde pedimos la info
            URL url = null;
            String devuelve = "";

            if (params[1] == "1") { //consulta de todos los alumnos
                try {
                    url = new URL(cadena);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    int respuesta = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();
                    if (respuesta == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        line = reader.readLine();
                        while (line != null) {
                            result.append(line);
                            line = reader.readLine();
                        }
                        JSONObject respuestaJSON = new JSONObject(result.toString());
                        String resultJSON = respuestaJSON.getString("estado");
                        if (resultJSON.equals("1")) {
                            JSONArray alumnosJSON = respuestaJSON.getJSONArray("alumnos");
                            for (int i = 0; i < alumnosJSON.length(); i++) {
                                devuelve = devuelve + alumnosJSON.getJSONObject(i).getString("idalumnos") + " " +
                                        alumnosJSON.getJSONObject(i).getString("nombre") + " " +
                                        alumnosJSON.getJSONObject(i).getString("direccion") + "\n";
                            }
                        } else {
                            devuelve = "no hay alumnos";
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

            } else if (params[1] == "2") {
                try {
                    url = new URL(cadena);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                    int respuesta = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();
                    if (respuesta == HttpURLConnection.HTTP_OK) {
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

                        if (resultJSON.equals("1")) {      // hay un alumno que mostrar
                            devuelve = devuelve + respuestaJSON.getJSONObject("alumno").getString("idAlumno") + " " +
                                    respuestaJSON.getJSONObject("alumno").getString("nombre") + " " +
                                    respuestaJSON.getJSONObject("alumno").getString("direccion");

                        } else if (resultJSON.equals("2")) {
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

                } else if(params[1]=="3") {

                    try {
                        //creo un objeto conexion y creo la coneccion----------
                        //Le paso la peticion con la ip y la ruta a pedir
                        HttpURLConnection urlConnection;
                        DataOutputStream out;
                        DataInputStream Imput;
                        url = new URL(cadena);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        //le indico qu evoy a hacerle una consulta al servidor de tipo Post
                        urlConnection.setDoInput(true);
                        //le indico que voy a recivir datos del servidor
                        urlConnection.setDoOutput(true);
                        //le digo que cuando me lleguen los datos que vacie la cache.
                        urlConnection.setUseCaches(false);
                        urlConnection.setRequestProperty("Content-type", "application/json");
                        urlConnection.setRequestProperty("Accept", "application/json");
                        urlConnection.connect();
                        //Creo el objeto JSON
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("nombre", params[2]);
                        jsonParam.put("direccion", params[3]);
                        //Envio los datos por POST y le paso UTF-8 para indicarle que tipo de codoficacion es
                        OutputStream OS = urlConnection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                        writer.write(jsonParam.toString());
                        writer.flush();
                        writer.close();

                        int respuesta = urlConnection.getResponseCode();
                        StringBuilder result = new StringBuilder();
                        if (respuesta == HttpURLConnection.HTTP_OK) {
                            String line;
                            BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
                            while ((line = br.readLine()) != null) {
                                result.append(line);


                            }
                            //Cuando hacemos un peticion php y solicitamos el estado, que esta programado
                            //el codigo phppara que guarde un valor en estado 1 si fue bien y 2 si fue mal.
                            JSONObject respuestaJSON = new JSONObject(result.toString());
                            String resultJSON = respuestaJSON.getString("estado");
                            if (resultJSON.equals("1")) {
                                devuelve = "Alumno insertado correctamente";
                            } else if (resultJSON.equals("2")) {
                                devuelve = "El alumno no se pudo insertar";
                            }

                        }





                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


                return devuelve;
            }
        }
    }

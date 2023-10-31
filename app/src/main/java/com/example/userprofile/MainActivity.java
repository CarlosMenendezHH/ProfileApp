package com.example.userprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.userprofile.Clases.ConexionesAPI;
import com.example.userprofile.Clases.NetWorkBoolean;
import com.example.userprofile.Clases.Progress;
import com.example.userprofile.DB.DBPerfil_Usuario;
import com.example.userprofile.retrofit.ApiClient;
import com.example.userprofile.retrofit.profile.ModifyProfileService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    EditText nombres, correo, fecha;
    Button edit;
    DBPerfil_Usuario perfil_usuario;
    ConexionesAPI conexionesAPI;
    Progress progress = new Progress(this);
    Calendar c3 = new GregorianCalendar();
    public static final int MY_DEFAULT_TIMEOUT4 = 10000;
    private Handler handler;
    private Runnable delayedTask;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conexionesAPI = new ConexionesAPI();

        nombres = findViewById(R.id.edit_nombre_pro);
        correo = findViewById(R.id.edit_correo_pro);
        fecha = findViewById(R.id.edit_fecha_pro);
        edit = findViewById(R.id.btn_edit);
        edit.setEnabled(false);

        cargarDatos();

        fecha.setOnClickListener(view -> {
            edit.setEnabled(true);
            final DatePickerDialog.OnDateSetListener fec = new DatePickerDialog.OnDateSetListener() {
                public void updateLabel3() {
                    String format = "yyyy-MM-dd";
                    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

                    fecha.setText(sdf.format(c3.getTime()));
                }

                @Override
                public void onDateSet(DatePicker view, int year, int monthofyear, int day) {
                    c3.set(Calendar.YEAR, year);
                    c3.set(Calendar.MONTH, monthofyear);
                    c3.set(Calendar.DATE, day);
                    updateLabel3();
                }

            };
            new DatePickerDialog(MainActivity.this, fec, c3.get(Calendar.YEAR), c3.get(Calendar.MONTH), c3.get(Calendar.DATE)).show();
        });

        edit.setOnClickListener(view -> {
            perfil_usuario = new DBPerfil_Usuario(this);
            perfil_usuario.ActualizarProfile(separar(nombres.getText().toString(), 1), separar(nombres.getText().toString(), 2), correo.getText().toString(), fecha.getText().toString());
            ActualizarDato(id, separar(nombres.getText().toString(), 1), separar(nombres.getText().toString(), 2), correo.getText().toString(), fecha.getText().toString());
        });
    }

    private void ActualizarDato(int id, String nombre, String apellidp, String correo, String fecha) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("Nombre", nombre);
        datos.put("Apellido", apellidp);
        datos.put("email", correo);
        datos.put("fechaNac", fecha);

        Retrofit retrofit = ApiClient.getRetrofit();

        // Crear una instancia del ApiService
        ModifyProfileService apiService = retrofit.create(ModifyProfileService.class);

        // Realizar la llamada a la API

        Call<Void> call = apiService.modifyuser(id, datos);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    handler = new Handler(Looper.getMainLooper());
                    delayedTask = new Runnable() {
                        @Override
                        public void run() {
                            if (NetWorkBoolean.isNetworkConnected(MainActivity.this) || NetWorkBoolean.isWifiConnected(MainActivity.this)){
                                Toast.makeText(MainActivity.this, "Datos Actualizados correctamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(MainActivity.this, "Error al Actualizar", Toast.LENGTH_LONG).show();
                                progress.dismissDialog();
                            }
                        }
                    };
                    handler.postDelayed(delayedTask, 1000);
                } else {
                    Toast.makeText(MainActivity.this, "Error al llamar", Toast.LENGTH_LONG).show();
                    progress.dismissDialog();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo de Conexion", Toast.LENGTH_LONG).show();
                progress.dismissDialog();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void cargarDatos() {
        perfil_usuario = new DBPerfil_Usuario(this);
        Cursor cursor = perfil_usuario.GetProfile();
        if (cursor.moveToFirst()){
            do {
                nombres.setText(cursor.getString(3) + " - " + cursor.getString(4));
                correo.setText(cursor.getString(5));
                fecha.setText(cursor.getString(6));
                ObtenerID(conexionesAPI.getAPI() + "UserTEMPs?correo=" + cursor.getString(5));
            }while (cursor.moveToNext());
        }
    }

    private String separar(String name, int i){
        String[] parts = name.split(" - ");
        String t;
        if (i == 1){
            t = parts[0];
        }else {
            t = parts[1];
        }
        return t;
    }

    private void ObtenerID(String URL){
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int idUser = jsonObject.getInt("idUser");
                                id = idUser;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        arrayRequest.setRetryPolicy(new DefaultRetryPolicy(MY_DEFAULT_TIMEOUT4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(arrayRequest);
    }
}
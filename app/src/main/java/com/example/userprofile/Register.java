package com.example.userprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.userprofile.Clases.ConexionesAPI;
import com.example.userprofile.Clases.Progress;
import com.example.userprofile.DB.DBPerfil_Usuario;
import com.example.userprofile.retrofit.ApiClient;
import com.example.userprofile.retrofit.profile.AddProfileService;
import com.example.userprofile.retrofit.profile.ProfileResponses;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {

    EditText nombres, apellidos, correo, contra;
    Button register;
    ConexionesAPI conexionesAPI;
    DBPerfil_Usuario perfil_usuario;
    AddProfileService addProfileService;
    Progress progress = new Progress(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        perfil_usuario = new DBPerfil_Usuario(this);
        conexionesAPI = new ConexionesAPI();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(conexionesAPI.getAPI())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        addProfileService = retrofit.create(AddProfileService.class);

        nombres = findViewById(R.id.edit_nombre_register);
        apellidos = findViewById(R.id.edit_apellido_register);
        correo = findViewById(R.id.edit_correo_register);
        contra = findViewById(R.id.password_register);
        register = findViewById(R.id.btn_register);

        register.setOnClickListener(view -> {
            progress.startProgressDialog();
            register.setEnabled(false);
            perfil_usuario = new DBPerfil_Usuario(this);
            perfil_usuario.Registrar(nombres.getText().toString(), contra.getText().toString(), nombres.getText().toString(), apellidos.getText().toString(), correo.getText().toString(), GetDate());
            crearusuario();
        });

    }

    private String GetDate() {
        Date fecha = new Date();

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.format(fecha);
    }

    private void crearusuario(){
        String jsonBody = "{\"Nombre\":\"" + nombres.getText().toString() + "\",\"Apellido\":\"" + apellidos.getText().toString() + "\",\"email\":\"" + correo.getText().toString() + "\",\"Password\":\"" + contra.getText().toString() + "\"}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Call<ProfileResponses> call = addProfileService.newUser(requestBody);
        call.enqueue(new Callback<ProfileResponses>() {
            @Override
            public void onResponse(Call<ProfileResponses> call, Response<ProfileResponses> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Register.this, "Perfil de Usuario creado Correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Procesar respuesta de error
                    Toast.makeText(Register.this, "Error al crear perfil", Toast.LENGTH_SHORT).show();
                    progress.dismissDialog();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponses> call, Throwable t) {
                // Procesar error de la llamada
                Toast.makeText(Register.this, "Error en la llamada", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                progress.dismissDialog();
            }
        });
    }
}
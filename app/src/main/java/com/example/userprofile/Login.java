package com.example.userprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userprofile.Clases.Progress;
import com.example.userprofile.DB.DBPerfil_Usuario;
import com.example.userprofile.retrofit.profile.AddProfileService;

public class Login extends AppCompatActivity {

    EditText correo, pass;
    Button login;
    TextView register;
    DBPerfil_Usuario perfil_usuario;
    Progress progress = new Progress(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        correo = findViewById(R.id.edit_correo);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.btn_iniciar_sesion);
        register = findViewById(R.id.register_txt);

        login.setOnClickListener(view -> {
            progress.startProgressDialog();
            perfil_usuario = new DBPerfil_Usuario(this);
            Cursor cursor = perfil_usuario.InicioSesion(correo.getText().toString(), pass.getText().toString());
            if (cursor.moveToFirst()){
                Toast.makeText(Login.this, "Bienvenido/a " + cursor.getString(1), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(Login.this, "No estas Registrado/a", Toast.LENGTH_SHORT).show();
                progress.dismissDialog();
            }
        });

        register.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });
    }
}
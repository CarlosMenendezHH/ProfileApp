package com.example.userprofile.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBPerfil_Usuario extends SQLiteOpenHelper {
    public DBPerfil_Usuario(@Nullable Context context) {
        super(context, "perfil1", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table Perfil_Usuario (idUser integer primary key autoincrement" +
                ", User text" +
                ", Password text" +
                ", Nombres text" +
                ", Apellidos text" +
                ", Email text" +
                ", FechaNacimiento text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Perfil_Usuario");
    }

    public void Registrar(String usuario, String contra, String nombre, String apellido, String email, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues datos = new ContentValues();
        datos.put("User", usuario);
        datos.put("Password", contra);
        datos.put("Nombres", nombre);
        datos.put("Apellidos", apellido);
        datos.put("Email", email);
        datos.put("FechaNacimiento", fecha);
        db.insert("Perfil_Usuario", null, datos);
    }

    public Cursor InicioSesion(String correo, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from Perfil_Usuario where Email = '" + correo + "' and Password = '" + pass + "'", null);
    }

    public Cursor GetProfile() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from Perfil_Usuario Limit 1", null);
    }

    public void ActualizarProfile(String nombre, String apellido, String correo, String Fecha){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Update Perfil_Usuario set Nombres = '" + nombre + "', Apellidos = '" + apellido + "', Email = '" + correo + "', FechaNacimiento = '" + Fecha + "'");
    }
}

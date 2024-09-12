package net.ddsmedia.baceh.asistencia.qr.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyOpenHelper extends SQLiteOpenHelper {

    private static final String CREAR_TABLA_BENEFICIARIO="CREATE TABLE beneficiarios(id_titular INTEGER NOT NULL, credencial TEXT NOT NULL, fecha_visita TEXT NOT NULL, fk_municipio INTEGER NOT NULL, estatus INTEGER NOT NULL, fecha_baja TEXT NOT NULL, faltas INTEGER NOT NULL, ultima_visita TEXT NOT NULL, ultima_falta TEXT NOT NULL, bajas INTEGER NOT NULL, tipo INTEGER NOT NULL, observaciones_asist TEXT NOT NULL, id_integrante TEXT, parentesco TEXT NOT NULL, nombre TEXT NOT NULL, apaterno TEXT NOT NULL, amaterno TEXT NOT NULL, lista TEXT NOT NULL, sincronizar INTEGER NOT NULL, espera INTEGER NOT NULL)";
    private static final String CREAR_TABLA_MUNICIPIO="CREATE TABLE listas(lista INTEGER UNIQUE NOT NULL)";
    private static final String CREAR_TABLA_ASISTENCIA="CREATE TABLE asistencia(id INTEGER PRIMARY KEY AUTOINCREMENT, fk_titular INTEGER NOT NULL, credencial TEXT NOT NULL, fecha TEXT DEFAULT (datetime('now','localtime')) NOT NULL, ultima_visita TEXT NOT NULL, nombre TEXT NOT NULL, usuario TEXT NOT NULL, sincronizar INTEGER NOT NULL, lista TEXT NOT NULL)";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NOMBRE = "usuarios.sqlite";

    public MyOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_BENEFICIARIO);
        db.execSQL(CREAR_TABLA_MUNICIPIO);
        db.execSQL(CREAR_TABLA_ASISTENCIA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_Version) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }
}

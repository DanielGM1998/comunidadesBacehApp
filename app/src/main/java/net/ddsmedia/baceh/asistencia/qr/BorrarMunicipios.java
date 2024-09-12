package net.ddsmedia.baceh.asistencia.qr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import net.ddsmedia.baceh.R;
import net.ddsmedia.baceh.asistencia.qr.db.MyOpenHelper;
import net.ddsmedia.baceh.asistencia.qr.entidad.Beneficiarios;

import java.util.ArrayList;

public class BorrarMunicipios extends AppCompatActivity {

    Spinner spinListaMunicipios;
    Button btnEliminar, btnEliminarTodo;
    ArrayList<String> listaInformacion;
    ArrayList<Beneficiarios> listas;
    MyOpenHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_municipios);

        this.setTitle("Eliminar beneficiarios");

        conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
        spinListaMunicipios  = findViewById(R.id.spinListaMunicipios);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnEliminarTodo = findViewById(R.id.btnEliminarTodo);
        consultarListaMunicipios();
        ArrayAdapter adaptador = new ArrayAdapter(this, R.layout.spinnermunicipios,listaInformacion);
        spinListaMunicipios.setAdapter(adaptador);

        btnEliminarTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mensaje=new AlertDialog.Builder(BorrarMunicipios.this);
                mensaje.setTitle("¿Deseas eliminar todos los beneficiarios?");
                mensaje.setCancelable(false);
                mensaje.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyOpenHelper myOpenHelper = new MyOpenHelper(BorrarMunicipios.this,MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
                        SQLiteDatabase db = myOpenHelper.getWritableDatabase();
                        db.execSQL("Delete from beneficiarios");
                        Toast.makeText(BorrarMunicipios.this, "Todos los beneficiarios eliminados", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BorrarMunicipios.this, Inicio.class);
                        startActivity(intent);
                    }
                });
                mensaje.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                mensaje.show();


            }
        });

        spinListaMunicipios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {

                //Toast.makeText(BorrarMunicipios.this, ""+listaMunicipios.get(position).getLista(), Toast.LENGTH_SHORT).show();

                btnEliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder mensaje=new AlertDialog.Builder(BorrarMunicipios.this);
                        mensaje.setTitle("¿Deseas eliminar los beneficiarios de la lista "+listas.get(position).getLista()+"?");
                        mensaje.setCancelable(false);
                        mensaje.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyOpenHelper myOpenHelper = new MyOpenHelper(BorrarMunicipios.this,MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
                                SQLiteDatabase db = myOpenHelper.getWritableDatabase();
                                //Cursor cursor = db.rawQuery("SELECT Distinct beneficiarios.fk_municipio from beneficiarios inner join cat_municipio WHERE cat_municipio.id_municipio=beneficiarios.fk_municipio and cat_municipio.nombre='"+listaMunicipios.get(position).getFk_municipio()+"'",null);
                                //cursor.moveToFirst();

                                db.execSQL("Delete from beneficiarios WHERE lista="+listas.get(position).getLista());

                                //Cierre de conexión a base de datos
                                db.close();

                                Toast.makeText(BorrarMunicipios.this, "Beneficiarios de lista "+listas.get(position).getLista()+" eliminados", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(BorrarMunicipios.this, Inicio.class);
                                startActivity(intent);
                            }
                        });
                        mensaje.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        mensaje.show();
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Administrar.class);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        startActivity(intent);
    }

    private void consultarListaMunicipios(){
        SQLiteDatabase db = conn.getReadableDatabase();
        Beneficiarios beneficiarios = null;
        listas = new ArrayList<Beneficiarios>();

        Cursor cursor = db.rawQuery("SELECT DISTINCT lista from beneficiarios WHERE estatus=1 ORDER BY lista ASC",null);

        while(cursor.moveToNext()){
            beneficiarios = new Beneficiarios();
            beneficiarios.setLista(cursor.getString(0));
            listas.add(beneficiarios);
        }
        obtenerlista();
    }

    private void obtenerlista(){
        listaInformacion = new ArrayList<String>();
        //listaInformacion.add("Seleccionar");
        for(int i=0;i<listas.size();i++){
            listaInformacion.add("Lista "+listas.get(i).getLista());
        }
    }

}
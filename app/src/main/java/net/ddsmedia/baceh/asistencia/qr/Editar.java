package net.ddsmedia.baceh.asistencia.qr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.ddsmedia.baceh.R;
import net.ddsmedia.baceh.asistencia.qr.db.MyOpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Editar extends AppCompatActivity {

    EditText txtObservaciones_asist;
    TextView txtNombre, txtApaterno, txtAmaterno, txtCredencial, txtFaltas, txtUltima_falta;
    FloatingActionButton btnGuardar;
    String Cre, id_tit;
    int sinc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        this.setTitle("Editar beneficiario");

        txtNombre = findViewById(R.id.txtNombre);
        txtApaterno = findViewById(R.id.txtApaterno);
        txtAmaterno = findViewById(R.id.txtAmaterno);
        txtFaltas = findViewById(R.id.txtFaltas);
        txtObservaciones_asist = findViewById(R.id.txtObservaciones_asist);
        txtCredencial = findViewById(R.id.txtCredencial);
        txtUltima_falta = findViewById(R.id.txtUltima_falta);
        btnGuardar = findViewById(R.id.btnGuardar);

        Bundle datos = this.getIntent().getExtras();
        Cre = datos.getString("credencial");
        //Toast.makeText(this, ""+Cre, Toast.LENGTH_SHORT).show();

        MyOpenHelper conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT * from beneficiarios WHERE credencial='"+Cre+"'",null);
        c1.moveToFirst();

            txtNombre.setText(c1.getString(14));
            txtApaterno.setText(c1.getString(15));
            txtAmaterno.setText(c1.getString(16));
            txtCredencial.setText(Cre);
            txtFaltas.setText(c1.getString(6));
            txtObservaciones_asist.setText(c1.getString(11));
            txtUltima_falta.setText(c1.getString(8));
            id_tit=c1.getString(0);
            sinc=c1.getInt(17);

        modificar();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Administrar.class);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        startActivity(intent);
    }

    private void modificar(){

        //MODIFICAR usuario
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        MyOpenHelper dbHelper = new MyOpenHelper(Editar.this,MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                            //Modificar contacto
                            ContentValues cv = new ContentValues();
                            cv.put("observaciones_asist",txtObservaciones_asist.getText().toString().toUpperCase());
                            cv.put("sincronizar",3);
                            String[] args = new String[]{""+id_tit};
                            db.update("beneficiarios",cv,"id_titular=?",args);

                            //Cierre de conexión a base de datos
                            db.close();

                    Toast.makeText(Editar.this, "Beneficiario Modificado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Editar.this, Administrar.class);
                    startActivity(intent);
            }
        });
    }
}
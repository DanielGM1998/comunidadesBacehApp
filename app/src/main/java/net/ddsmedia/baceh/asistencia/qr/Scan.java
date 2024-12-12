package net.ddsmedia.baceh.asistencia.qr;

import static java.lang.Math.abs;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.ddsmedia.baceh.R;
import net.ddsmedia.baceh.asistencia.qr.db.MyOpenHelper;
import net.ddsmedia.baceh.asistencia.qr.entidad.Asistencia;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Scan extends AppCompatActivity {

    private TextView txtCredencial, txtNombre, txtFaltas, txtObservaciones_asist, txtUltima_falta, txtDay;
    private ImageView imgView;
    private IntentIntegrator qrScan;
    public static String id_usuario;
    String fecha, user;

    //LISTA
    ListView listViewBeneficiarios;
    ArrayList<String> listaInformacion;
    ArrayList<Asistencia> listaBeneficiarios;
    MyOpenHelper conn;
    ArrayAdapter adaptador;

    //AlertDialog
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button btnACEPTAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //SharedPreferences validación
        SharedPreferences preferences = getSharedPreferences("cred", Context.MODE_PRIVATE);
        id_usuario= preferences.getString("id_usuario", "");
        user = preferences.getString("log", "");

        //Fecha
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        fecha = df.format(calendar.getTime());

        //LISTA
        conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
        listViewBeneficiarios = findViewById(R.id.lstPaseLista);
        consultarListaPersonas();
        adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listaInformacion);
        listViewBeneficiarios.setAdapter(adaptador);

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor total = db.rawQuery("SELECT * from asistencia",null);
        total.moveToFirst();

        this.setTitle("Pase de lista ("+total.getCount()+")");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Inicio.class);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        startActivity(intent);
    }

    //LISTA
    private void consultarListaPersonas(){
        SQLiteDatabase db = conn.getReadableDatabase();
        Asistencia asistencia = null;
        listaBeneficiarios = new ArrayList<Asistencia>();
        Cursor cursor = db.rawQuery("SELECT * from asistencia ORDER BY fecha DESC",null);

        while(cursor.moveToNext()){
            asistencia = new Asistencia();
            asistencia.setCredencial(cursor.getString(2));
            asistencia.setFecha(cursor.getString(3));
            asistencia.setNombrebene(cursor.getString(5));
            listaBeneficiarios.add(asistencia);
        }
        obtenerlista();
    }

    private void obtenerlista(){
        listaInformacion = new ArrayList<String>();
        for(int i=0;i<listaBeneficiarios.size();i++){
            listaInformacion.add(listaBeneficiarios.get(i).getNombrebene()+"\n"+listaBeneficiarios.get(i).getCredencial()+"\n"+listaBeneficiarios.get(i).getFecha());
        }
    }

    //MENU ...
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuscan, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case R.id.menuPasedelista:
                    escanear();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //ESCANER
    private void escanear(){
        qrScan = new IntentIntegrator(this);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        qrScan.setPrompt("Escanear código");
        qrScan.setCameraId(0);
        qrScan.setOrientationLocked(false);
        qrScan.setBeepEnabled(false);
        qrScan.setCaptureActivity(CaptureActivityPortrait.class);
        qrScan.setBarcodeImageEnabled(false);
        qrScan.initiateScan();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        //AlertDialog
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);
        final View popupView = getLayoutInflater().inflate(R.layout.popup, null);
        txtCredencial= popupView.findViewById(R.id.txtCREDENCIAL);
        txtNombre=popupView.findViewById(R.id.txtNOMBRE);
        txtFaltas=popupView.findViewById(R.id.txtFALTAS);
        txtObservaciones_asist=popupView.findViewById(R.id.txtOBSERVACIONES_ASIST);
        txtUltima_falta=popupView.findViewById(R.id.txtULTIMA_FALTA);
        txtDay=popupView.findViewById(R.id.txtDAY);
        imgView=popupView.findViewById(R.id.imgView);
        btnACEPTAR=popupView.findViewById(R.id.btnACEPTAR);

        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "No se pudo leer el código QR. Intente nuevamente", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }else{

                MyOpenHelper conn;
                conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
                SQLiteDatabase db = conn.getReadableDatabase();

                Cursor exist = db.rawQuery("SELECT * from beneficiarios WHERE credencial='"+result.getContents()+"' AND estatus=1",null);
                Cursor exist2 = db.rawQuery("SELECT * from beneficiarios WHERE credencial='"+result.getContents()+"' AND estatus=2",null);
                if(exist.moveToFirst()){

                    //ult_visita
                    String ultima_visita = exist.getString(7);
                    //Log.i("","cambios"+ultima_visita);

                    if(ultima_visita.equals("0000-00-00")){
                        // correcto nuevo
                        //Log.i("","cambios1:"+ultima_visita);

                        Cursor cursor2 = db.rawQuery("SELECT credencial, nombre, apaterno, amaterno, faltas, observaciones_asist, ultima_falta, id_titular, ultima_visita, lista, espera from beneficiarios WHERE credencial='"+result.getContents()+"' AND parentesco='TITULAR'",null);
                        cursor2.moveToFirst();

                        popupView.setBackgroundResource(R.color.green);
                        txtCredencial.setText(cursor2.getString(0));
                        txtNombre.setText(cursor2.getString(1) + " " + cursor2.getString(2) + " " + cursor2.getString(3));
                        txtFaltas.setText("Faltas: " + cursor2.getString(4));
                        txtObservaciones_asist.setText("Observaciones: " + cursor2.getString(5));
                        txtUltima_falta.setText("Ultima falta: " + cursor2.getString(6));
                        txtDay.setText("Correcto");
                        imgView.setBackgroundResource(R.drawable.ok);

                        dialogBuilder.setView(popupView);
                        dialog = dialogBuilder.create();
                        dialog.show();

                        //Insertar asistencia a SQLite
                        ContentValues cv = new ContentValues();
                        cv.put("fk_titular",cursor2.getString(7));
                        cv.put("credencial",cursor2.getString(0));
                        cv.put("ultima_visita",fecha);
                        cv.put("nombre",cursor2.getString(1) + " " + cursor2.getString(2) + " " + cursor2.getString(3));
                        cv.put("usuario",id_usuario);
                        cv.put("sincronizar",3);
                        cv.put("lista",cursor2.getString(9));
                        db.replace("asistencia",null,cv);

                        //Modificar beneficiario en SQLite

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = df.format(calendar.getTime());

                        ContentValues cv2 = new ContentValues();
                        cv2.put("ultima_visita",""+formattedDate);
                        cv2.put("sincronizar",3);
                        String[] args = new String[]{""+cursor2.getString(7)};
                        db.update("beneficiarios",cv2,"id_titular=?",args);

                        //Cierre de conexión a base de datos
                        db.close();

                        btnACEPTAR.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                    }else{
                        // verificar si ya paso en los 8 dias posteriores a la descarga de lista

                        // Definir el formato de la fecha de entrada
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        // Parsear la cadena de fecha a un objeto Date
                        Date date = null;
                        try {
                            date = inputFormat.parse(ultima_visita);
                        } catch (ParseException e) {
                            Log.i("","no se puede formatear");
                        }
                        // Crear una instancia de Calendar y establecer la fecha parseada
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        // Obtener el día del año
                        int dia_ultima_visita = calendar.get(Calendar.DAY_OF_YEAR);
                        // Imprimir el día del año
                        //Log.i("","cambios"+dia_ultima_visita);

                        //num de lista
                        //Log.i("","cambios"+exist.getString(17));
                        SharedPreferences preferences = getSharedPreferences("var", Context.MODE_PRIVATE);
                        int dia = preferences.getInt("diaDescargaLista"+exist.getString(17), 0);
                        //dia de descarga de lista
                        //Log.i("","cambios Share:"+dia);

                        if(dia_ultima_visita >= dia && dia_ultima_visita <= (dia+8)){
                            //ya ha pasado por su despensa esta semana
                            popupView.setBackgroundResource(R.color.yellow);
                            txtDay.setText("El apoyo ya ha sido ENTREGADO esta semana");
                            imgView.setBackgroundResource(R.drawable.alert);
                            txtDay.setTextSize(20);

                            dialogBuilder.setView(popupView);
                            dialog = dialogBuilder.create();
                            dialog.show();

                            btnACEPTAR.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                        }else{
                            // correcto
                            //Log.i("","cambios2:"+ultima_visita);

                            Cursor cursor2 = db.rawQuery("SELECT credencial, nombre, apaterno, amaterno, faltas, observaciones_asist, ultima_falta, id_titular, ultima_visita, lista, espera from beneficiarios WHERE credencial='"+result.getContents()+"' AND parentesco='TITULAR'",null);
                            cursor2.moveToFirst();

                            popupView.setBackgroundResource(R.color.green);
                            txtCredencial.setText(cursor2.getString(0));
                            txtNombre.setText(cursor2.getString(1) + " " + cursor2.getString(2) + " " + cursor2.getString(3));
                            txtFaltas.setText("Faltas: " + cursor2.getString(4));
                            txtObservaciones_asist.setText("Observaciones: " + cursor2.getString(5));
                            txtUltima_falta.setText("Ultima falta: " + cursor2.getString(6));
                            txtDay.setText("Correcto");
                            imgView.setBackgroundResource(R.drawable.ok);

                            dialogBuilder.setView(popupView);
                            dialog = dialogBuilder.create();
                            dialog.show();

                            //Insertar asistencia a SQLite
                            ContentValues cv = new ContentValues();
                            cv.put("fk_titular",cursor2.getString(7));
                            cv.put("credencial",cursor2.getString(0));
                            cv.put("ultima_visita",fecha);
                            cv.put("nombre",cursor2.getString(1) + " " + cursor2.getString(2) + " " + cursor2.getString(3));
                            cv.put("usuario",id_usuario);
                            cv.put("sincronizar",3);
                            cv.put("lista",cursor2.getString(9));
                            db.replace("asistencia",null,cv);

                            //Modificar beneficiario en SQLite

                            Calendar calendar3 = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            String formattedDate = df.format(calendar3.getTime());

                            ContentValues cv2 = new ContentValues();
                            cv2.put("ultima_visita",""+formattedDate);
                            cv2.put("sincronizar",3);
                            String[] args = new String[]{""+cursor2.getString(7)};
                            db.update("beneficiarios",cv2,"id_titular=?",args);

                            //Cierre de conexión a base de datos
                            db.close();

                            btnACEPTAR.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                        }
                    }

                    /////////// before changes
                    /*
                    Cursor asistencia = db.rawQuery("SELECT * from asistencia WHERE credencial='"+result.getContents()+"' AND ultima_visita='"+fecha+"'",null);
                    if(asistencia.moveToFirst()){
                        popupView.setBackgroundResource(R.color.yellow);
                        txtDay.setText("Apoyo entregado el día de hoy");
                        imgView.setBackgroundResource(R.drawable.alert);
                        txtDay.setTextSize(20);

                        dialogBuilder.setView(popupView);
                        dialog = dialogBuilder.create();
                        dialog.show();

                        btnACEPTAR.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                    }else{

                        Cursor cursor2 = db.rawQuery("SELECT credencial, nombre, apaterno, amaterno, faltas, observaciones_asist, ultima_falta, id_titular, ultima_visita, lista, espera from beneficiarios WHERE credencial='"+result.getContents()+"' AND parentesco='TITULAR'",null);
                        cursor2.moveToFirst();

                        //Ult fecha pase de lista
                        SharedPreferences preferences = getSharedPreferences("cred", Context.MODE_PRIVATE);
                        String listaa = "ultfechpase"+cursor2.getString(9);
                        String ultfec = preferences.getString(listaa, "0");

                        //Ultima fecha beneficiario != ultima fecha pase de lista
                        Log.i("","ult_visita: "+cursor2.getString(8)+" fecha_ult_pase: "+ultfec);

                        //Convertir a dia del año
                        SimpleDateFormat formatoo = new SimpleDateFormat("yyyy-MM-dd");
                        Date fechaDate = null;
                        int day_year2=0;
                        try {
                            fechaDate = formatoo.parse(cursor2.getString(8));
                            Calendar cale = Calendar.getInstance();
                            cale.setTime(fechaDate);
                            Log.i("","KKKKKKKKKKKKKKK "+cale.get(Calendar.DAY_OF_YEAR));
                            day_year2 = cale.get(Calendar.DAY_OF_YEAR);
                        }
                        catch (ParseException ex)
                        {
                            System.out.println(ex);
                        }

                        Log.i("UUUUUUUUUUUUUU",""+day_year2+" = "+Integer.parseInt(ultfec));

                        //if(!cursor2.getString(8).equals(ultfec)){
                        //if(day_year2 == Integer.parseInt(ultfec) || (day_year2-Integer.parseInt(ultfec))>0 && (day_year2-Integer.parseInt(ultfec))<3){
                        if(day_year2 == Integer.parseInt(ultfec) ||  Integer.parseInt(ultfec)<day_year2){

                            //AlertDialog
                            popupView.setBackgroundResource(R.color.green);
                            txtCredencial.setText(cursor2.getString(0));
                            txtNombre.setText(cursor2.getString(1) + " " + cursor2.getString(2) + " " + cursor2.getString(3));
                            txtFaltas.setText("Faltas: " + cursor2.getString(4));
                            txtObservaciones_asist.setText("Observaciones: " + cursor2.getString(5));
                            txtUltima_falta.setText("Ultima falta: " + cursor2.getString(6));
                            txtDay.setText("Correcto");
                            imgView.setBackgroundResource(R.drawable.ok);

                            dialogBuilder.setView(popupView);
                            dialog = dialogBuilder.create();
                            dialog.show();

                            //Insertar asistencia a SQLite
                            ContentValues cv = new ContentValues();
                            cv.put("fk_titular",cursor2.getString(7));
                            cv.put("credencial",cursor2.getString(0));
                            cv.put("ultima_visita",fecha);
                            cv.put("nombre",cursor2.getString(1) + " " + cursor2.getString(2) + " " + cursor2.getString(3));
                            cv.put("usuario",id_usuario);
                            cv.put("sincronizar",3);
                            cv.put("lista",cursor2.getString(9));
                            db.replace("asistencia",null,cv);

                            //Modificar beneficiario en SQLite

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            String formattedDate = df.format(calendar.getTime());

                            ContentValues cv2 = new ContentValues();
                            cv2.put("ultima_visita",""+formattedDate);
                            cv2.put("sincronizar",3);
                            String[] args = new String[]{""+cursor2.getString(7)};
                            db.update("beneficiarios",cv2,"id_titular=?",args);

                            //Cierre de conexión a base de datos
                            db.close();

                            btnACEPTAR.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                        }else{

                            Cursor cursor3 = db.rawQuery("SELECT espera from beneficiarios WHERE credencial='"+result.getContents()+"' AND parentesco='TITULAR'",null);
                            cursor3.moveToFirst();

                            if(cursor3.getInt(0) == 0) {

                                //AlertDialog
                                popupView.setBackgroundResource(R.color.yellow);
                                txtCredencial.setText(cursor2.getString(0));
                                txtNombre.setText(cursor2.getString(1) + " " + cursor2.getString(2) + " " + cursor2.getString(3));
                                txtFaltas.setText("Faltas: " + cursor2.getString(4));
                                txtObservaciones_asist.setText("Observaciones: " + cursor2.getString(5));
                                txtUltima_falta.setText("Ultima falta: " + cursor2.getString(6));
                                txtDay.setText("Beneficiario No asistió a la ultima entrega");
                                imgView.setBackgroundResource(R.drawable.alert);
                                txtDay.setTextSize(24);

                                //Modificar beneficiario en SQLite
                                ContentValues cv3 = new ContentValues();
                                cv3.put("espera",1);
                                String[] args2 = new String[]{""+cursor2.getString(7)};
                                db.update("beneficiarios",cv3,"id_titular=?",args2);

                                dialogBuilder.setView(popupView);
                                dialog = dialogBuilder.create();
                                dialog.show();

                                btnACEPTAR.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();

                                    }
                                });

                            }else{
                                //AlertDialog
                                popupView.setBackgroundResource(R.color.green);
                                txtCredencial.setText(cursor2.getString(0));
                                txtNombre.setText(cursor2.getString(1) + " " + cursor2.getString(2) + " " + cursor2.getString(3));
                                txtFaltas.setText("Faltas: " + cursor2.getString(4));
                                txtObservaciones_asist.setText("Observaciones: " + cursor2.getString(5));
                                txtUltima_falta.setText("Ultima falta: " + cursor2.getString(6));
                                txtDay.setText("Correcto");
                                imgView.setBackgroundResource(R.drawable.ok);

                                dialogBuilder.setView(popupView);
                                dialog = dialogBuilder.create();
                                dialog.show();

                                //Insertar asistencia a SQLite
                                ContentValues cv = new ContentValues();
                                cv.put("fk_titular",cursor2.getString(7));
                                cv.put("credencial",cursor2.getString(0));
                                cv.put("ultima_visita",fecha);
                                cv.put("nombre",cursor2.getString(1) + " " + cursor2.getString(2) + " " + cursor2.getString(3));
                                cv.put("usuario",id_usuario);
                                cv.put("sincronizar",3);
                                cv.put("lista",cursor2.getString(9));
                                db.replace("asistencia",null,cv);

                                //Modificar beneficiario en SQLite

                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                String formattedDate = df.format(calendar.getTime());

                                ContentValues cv2 = new ContentValues();
                                cv2.put("ultima_visita",""+formattedDate);
                                cv2.put("sincronizar",3);
                                String[] args = new String[]{""+cursor2.getString(7)};
                                db.update("beneficiarios",cv2,"id_titular=?",args);

                                ContentValues cv4 = new ContentValues();
                                cv4.put("espera",0);
                                String[] args3 = new String[]{""+cursor2.getString(7)};
                                db.update("beneficiarios",cv4,"id_titular=?",args3);

                                //Cierre de conexión a base de datos
                                db.close();

                                btnACEPTAR.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        finish();
                                        startActivity(getIntent());
                                    }
                                });
                            }
                        }
                    }*/

                    // bene baja o bene no existe
                }else if(exist2.moveToFirst()){
                        popupView.setBackgroundResource(R.color.yellow);
                        txtCredencial.setText(exist2.getString(1));
                        txtNombre.setText(exist2.getString(14) + " " + exist2.getString(15) + " " + exist2.getString(16));
                        txtFaltas.setText("Faltas: " + exist2.getString(6));
                        txtObservaciones_asist.setText("Observaciones: " + exist2.getString(11));
                        txtUltima_falta.setText("Ultima falta: " + exist2.getString(8));
                        txtDay.setText("Beneficiario dado de BAJA");
                        imgView.setBackgroundResource(R.drawable.alert);
                        txtDay.setTextSize(24);

                    dialogBuilder.setView(popupView);
                    dialog = dialogBuilder.create();
                    dialog.show();

                    btnACEPTAR.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());
                        }
                    });
                }else{
                    popupView.setBackgroundResource(R.color.red);
                    txtDay.setText("Beneficiario no existe");
                    txtDay.setTextSize(24);
                    imgView.setBackgroundResource(R.drawable.error);

                    dialogBuilder.setView(popupView);
                    dialog = dialogBuilder.create();
                    dialog.show();

                    btnACEPTAR.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());
                        }
                    });
                }
                //Cierre de conexión a base de datos
                db.close();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
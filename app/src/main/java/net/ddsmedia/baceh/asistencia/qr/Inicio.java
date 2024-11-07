package net.ddsmedia.baceh.asistencia.qr;

import static net.ddsmedia.baceh.BuildConfig.VERSION_NAME;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.ddsmedia.baceh.R;
import net.ddsmedia.baceh.asistencia.qr.api.Globals;
import net.ddsmedia.baceh.asistencia.qr.api.ServiceApi;
import net.ddsmedia.baceh.asistencia.qr.db.MyOpenHelper;
import net.ddsmedia.baceh.asistencia.qr.entidad.Actualizar;
import net.ddsmedia.baceh.asistencia.qr.entidad.Asistenciados;
import net.ddsmedia.baceh.asistencia.qr.entidad.Faltas;
import net.ddsmedia.baceh.asistencia.qr.entidad.Listas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Inicio extends AppCompatActivity {

    ServiceApi serviceApi;
    ProgressDialog progressDialog;
    private static boolean variableBooleana = true;
    private TextView txtUSERNAME, txtDESCMUN, txtDESC, txtA, txtAlertSinc;
    private ImageView imgAlertSinc;
    private Button btnLISTA, btnADM, btnMUN;
    public static String id_usuario;
    Boolean bandera = true, bandera2=true, bandera3=true;
    int contt, contt2, contFail, contFail2;

    //AlertDialog
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button btnACEPTAR_ALERT_SINC;
    MenuItem sincronizadoForzosoItem;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        this.setTitle("BAMX Hidalgo v"+VERSION_NAME);

        txtUSERNAME=findViewById(R.id.txtUSERNAME);
        txtDESCMUN=findViewById(R.id.txtDESCMUN);
        txtDESC=findViewById(R.id.txtDESC);
        txtA=findViewById(R.id.txtA);
        btnLISTA=findViewById(R.id.btnLISTA);
        btnADM=findViewById(R.id.btnADM);
        btnMUN=findViewById(R.id.btnMUN);

        btnLISTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paseLista();
            }
        });
        btnADM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                administrarRegistro();
            }
        });
        btnMUN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                descargarLista();
            }
        });

        cargaListas();
        username();
        estadistica();

        // borrar asistencia despues de 15 dias con shared preferences de fecha descarga de lista
        SharedPreferences preferences2 = getSharedPreferences("var", Context.MODE_PRIVATE);
        MyOpenHelper conn = new MyOpenHelper(getApplicationContext(), MyOpenHelper.DATABASE_NOMBRE, null, MyOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = conn.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT lista from asistencia order by lista", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String listaValue = cursor.getString(0);
                Log.d("SQLite", "Lista Value: " + listaValue);

                int diaList = preferences2.getInt("diaDescargaLista"+listaValue, 0);
                Log.d("SQLite", "Lista Value: " + diaList);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("D");
                String formattedDate = df.format(calendar.getTime());
                int hoy = Integer.parseInt(formattedDate);
                Log.d("SQLite", "Lista Value: " + hoy);

                //////////////////// cambiar a 15 o 16 dias
                if(hoy > (diaList+15)){
                    db.execSQL("DELETE FROM asistencia WHERE lista = '" + listaValue + "'");
                }

            } while (cursor.moveToNext());
        }

        /*if(cursor.moveToFirst()) {

            String ultimaVisita = cursor.getString(4);
            Log.i("compar",ultimaVisita);
            Calendar visitaCalendar = Calendar.getInstance();
            try {
                visitaCalendar.setTime(Objects.requireNonNull(dateFormat.parse(ultimaVisita)));
            } catch (ParseException e) {
                //throw new RuntimeException(e);
            }

            //////////////////// cambiar a 15 o 16 dias
            visitaCalendar.add(Calendar.DAY_OF_MONTH, 1);
            String quinceDiasDespues = dateFormat.format(visitaCalendar.getTime());
            Log.i("compar",quinceDiasDespues);


            if(today.compareTo(quinceDiasDespues) > 0) {
                Log.i("compar",""+today.compareTo(quinceDiasDespues));
                db.execSQL("DELETE FROM asistencia WHERE ultima_visita = '" + ultimaVisita + "'");
            }
        }*/




        // borrar asistencia despues de 15 dias
        /*
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(calendar.getTime());

        MyOpenHelper conn = new MyOpenHelper(getApplicationContext(), MyOpenHelper.DATABASE_NOMBRE, null, MyOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = conn.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from asistencia order by id", null);

        if(cursor.moveToFirst()) {

            String ultimaVisita = cursor.getString(4);
            Log.i("compar",ultimaVisita);
            Calendar visitaCalendar = Calendar.getInstance();
            try {
                visitaCalendar.setTime(Objects.requireNonNull(dateFormat.parse(ultimaVisita)));
            } catch (ParseException e) {
                //throw new RuntimeException(e);
            }

            //////////////////// cambiar a 15 o 16 dias
            visitaCalendar.add(Calendar.DAY_OF_MONTH, 1);
            String quinceDiasDespues = dateFormat.format(visitaCalendar.getTime());
            Log.i("compar",quinceDiasDespues);


            if(today.compareTo(quinceDiasDespues) > 0) {
                Log.i("compar",""+today.compareTo(quinceDiasDespues));
                db.execSQL("DELETE FROM asistencia WHERE ultima_visita = '" + ultimaVisita + "'");
            }
        }
        //cursor.close();
        //db.close();
        */




        // borrar asistencia al dia siguiente
        /*
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDateDay = day.format(calendar.getTime());

            //Log.i("", "" +formattedDateDay);

            MyOpenHelper conn = new MyOpenHelper(getApplicationContext(), MyOpenHelper.DATABASE_NOMBRE, null, MyOpenHelper.DATABASE_VERSION);
            SQLiteDatabase db = conn.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * from asistencia where ultima_visita != '"+formattedDateDay+"'",null);
            if(cursor.moveToFirst()) {
                db.execSQL("Delete from asistencia where ultima_visita != '" + formattedDateDay + "'");
            }
         */
    }

    protected void onPause() {
        super.onPause();
        variableBooleana = false;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder mensaje=new AlertDialog.Builder(this);
        mensaje.setTitle("¿Deseas Salir de la Aplicación?");
        mensaje.setCancelable(false);
        mensaje.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    //MENU ...
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        sincronizadoForzosoItem = menu.findItem(R.id.sincronizadoForzoso);

        SharedPreferences preferences2 = getSharedPreferences("cred", Context.MODE_PRIVATE);
        String superadmin = preferences2.getString("log", "");

        //Log.i("loggggg","es: "+superadmin);

        if(superadmin.equals("daniel")){
            sincronizadoForzosoItem.setVisible(true);
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        //AlertDialog
        dialogBuilder = new AlertDialog.Builder(this)
                .setCancelable(false);
        final View popupView = getLayoutInflater().inflate(R.layout.popup_sinc, null);
        txtAlertSinc= popupView.findViewById(R.id.txtAlertSinc);
        imgAlertSinc=popupView.findViewById(R.id.imgAlertSinc);
        btnACEPTAR_ALERT_SINC=popupView.findViewById(R.id.btnACEPTAR_ALERT_SINC);

        switch(item.getItemId()){

            case R.id.menuDescargar:
                // if(isNetworkAvailable(Inicio.this)) {
                    if (checkConectivity()) {
                        descargarLista();
                    } else {
                        //Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();

                        //Alert Dialog con error en la sincronización
                        popupView.setBackgroundResource(R.color.yellow);
                        txtAlertSinc.setText("No hay conexión a internet");
                        imgAlertSinc.setBackgroundResource(R.drawable.alert);
                        txtAlertSinc.setTextSize(20);

                        dialogBuilder.setView(popupView);
                        dialog = dialogBuilder.create();
                        dialog.show();

                        btnACEPTAR_ALERT_SINC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                    }
                    /*
                }else{
                    Log.i("","No esta conectado a una red WIFI");
                    //Toast.makeText(this, "No esta conectado a una red WIFI", Toast.LENGTH_SHORT).show();

                    //Alert Dialog con error en la sincronización
                    popupView.setBackgroundResource(R.color.yellow);
                    txtAlertSinc.setText("No esta conectado a una red WIFI");
                    imgAlertSinc.setBackgroundResource(R.drawable.alert);
                    txtAlertSinc.setTextSize(20);

                    dialogBuilder.setView(popupView);
                    dialog = dialogBuilder.create();
                    dialog.show();

                    btnACEPTAR_ALERT_SINC.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());
                        }
                    });
                }
                     */
                return true;

            case R.id.sincronizadoForzoso:
                // if(isNetworkAvailable(Inicio.this)) {
                    if (checkConectivity()) {

                        AlertDialog.Builder mensaje = new AlertDialog.Builder(this);
                        mensaje.setTitle("¿Deseas forzar la sincronización?");
                        mensaje.setCancelable(false);
                        mensaje.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                progressDialog = new ProgressDialog(Inicio.this);
                                progressDialog.setMessage("Sincronizando..."); // Mensaje
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                                progressDialog.show(); // Display Progress Dialog
                                progressDialog.setCancelable(false);

                                new Thread(new Runnable() {
                                    View vista;


                                    @Override
                                    public void run() {
                                        while (bandera) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (Exception e) {
                                            }
                                        }
                                        progressDialog.dismiss();
                                    }
                                }).start();

                                sincronizarForzado();

                            }
                        });
                        mensaje.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        mensaje.show();

                    } else {
                        //Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();

                        //Alert Dialog con error en la sincronización
                        popupView.setBackgroundResource(R.color.yellow);
                        txtAlertSinc.setText("No hay conexión a internet");
                        imgAlertSinc.setBackgroundResource(R.drawable.alert);
                        txtAlertSinc.setTextSize(20);

                        dialogBuilder.setView(popupView);
                        dialog = dialogBuilder.create();
                        dialog.show();

                        btnACEPTAR_ALERT_SINC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                    }
                    /*
                }else{
                    Log.i("","No esta conectado a una red WIFI");
                    //Toast.makeText(this, "No esta conectado a una red WIFI", Toast.LENGTH_SHORT).show();

                    //Alert Dialog con error en la sincronización
                    popupView.setBackgroundResource(R.color.yellow);
                    txtAlertSinc.setText("No esta conectado a una red WIFI");
                    imgAlertSinc.setBackgroundResource(R.drawable.alert);
                    txtAlertSinc.setTextSize(20);

                    dialogBuilder.setView(popupView);
                    dialog = dialogBuilder.create();
                    dialog.show();

                    btnACEPTAR_ALERT_SINC.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());
                        }
                    });

                }
                     */
                return true;


            case R.id.sincronizar:
                // if(isNetworkAvailable(Inicio.this)) {
                    if (checkConectivity()) {

                        AlertDialog.Builder mensaje = new AlertDialog.Builder(this);
                        mensaje.setTitle("¿Deseas realizar la sincronización?");
                        mensaje.setCancelable(false);
                        mensaje.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                progressDialog = new ProgressDialog(Inicio.this);
                                progressDialog.setMessage("Sincronizando..."); // Mensaje
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                                progressDialog.show(); // Display Progress Dialog
                                progressDialog.setCancelable(false);

                                new Thread(new Runnable() {
                                    View vista;


                                    @Override
                                    public void run() {
                                        while (bandera) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (Exception e) {
                                            }
                                        }
                                        progressDialog.dismiss();
                                    }
                                }).start();

                                sincronizarFaltas();


                            }
                        });
                        mensaje.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        mensaje.show();

                    } else {
                        //Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();

                        //Alert Dialog con error en la sincronización
                        popupView.setBackgroundResource(R.color.yellow);
                        txtAlertSinc.setText("No hay conexión a internet");
                        imgAlertSinc.setBackgroundResource(R.drawable.alert);
                        txtAlertSinc.setTextSize(20);

                        dialogBuilder.setView(popupView);
                        dialog = dialogBuilder.create();
                        dialog.show();

                        btnACEPTAR_ALERT_SINC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                    }
                    /*
                }else{
                    Log.i("","No esta conectado a una red WIFI");
                    //Toast.makeText(this, "No esta conectado a una red WIFI", Toast.LENGTH_SHORT).show();

                    //Alert Dialog con error en la sincronización
                    popupView.setBackgroundResource(R.color.yellow);
                    txtAlertSinc.setText("No esta conectado a una red WIFI");
                    imgAlertSinc.setBackgroundResource(R.drawable.alert);
                    txtAlertSinc.setTextSize(20);

                    dialogBuilder.setView(popupView);
                    dialog = dialogBuilder.create();
                    dialog.show();

                    btnACEPTAR_ALERT_SINC.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());
                        }
                    });

                }
                     */
                return true;

            case R.id.logout:

                AlertDialog.Builder mensaje=new AlertDialog.Builder(this);
                mensaje.setTitle("¿Deseas cerrar sesión?");
                mensaje.setCancelable(false);
                mensaje.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getSharedPreferences("cred", Context.MODE_PRIVATE);
                        preferences.edit().clear().commit();
                        Toast.makeText(Inicio.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Inicio.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                mensaje.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                mensaje.show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Usuario logueado
    private void username(){

        //SharedPreferences validación
        SharedPreferences preferences = getSharedPreferences("cred", Context.MODE_PRIVATE);
        String user = preferences.getString("log", "");
        txtUSERNAME.setText("Usuario: "+user);

    }

    //DIRECCIONAR A PASE DE LISTA
    private void paseLista(){
        Intent intent = new Intent(this, Scan.class);
        intent.putExtra("id_usuario",id_usuario);
        startActivity(intent);
    }

    //DIRECCIONAR A ADMINISTRAR
    private void administrarRegistro(){
        Intent intent = new Intent(this, Administrar.class);
        startActivity(intent);
    }

    //DIRECCIONAR A MUNICIPIOS
    private void descargarLista(){
        Intent intent = new Intent(this, DescargaMunicipios.class);
        startActivity(intent);
    }

    //DESCARGA DE LISTAS A SQLite
    private void cargaListas() {
        // if (isNetworkAvailable(Inicio.this)) {
            if (checkConectivity()) {
                if (variableBooleana) {

                    ////listaMunicipios() GET
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Globals.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    serviceApi = retrofit.create(ServiceApi.class);
                    Call<List<Listas>> call = serviceApi.listas();
                    call.enqueue((new Callback<List<Listas>>() {
                        @Override
                        public void onResponse(Call<List<Listas>> call, Response<List<Listas>> response) {
                            List<Listas> postslist = response.body();
                            for (Listas x : postslist) {
                                MyOpenHelper conn = new MyOpenHelper(getApplicationContext(), MyOpenHelper.DATABASE_NOMBRE, null, MyOpenHelper.DATABASE_VERSION);
                                SQLiteDatabase db = conn.getReadableDatabase();
                                if (db != null) {
                                    //Insertar listas a SQLite
                                    ContentValues cv = new ContentValues();
                                    cv.put("lista", x.getLista());
                                    db.replace("listas", null, cv);
                                    Log.i("OK CARGA MUNICIPIOS", "");
                                    //Toast.makeText(Inicio.this, "OK", Toast.LENGTH_SHORT).show();
                                }
                                db.close();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Listas>> call, Throwable t) {
                            Log.i("ERROR CARGA MUNICIPIOS", "");
                            //Toast.makeText(Inicio.this, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }));

                }
            }
            /*
        }else{
            Log.i("","No esta conectado a una red WIFI");
        }
             */
    }

    private void estadistica(){
        List<String> datos = new ArrayList<String>();
        MyOpenHelper conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor aux = db.rawQuery("SELECT DISTINCT lista from beneficiarios WHERE estatus=1",null);
        Cursor aux2 = db.rawQuery("SELECT * from beneficiarios where estatus=1",null);
        if(aux2.moveToFirst()) {
        }else{
            btnLISTA.setEnabled(false);
            btnLISTA.setBackgroundColor(Color.parseColor("#ffffff"));
            btnLISTA.setTextColor(Color.WHITE);
            btnADM.setEnabled(false);
            btnADM.setBackgroundColor(Color.parseColor("#ffffff"));
            btnADM.setTextColor(Color.WHITE);
            txtDESC.setTextColor(Color.WHITE);
            txtA.setTextColor(Color.BLACK);
        }
        while(aux.moveToNext()){
            Cursor cursor = db.rawQuery("SELECT lista from listas WHERE lista=" + aux.getString(0), null);
            cursor.moveToFirst();
            Cursor cursor2 = db.rawQuery("SELECT * from beneficiarios WHERE estatus=1 and lista="+aux.getString(0), null);
            cursor2.moveToFirst();
            //Cursor cursor3 = db.rawQuery("SELECT * from beneficiarios WHERE lista="+aux.getString(0), null);
            //cursor3.moveToFirst();
            btnMUN.setEnabled(false);
            btnMUN.setBackgroundColor(Color.parseColor("#ffffff"));
            btnMUN.setTextColor(Color.WHITE);
            txtDESC.setTextColor(Color.BLACK);
            txtA.setTextColor(Color.WHITE);
            //datos.add("Lista "+cursor.getString(0) + " con "+cursor3.getCount()+" beneficiarios y "+cursor2.getCount()+" activos \n");
            datos.add("Lista "+cursor.getString(0) + " con "+cursor2.getCount()+" activos \n");

        }
            txtDESCMUN.setText(""+datos.toString().replace("[", "").replace("]", "").replace(",", ""));
            //Habilitar scroll Textview
            txtDESCMUN.setMovementMethod(new ScrollingMovementMethod());
    }

    //SINCRONIZAR BENEFICIARIOS CON SERVIDOR

    private void sincronizar(){

        //AlertDialog
        dialogBuilder = new AlertDialog.Builder(this)
                .setCancelable(false);
        final View popupView = getLayoutInflater().inflate(R.layout.popup_sinc, null);
        txtAlertSinc= popupView.findViewById(R.id.txtAlertSinc);
        imgAlertSinc=popupView.findViewById(R.id.imgAlertSinc);
        btnACEPTAR_ALERT_SINC=popupView.findViewById(R.id.btnACEPTAR_ALERT_SINC);

        ServiceApi serviceApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceApi = retrofit.create(ServiceApi.class);

        MyOpenHelper conn;
        conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from beneficiarios WHERE sincronizar=3",null);
        //Log.i("CONT",""+cursor.getCount());
        if(cursor.getCount()>0) {
            contt=0;
            contFail=0;
            while (cursor.moveToNext()) {
                if (bandera2) {
                    ////actualizarBeneficiarios() POST LISTA
                    Call<Actualizar> call3 = serviceApi.actualizarBeneficiario(cursor.getString(0), cursor.getString(7), cursor.getString(11));
                    call3.enqueue(new Callback<Actualizar>() {
                        @Override
                        public void onResponse(Call<Actualizar> call, Response<Actualizar> response) {
                            contt++;
                            //Log.i("CONTTTT",""+contt);
                            if (contt == cursor.getCount()) {
                                bandera = false;
                                db.execSQL("Update beneficiarios set sincronizar='1'");
                                Log.i("OK SINCRONIZAR1","000");

                                //Toast.makeText(Inicio.this, "Sincronizado", Toast.LENGTH_SHORT).show();

                                //Alert Dialog con exito en la sincronización
                                popupView.setBackgroundResource(R.color.green);
                                txtAlertSinc.setText("Sincronizado");
                                imgAlertSinc.setBackgroundResource(R.drawable.ok);
                                txtAlertSinc.setTextSize(20);

                                dialogBuilder.setView(popupView);
                                dialog = dialogBuilder.create();
                                dialog.show();

                                btnACEPTAR_ALERT_SINC.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        finish();
                                        startActivity(getIntent());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<Actualizar> call, Throwable t) {
                            contFail++;
                            bandera = false;
                            bandera2 = false;
                            Log.i("FAIL SINCRONIZAR1", ""+contFail);
                            if(contt+contFail == cursor.getCount()){
                                //Toast.makeText(Inicio.this, "Error al intentar sincronizar, intentarlo más tarde", Toast.LENGTH_SHORT).show();

                                //Alert Dialog con error en la sincronización
                                popupView.setBackgroundResource(R.color.yellow);
                                txtAlertSinc.setText("Error al sincronizar, verificar conexión a Internet");
                                imgAlertSinc.setBackgroundResource(R.drawable.alert);
                                txtAlertSinc.setTextSize(20);

                                dialogBuilder.setView(popupView);
                                dialog = dialogBuilder.create();
                                dialog.show();

                                btnACEPTAR_ALERT_SINC.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        finish();
                                        startActivity(getIntent());
                                    }
                                });
                            }

                        }
                    });
                    /////
                }
            }

        }else{
            bandera=false;
            Log.i("TODOS BENEFICIARIOS SINCRONIZADOS","000");
            //Toast.makeText(Inicio.this, "Todo_está Sincronizado", Toast.LENGTH_SHORT).show();

            //Alert Dialog con todo_esta sincronizado
            popupView.setBackgroundResource(R.color.green);
            txtAlertSinc.setText("Todo está Sincronizado");
            imgAlertSinc.setBackgroundResource(R.drawable.ok);
            txtAlertSinc.setTextSize(20);

            dialogBuilder.setView(popupView);
            dialog = dialogBuilder.create();
            dialog.show();

            btnACEPTAR_ALERT_SINC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    finish();
                    startActivity(getIntent());
                }
            });
        }
    }

    //SINCRONIZAR ASISTENCIA CON SERVIDOR
    private void sincronizarAsistencia(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceApi = retrofit.create(ServiceApi.class);

        MyOpenHelper conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from asistencia WHERE sincronizar=3 ORDER BY fecha",null);
        if(cursor.getCount()>0) {
            contt2=0;
            SharedPreferences preferences4 = getSharedPreferences("var", Context.MODE_PRIVATE);
            while (cursor.moveToNext()) {
                if (bandera3) {
                    String lista = cursor.getString(8);
                    String diaList = preferences4.getString("FechaDescargaLista"+lista, "");
                    ////listaAsistencia() POST
                    Call<Asistenciados> call3 = serviceApi.listaAsistencia(cursor.getString(1), cursor.getString(3), cursor.getString(6), diaList);
                    call3.enqueue(new Callback<Asistenciados>() {
                        @Override
                        public void onResponse(Call<Asistenciados> call, Response<Asistenciados> response) {
                            contt2++;
                            if (contt2 == cursor.getCount()) {
                                db.execSQL("Update asistencia set sincronizar=1");
                                Log.i("OK ASISTENCIAS1", "000");
                                sincronizar();
                            }
                        }

                        @Override
                        public void onFailure(Call<Asistenciados> call, Throwable t) {
                            contFail2++;
                            bandera3 = false;
                            if (contFail2+contt2 == cursor.getCount()) {
                                Log.i("FAIL ASISTENCIAS1", "000");
                            }
                        }
                    });
                    /////

                }
            }
        }else{
            bandera=false;
            Log.i("TODAS ASISTENCIAS SINCRONIZADAS","000");
        }
    }

    //SINCRONIZAR FALTAS CON SERVIDOR
    private void sincronizarFaltas(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceApi = retrofit.create(ServiceApi.class);

        MyOpenHelper conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from beneficiarios WHERE estatus = 1 and sincronizar = 1",null);
        if(cursor.getCount()>0) {
            contt2=0;
            SharedPreferences preferences3 = getSharedPreferences("var", Context.MODE_PRIVATE);
            while (cursor.moveToNext()) {
                if (bandera3) {
                    String lista = cursor.getString(17);
                    String diaList = preferences3.getString("FechaDescargaLista"+lista, "");

                    Log.i("SQLITE"," | "+cursor.getString(0)+" | "+cursor.getString(6)+" | "+diaList);

                    // evitar parsear fechas con 0000-00-00
                    if(!cursor.getString(7).equals("0000-00-00")){
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                        String descargaLista = diaList;
                        String ultimaVisita = cursor.getString(7);

                        LocalDate parseDescargaLista = LocalDate.parse(descargaLista, formatter);
                        LocalDate parseUltimaVisita = LocalDate.parse(ultimaVisita, formatter);

                        LocalDate limitePase = parseDescargaLista.plusDays(15);

                        if ((parseUltimaVisita.isEqual(parseDescargaLista) || parseUltimaVisita.isAfter(parseDescargaLista)) &&
                                parseUltimaVisita.isBefore(limitePase) || parseUltimaVisita.isEqual(limitePase)) {

                            // La fecha de descarga está dentro del período
                            Log.i("sqlite", "ya fue generada la falta");
                            contt2++;
                            if (contt2 == cursor.getCount()) {
                                //db.execSQL("Update asistencia set sincronizar=1");
                                Log.i("OK FALTAS2.2", "000");
                                sincronizarAsistencia();
                            }

                        } else {

                            // La fecha de descarga NO está dentro del período

                            ////actualizarFaltas() POST
                            Call<Faltas> call3 = serviceApi.actualizarFaltas(cursor.getString(0), cursor.getString(6), diaList);
                            call3.enqueue(new Callback<Faltas>() {
                                @Override
                                public void onResponse(Call<Faltas> call, Response<Faltas> response) {
                                    contt2++;
                                    Log.i("OK FALTAS1", "000");
                                    if (contt2 == cursor.getCount()) {
                                        //db.execSQL("Update asistencia set sincronizar=1");
                                        Log.i("OK FALTAS2", "000");
                                        sincronizarAsistencia();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Faltas> call, Throwable t) {
                                    contFail2++;
                                    bandera3 = false;
                                    if (contFail2+contt2 == cursor.getCount()) {
                                        Log.i("FAIL FALTAS1", "000");
                                    }
                                }
                            });
                            /////
                        }
                    }else{
                        ////actualizarFaltas() POST
                        Call<Faltas> call3 = serviceApi.actualizarFaltas(cursor.getString(0), cursor.getString(6), diaList);
                        call3.enqueue(new Callback<Faltas>() {
                            @Override
                            public void onResponse(Call<Faltas> call, Response<Faltas> response) {
                                contt2++;
                                Log.i("OK FALTAS3", "000");
                                if (contt2 == cursor.getCount()) {
                                    //db.execSQL("Update asistencia set sincronizar=1");
                                    Log.i("OK FALTAS4", "000");
                                    sincronizarAsistencia();
                                }
                            }

                            @Override
                            public void onFailure(Call<Faltas> call, Throwable t) {
                                contFail2++;
                                bandera3 = false;
                                if (contFail2+contt2 == cursor.getCount()) {
                                    Log.i("FAIL FALTAS2", "000");
                                }
                            }
                        });
                        /////
                    }

                }
            }
        }else{
            bandera=false;
            Log.i("TODAS FALTAS SINCRONIZADAS","000");
        }
    }

    private void sincronizarForzado(){

        //AlertDialog
        dialogBuilder = new AlertDialog.Builder(this)
                .setCancelable(false);
        final View popupView = getLayoutInflater().inflate(R.layout.popup_sinc, null);
        txtAlertSinc= popupView.findViewById(R.id.txtAlertSinc);
        imgAlertSinc=popupView.findViewById(R.id.imgAlertSinc);
        btnACEPTAR_ALERT_SINC=popupView.findViewById(R.id.btnACEPTAR_ALERT_SINC);

        ServiceApi serviceApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceApi = retrofit.create(ServiceApi.class);

        MyOpenHelper conn;
        conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from asistencia ORDER BY fecha",null);
        //Log.i("CONT",""+cursor.getCount());
            contt=0;
            contFail=0;
            while (cursor.moveToNext()) {
                if (bandera2) {
                    ////actualizarBeneficiarios() POST LISTA
                    Call<Actualizar> call3 = serviceApi.actualizarBeneficiario(cursor.getString(1), cursor.getString(4), "");
                    call3.enqueue(new Callback<Actualizar>() {
                        @Override
                        public void onResponse(Call<Actualizar> call, Response<Actualizar> response) {
                            contt++;
                            //Log.i("CONTTTT",""+contt);
                            if (contt == cursor.getCount()) {
                                bandera = false;
                                db.execSQL("Update beneficiarios set sincronizar='1'");
                                Log.i("OK SINCRONIZAR FORZADO","000");

                                sincronizarAsistenciaForzado();

                                //Toast.makeText(Inicio.this, "Sincronizado", Toast.LENGTH_SHORT).show();

                                //Alert Dialog con exito en la sincronización
                                popupView.setBackgroundResource(R.color.green);
                                txtAlertSinc.setText("Sincronizado");
                                imgAlertSinc.setBackgroundResource(R.drawable.ok);
                                txtAlertSinc.setTextSize(20);

                                dialogBuilder.setView(popupView);
                                dialog = dialogBuilder.create();
                                dialog.show();

                                btnACEPTAR_ALERT_SINC.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        finish();
                                        startActivity(getIntent());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<Actualizar> call, Throwable t) {
                            contFail++;
                            bandera = false;
                            bandera2 = false;
                            Log.i("FAIL SINCRONIZAR FORZADO", ""+contFail);
                            if(contt+contFail == cursor.getCount()){
                                //Toast.makeText(Inicio.this, "Error al intentar sincronizar, intentarlo más tarde", Toast.LENGTH_SHORT).show();

                                //Alert Dialog con error en la sincronización
                                popupView.setBackgroundResource(R.color.yellow);
                                txtAlertSinc.setText("Error al forzar sincronización, verificar conexión a Internet");
                                imgAlertSinc.setBackgroundResource(R.drawable.alert);
                                txtAlertSinc.setTextSize(20);

                                dialogBuilder.setView(popupView);
                                dialog = dialogBuilder.create();
                                dialog.show();

                                btnACEPTAR_ALERT_SINC.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        finish();
                                        startActivity(getIntent());
                                    }
                                });
                            }

                        }
                    });
                    /////
                }
            }
    }

    private void sincronizarAsistenciaForzado(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceApi = retrofit.create(ServiceApi.class);

        MyOpenHelper conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from asistencia ORDER BY fecha",null);
            contt2=0;
            SharedPreferences preferences4 = getSharedPreferences("var", Context.MODE_PRIVATE);
            while (cursor.moveToNext()) {
                if (bandera3) {
                    String lista = cursor.getString(8);
                    String diaList = preferences4.getString("FechaDescargaLista"+lista, "");
                    ////listaAsistencia() POST
                    Call<Asistenciados> call3 = serviceApi.listaAsistencia(cursor.getString(1), cursor.getString(3), cursor.getString(6), diaList);
                    call3.enqueue(new Callback<Asistenciados>() {
                        @Override
                        public void onResponse(Call<Asistenciados> call, Response<Asistenciados> response) {
                            contt2++;
                            if (contt2 == cursor.getCount()) {
                                db.execSQL("Update asistencia set sincronizar=1");
                                Log.i("OK ASISTENCIAS", "000");
                            }
                        }

                        @Override
                        public void onFailure(Call<Asistenciados> call, Throwable t) {
                            contFail2++;
                            bandera3 = false;
                            if (contFail2+contt2 == cursor.getCount()) {
                                Log.i("FAIL ASISTENCIAS", "000");
                            }
                        }
                    });
                    /////

                }
            }
    }

    /*
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true;
                }
            }
        }
        return false;
    }
     */

    //Verificar conexión a internet
    private boolean checkConectivity(){
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
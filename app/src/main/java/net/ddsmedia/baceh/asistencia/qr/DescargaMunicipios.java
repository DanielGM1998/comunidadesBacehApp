package net.ddsmedia.baceh.asistencia.qr;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.ddsmedia.baceh.R;
import net.ddsmedia.baceh.asistencia.qr.api.Globals;
import net.ddsmedia.baceh.asistencia.qr.api.ServiceApi;
import net.ddsmedia.baceh.asistencia.qr.db.MyOpenHelper;
import net.ddsmedia.baceh.asistencia.qr.entidad.Beneficiarios;
import net.ddsmedia.baceh.asistencia.qr.entidad.FechaUltPase;
import net.ddsmedia.baceh.asistencia.qr.entidad.Listas;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DescargaMunicipios extends AppCompatActivity {

    private ServiceApi serviceApi;
    Button btnDescargarlista;
    Spinner spinMunicipios;
    ArrayList<String> listaInformacion;
    ArrayList<Listas> listas;
    MyOpenHelper conn;
    ProgressDialog progressDialog;
    Integer a=0, pagina=0;
    String consulta, replace="", rep="", id_muni;
    SharedPreferences preferences, preferences2;
    SharedPreferences.Editor editor, editor2;

    //AlertDialog
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button btnACEPTAR_ALERT_SINC;
    private TextView txtAlertSinc;
    private ImageView imgAlertSinc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descargamunicipios);

        this.setTitle("Descarga de beneficiarios");

        conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
        spinMunicipios  = findViewById(R.id.spinMunicipios);
        btnDescargarlista = findViewById(R.id.btnDescargarlista);
        consultarListas();
        ArrayAdapter adaptador = new ArrayAdapter(this, R.layout.spinnermunicipios,listaInformacion);
        spinMunicipios.setAdapter(adaptador);

        //AlertDialog
        dialogBuilder = new AlertDialog.Builder(this)
                .setCancelable(false);
        final View popupView = getLayoutInflater().inflate(R.layout.popup_sinc, null);
        txtAlertSinc= popupView.findViewById(R.id.txtAlertSinc);
        imgAlertSinc=popupView.findViewById(R.id.imgAlertSinc);
        btnACEPTAR_ALERT_SINC=popupView.findViewById(R.id.btnACEPTAR_ALERT_SINC);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        serviceApi = retrofit.create(ServiceApi.class);

        spinMunicipios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                id_muni = listas.get(position).getLista();

                btnDescargarlista.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // if(isNetworkAvailable(DescargaMunicipios.this)) {
                            if (checkConectivity()) {
                                pagina = 0;
                                a = 0;
                                //ProgressDialog
                                progressDialog = new ProgressDialog(DescargaMunicipios.this);
                                progressDialog.setMessage("Esta operación puede tomar varios segundos, Por favor espere."); // Mensaje
                                progressDialog.setTitle("Descargando..."); // Título
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                                progressDialog.show(); // Display Progress Dialog
                                progressDialog.setCancelable(false);

                                MyOpenHelper conn = new MyOpenHelper(getApplicationContext(), MyOpenHelper.DATABASE_NOMBRE, null, MyOpenHelper.DATABASE_VERSION);
                                SQLiteDatabase db3 = conn.getReadableDatabase();
                                Cursor aux3 = db3.rawQuery("SELECT count(*) from beneficiarios WHERE lista=" + id_muni, null);
                                aux3.moveToFirst();

                                if (aux3.getInt(0) >= 1) {
                                    db3.execSQL("Delete from beneficiarios where lista=" + id_muni);
                                    beneficiarios(id_muni);
                                } else {
                                    beneficiarios(id_muni);
                                }
                            } else {
                                Log.i("No hay conexión", "noooooooo");
                                //Toast.makeText(DescargaMunicipios.this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();

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
                            //Toast.makeText(DescargaMunicipios.this, "No esta conectado a una red WIFI", Toast.LENGTH_SHORT).show();

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
        Intent intent = new Intent(this, Inicio.class);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        startActivity(intent);
    }

    private void beneficiarios(String id_mun){

        //Ult fecha pase lista
        int lis = Integer.parseInt(id_mun);
        Call<List<FechaUltPase>> call2 = serviceApi.ultimaFechaPase(lis);
        call2.enqueue(new Callback<List<FechaUltPase>>() {
            @Override
            public void onResponse(Call<List<FechaUltPase>> call2, Response<List<FechaUltPase>> response) {
                List<FechaUltPase> fecha = response.body();
                for (FechaUltPase x : fecha) {
                    String dat = x.getFecha();
                    Log.i("Ok","fecha "+x.getFecha());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
                    Date dates = null;
                    try {
                        dates = dff.parse(dat);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String listaa = "ultfechpase"+id_mun;
                    Log.i("","UltFECHa: "+dat+" == "+dff.format(dates));

                    //Convertir a dia del año
                    int day_year = 0;
                    SimpleDateFormat formatoo = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaDate = null;
                    try {
                        fechaDate = formatoo.parse(dff.format(dates));
                        Calendar cale = Calendar.getInstance();
                        cale.setTime(fechaDate);
                        Log.i("","QQQQQQQQQQQQQQQ "+cale.get(Calendar.DAY_OF_YEAR));
                        day_year=(cale.get(Calendar.DAY_OF_YEAR));
                    }
                    catch (ParseException ex)
                    {
                        System.out.println(ex);
                    }

                    // guardar fecha de descarga de lista
                    preferences2 = getSharedPreferences("var", Context.MODE_PRIVATE);
                    editor2 = preferences2.edit();

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDateDay = day.format(calendar.getTime());

                    SimpleDateFormat df = new SimpleDateFormat("D");
                    String formattedDate = df.format(calendar.getTime());
                    int a = Integer.parseInt(formattedDate);

                    String fechap = "FechaDescargaLista"+id_mun;
                    editor2.putString(fechap, formattedDateDay);
                    editor2.putInt("diaDescargaLista"+id_mun, a);
                    editor2.apply();



                    //Guardar ult fecha pase lista
                    /*
                    preferences = getSharedPreferences("cred", Context.MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.putString(listaa, String.valueOf(day_year));
                    editor.apply();
                    */
                }
                Log.i("OK FECHA ULT PASE","000");
            }
            @Override
            public void onFailure(Call<List<FechaUltPase>> call2, Throwable t) {
                Log.i("FAIL FECHA ULT PASE","000");
            }
        });


        Call<List<Beneficiarios>> call = serviceApi.listaIntegrantes(id_mun,pagina);
        call.enqueue((new Callback<List<Beneficiarios>>() {
            @Override
            public void onResponse(Call<List<Beneficiarios>> call, Response<List<Beneficiarios>> response) {
                replace="";
                List<Beneficiarios> postslist = response.body();
                consulta = "Replace into beneficiarios (id_titular,credencial,fecha_visita,fk_municipio,estatus,fecha_baja,faltas,ultima_visita,ultima_falta,bajas,tipo,observaciones_asist,id_integrante,parentesco,nombre,apaterno,amaterno,lista,sincronizar,espera) values ";
                for (Beneficiarios x : postslist) {

                    String fechabaja = "0000-00-00";
                    String ultimavisita = "0000-00-00";
                    String ultimafalta = "0000-00-00";

                    if(x.getFecha_baja()!=null || x.getFecha_baja() == ""){
                        fechabaja = x.getFecha_baja();
                    }
                    if(x.getUltima_visita()!=null || x.getUltima_visita() == ""){
                        ultimavisita = x.getUltima_visita();
                    }
                    if(x.getUltima_falta()!=null || x.getUltima_falta() == ""){
                        ultimafalta = x.getUltima_falta();
                    }

                    rep = "(" + x.getId_titular() + ",'" + x.getCredencial() + "','" + x.getFecha_visita() + "'," + x.getFk_municipio() + "," + x.getEstatus() + ",'" + fechabaja + "'," + x.getFaltas() + ",'" + ultimavisita + "','" + ultimafalta + "'," + x.getBajas() + "," + x.getTipo() + ",'" + x.getObservaciones_asist() + "'," + x.getId_integrante() + ",'" + x.getParentesco() + "','" + x.getNombre() + "','" + x.getApaterno() + "','" + x.getAmaterno() + "','" + x.getLista() + "',1,0)\n,";
                    replace = replace + rep;
                    a++;

                }
                replace = replace.substring(0, replace.length() - 1);

                //Log.i("SQL: ",""+replace);
                MyOpenHelper myOpenHelper = new MyOpenHelper(DescargaMunicipios.this, MyOpenHelper.DATABASE_NOMBRE, null, MyOpenHelper.DATABASE_VERSION);
                SQLiteDatabase db = myOpenHelper.getWritableDatabase();
                if (db != null) {
                    //Insertar beneficiarios a base SQLite
                    db.execSQL(consulta + replace);
                }
                //Cierre de conexión a base de datos
                db.close();

                //Log.i("log","Pagina: "+pagina+" : "+postslist.size());
                if(postslist.size() == 2000){
                    pagina++;
                    beneficiarios(id_muni);
                }else{
                    //cierre de ProgressDialog
                    progressDialog.dismiss();
                    MyOpenHelper conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
                    SQLiteDatabase db2 = conn.getReadableDatabase();
                    Cursor aux = db2.rawQuery("SELECT count(*) from beneficiarios WHERE lista="+id_muni,null);
                    aux.moveToFirst();
                    Toast.makeText(DescargaMunicipios.this, "Se descargarón " + aux.getString(0) + " beneficiarios", Toast.LENGTH_LONG).show();
                    //Cierre de conexión a base de datos
                    db2.close();
                    Log.i("OK DESCARGA","000");
                }
            }
            @Override
            public void onFailure(Call<List<Beneficiarios>> call, Throwable t) {
                //cierre de ProgressDialog
                progressDialog.dismiss();
                Toast.makeText(DescargaMunicipios.this, "Ocurrio un problema, Intentar de nuevo", Toast.LENGTH_SHORT).show();
                Log.i("FAIL DESCARGA","000");
            }
        }));

    }

    private void consultarListas(){
        SQLiteDatabase db = conn.getReadableDatabase();
        Listas list = null;
        listas = new ArrayList<Listas>();
        Cursor cursor = db.rawQuery("SELECT * from listas ORDER BY lista ASC",null);

        while(cursor.moveToNext()){
            list = new Listas();
            list.setLista(cursor.getString(0));
            listas.add(list);
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
package net.ddsmedia.baceh.asistencia.qr;

import static net.ddsmedia.baceh.BuildConfig.VERSION_NAME;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.ddsmedia.baceh.R;
import net.ddsmedia.baceh.asistencia.qr.api.Globals;
import net.ddsmedia.baceh.asistencia.qr.api.ServiceApi;
import net.ddsmedia.baceh.asistencia.qr.entidad.LoginResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText txtUsuario, txtContrasena;
    private Button btnInicio;
    private ServiceApi serviceApi;
    private ProgressBar progressBar;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("BAMX Hidalgo v"+VERSION_NAME);

        txtUsuario=findViewById(R.id.txtUsuario);
        txtContrasena=findViewById(R.id.txtContrasena);
        btnInicio=findViewById(R.id.btnInicio);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        ////login() POST
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceApi = retrofit.create(ServiceApi.class);

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtUsuario.getText().toString().equals("") && !txtContrasena.getText().toString().equals("")){
                    progressBar.setVisibility(View.VISIBLE);
                }
                Login();
            }
        });
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

    //LOGIN POST()
    private void Login() {
        String u = txtUsuario.getText().toString();
        String contrasena=txtContrasena.getText().toString();
        String c = reverse(MD5(SHA1(contrasena)));
        Log.i("PASS",""+c);

        // if(isNetworkAvailable(this)) {
            if (checkConectivity()) {
                if (u.isEmpty()) {
                    Toast.makeText(this, "Debe ingresar el usuario", Toast.LENGTH_SHORT).show();
                } else if (contrasena.isEmpty()) {
                    Toast.makeText(this, "Debe ingresar la contraseña", Toast.LENGTH_SHORT).show();
                } else {

                    Call<LoginResult> call = serviceApi.listaAccessUser(u, c);
                    call.enqueue(new Callback<LoginResult>() {
                        @Override
                        public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                            if (response.isSuccessful()) {
                                LoginResult resultado = response.body();
                                if (resultado.isError()) {
                                    progressBar.setVisibility(View.GONE);
                                    Log.i("", "" + resultado.getMensaje());
                                    //Toast.makeText(getApplicationContext(),
                                    //        resultado.getMensaje(),
                                    //        Toast.LENGTH_SHORT)
                                    //        .show();
                                } else {
                                    //Toast.makeText(getApplicationContext(),
                                    //        resultado.getMensaje(),
                                    //        Toast.LENGTH_SHORT)
                                    //        .show();

                                    Log.i("LOGIN", "" + resultado.getId_usuario() + " " + resultado.getMensaje());

                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("D");
                                    String formattedDate = df.format(calendar.getTime());

                                    int a = Integer.parseInt(formattedDate);

                                    preferences = getSharedPreferences("cred", Context.MODE_PRIVATE);
                                    String cred = resultado.getMensaje();
                                    editor = preferences.edit();
                                    editor.putString("log", cred);
                                    editor.putString("id_usuario", resultado.getId_usuario());
                                    editor.putInt("date", a);
                                    editor.apply();

                                    txtUsuario.setText("");
                                    txtContrasena.setText("");
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(MainActivity.this, Inicio.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResult> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Log.i("FAILL", "" + t.getMessage());
                            Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
            }
            /*
        }else{
            Log.i("","No esta conectado a una red WIFI");
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "No esta conectado a una red WIFI", Toast.LENGTH_SHORT).show();
        }
             */
    }

    //MD5
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    //SHA-1
    String SHA1( String toHash ) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
            byte[] bytes = toHash.getBytes(StandardCharsets.UTF_8);
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();
            hash = bytesToHex( bytes );
        }
        catch( NoSuchAlgorithmException e ) {
            e.printStackTrace();
        }
        return hash;
    }
    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    public static String bytesToHex( byte[] bytes ) {
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
    }

    //Inverso para contraseña
    public static String reverse(String toReverse) {
        Stack<Character> letters = new Stack<Character>();
        for(char c:toReverse.toCharArray()) {
            letters.push(c);
        }
        StringBuilder sb = new StringBuilder();
        while(!letters.empty()) {
            sb.append(letters.pop());
        }
        return sb.toString();
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
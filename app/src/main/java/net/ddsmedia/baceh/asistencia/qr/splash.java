package net.ddsmedia.baceh.asistencia.qr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;

import net.ddsmedia.baceh.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("D");
        String formattedDate = df.format(calendar.getTime());
        int a = Integer.parseInt(formattedDate);

        SystemClock.sleep(1500);

        SharedPreferences preferences2 = getSharedPreferences("var", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferences2.edit();
        editor2.putString("uno", "123");
        editor2.apply();

        //SharedPreferences validación
        SharedPreferences preferences = getSharedPreferences("cred", Context.MODE_PRIVATE);

        int b = preferences.getInt("date", 0);
        int c=a-b;

        //Inicio de sesión 2 días
        if(b==366 && a>=2){
            preferences.edit().clear().commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if(b==365 && a>=2){
            preferences.edit().clear().commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if(b==364 && a>=1){
            preferences.edit().clear().commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if(c >= 2) {
                    preferences.edit().clear().commit();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }else if (preferences.contains("log")) {
                        Intent intent = new Intent(this, Inicio.class);
                        startActivity(intent);
                       }else {
                               Intent intent = new Intent(this, MainActivity.class);
                               startActivity(intent);
                       }
    }
}
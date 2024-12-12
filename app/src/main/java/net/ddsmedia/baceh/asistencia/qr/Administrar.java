package net.ddsmedia.baceh.asistencia.qr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import net.ddsmedia.baceh.R;
import net.ddsmedia.baceh.asistencia.qr.db.MyOpenHelper;
import net.ddsmedia.baceh.asistencia.qr.entidad.Beneficiarios;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Administrar extends AppCompatActivity {

    ListView listViewPersonas;
    ArrayList<String> listaInformacion;
    ArrayList<Beneficiarios> listaBeneficiarios;
    MyOpenHelper conn;
    //ArrayAdapter adaptador;
    BeneficiariosAdapter adapter;
    SharedPreferences preferences2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar);

        this.setTitle("Administrar beneficiarios");

        //ocultar toolbar
        //getSupportActionBar().hide();

        conn = new MyOpenHelper(getApplicationContext(),MyOpenHelper.DATABASE_NOMBRE,null,MyOpenHelper.DATABASE_VERSION);
        listViewPersonas = findViewById(R.id.lstUsuarios);
        consultarListaPersonas();
        //adaptador = new ArrayAdapter(this, R.layout.mylistview, R.id.textView,listaInformacion);

        //myListview
        adapter = new BeneficiariosAdapter(this,listaBeneficiarios);
        //

        //listViewPersonas.setAdapter(adaptador);
        listViewPersonas.setAdapter(adapter);

        //CLick en listview
        listViewPersonas.setClickable(true);
        listViewPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Beneficiarios item = (Beneficiarios) adapterView.getItemAtPosition(position);

                //listViewPersonas.setSelector(R.color.Banco2);
                //Toast.makeText(Administrar.this, item.toString(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Administrar.this, Editar.class);
                //intent.putExtra("credencial",item.toString());
                intent.putExtra("credencial",item.getCredencial());

                startActivity(intent);
            }
        });

        preferences2 = getSharedPreferences("var", Context.MODE_PRIVATE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Inicio.class);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        startActivity(intent);
    }

    //MENU Busqueda
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menubusqueda,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        MenuItem menuItem2 = menu.findItem(R.id.borrar);
        menuItem2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(Administrar.this, BorrarMunicipios.class);
                startActivity(intent);
                return false;
            }
        });

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Buscar beneficiario");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void consultarListaPersonas(){
        SQLiteDatabase db = conn.getReadableDatabase();
        Beneficiarios beneficiarios = null;
        listaBeneficiarios = new ArrayList<Beneficiarios>();
        Cursor cursor = db.rawQuery("SELECT * from beneficiarios WHERE estatus=1 order by credencial asc",null);

        while(cursor.moveToNext()){
            beneficiarios = new Beneficiarios();
            //beneficiarios.setId(cursor.getInt(0));
            beneficiarios.setId_titular(cursor.getString(0));
            beneficiarios.setCredencial(cursor.getString(1));
            beneficiarios.setFecha_visita(cursor.getString(2));
            beneficiarios.setFk_municipio(cursor.getString(3));
            beneficiarios.setEstatus(cursor.getString(4));
            beneficiarios.setFecha_baja(cursor.getString(5));
            beneficiarios.setFaltas(cursor.getString(6));
            beneficiarios.setUltima_visita(cursor.getString(7));
            beneficiarios.setUltima_falta(cursor.getString(8));
            beneficiarios.setBajas(cursor.getString(9));
            beneficiarios.setTipo(cursor.getString(10));
            beneficiarios.setObservaciones_asist(cursor.getString(11));
            beneficiarios.setId_integrante(cursor.getString(12));
            beneficiarios.setParentesco(cursor.getString(13));
            beneficiarios.setNombre(cursor.getString(14));
            beneficiarios.setApaterno(cursor.getString(15));
            beneficiarios.setAmaterno(cursor.getString(16));
            beneficiarios.setLista(cursor.getString(17));
            beneficiarios.setSincronizar(cursor.getString(18));
            listaBeneficiarios.add(beneficiarios);
        }
        obtenerlista();
    }

    private void obtenerlista(){
        listaInformacion = new ArrayList<String>();
        for(int i=0;i<listaBeneficiarios.size();i++){
            listaInformacion.add(listaBeneficiarios.get(i).getCredencial());
        }
    }

    public class BeneficiariosAdapter extends ArrayAdapter<Beneficiarios> implements Filterable {

        private class ViewHolder {
            TextView textView;
        }

        public ArrayList<Beneficiarios> beneficiarios;
        public ArrayList<Beneficiarios> filteredOrdenes;
        private OrdenFilter ordenFilter;

        public BeneficiariosAdapter(Context context, ArrayList<Beneficiarios> beneficiarios){
            super(context,R.layout.mylistview,beneficiarios);
            this.beneficiarios = beneficiarios;
            this.filteredOrdenes = beneficiarios;

            getFilter();
        }

        @Override
        public int getCount() {
            return filteredOrdenes.size();
        }

        @Override
        public Beneficiarios getItem(int i) {
            return filteredOrdenes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Beneficiarios beneficiarios = getItem(position);

            ViewHolder viewHolder;
            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.mylistview, parent, false);

                viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.textView.setText(beneficiarios.getCredencial());


            if(!beneficiarios.getUltima_visita().equals("0000-00-00")) {

                String listaValue = beneficiarios.getLista();
                int diaList = preferences2.getInt("diaDescargaLista"+listaValue, 0);

                // Definir el formato de la fecha de entrada
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                // Parsear la cadena de fecha a un objeto Date
                Date date = null;
                try {
                    date = inputFormat.parse(beneficiarios.getUltima_visita());
                } catch (ParseException e) {
                    Log.i("","error catch date");
                }
                // Crear una instancia de Calendar y establecer la fecha parseada
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                // Obtener el día del año
                int diaUltimaVisita = calendar.get(Calendar.DAY_OF_YEAR);

                if(diaUltimaVisita >= diaList && diaUltimaVisita <= (diaList+8)){
                    //Log.i("administt","old "+beneficiarios.getCredencial());
                    viewHolder.textView.setBackgroundColor(getContext().getResources().getColor(R.color.Banco2));
                    viewHolder.textView.setTextColor(getContext().getResources().getColor(R.color.black));
                }else{
                    //Log.i("administt","old2 "+beneficiarios.getCredencial());
                    viewHolder.textView.setTextColor(Color.BLACK);
                    viewHolder.textView.setBackgroundColor(Color.WHITE);
                }
            }else {
                //Log.i("administt","nuevo "+beneficiarios.getCredencial());
                viewHolder.textView.setTextColor(Color.BLACK);
                viewHolder.textView.setBackgroundColor(Color.WHITE);
            }


            /*
            // color de beneficiarios con 1 dia diferente de que saco lista

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(calendar.getTime());

            if(beneficiarios.getUltima_visita().equals(formattedDate)) {
                viewHolder.textView.setBackgroundColor(getContext().getResources().getColor(R.color.Banco2));
                viewHolder.textView.setTextColor(getContext().getResources().getColor(R.color.black));

            }else {
                viewHolder.textView.setTextColor(Color.BLACK);
                viewHolder.textView.setBackgroundColor(Color.WHITE);
            }*/

            return convertView;

        }

        @Override
        public Filter getFilter() {
            if(ordenFilter == null){
                ordenFilter = new OrdenFilter();
            }
            return ordenFilter;
        }

        private class OrdenFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<Beneficiarios> tempList = new ArrayList<Beneficiarios>();
                if(constraint != null && beneficiarios!=null) {

                    for(Beneficiarios beneficiarios : beneficiarios){
                        if(beneficiarios.getCredencial().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                beneficiarios.getCredencial().toLowerCase().contains(constraint.toString().toLowerCase())){
                            tempList.add(beneficiarios);
                        }
                    }
                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }else {
                    filterResults.count = beneficiarios.size();
                    filterResults.values = beneficiarios;
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                filteredOrdenes = (ArrayList<Beneficiarios>) results.values;
                notifyDataSetChanged();
            }
        }
    }

}
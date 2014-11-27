package com.example.josu.lectortxt;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class Principal extends Activity {

    TextView tv;
    EditText et;
    Uri data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        tv = (TextView)findViewById(R.id.tvTexto);
        data = getIntent().getData();
        tv.setText(leerTXT(data.toString()));
        et = (EditText)findViewById(R.id.etTexto);
        et.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("et", et.getText().toString());
        outState.putInt("etVisibility",et.getVisibility());
        outState.putInt("tvVisibility",tv.getVisibility());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getString("et") != null)
            et.setText(savedInstanceState.getString("et"));
        if(View.GONE == Integer.valueOf(savedInstanceState.getInt("etVisibility")))
            et.setVisibility(View.GONE);
        else
            if(View.VISIBLE == Integer.valueOf(savedInstanceState.getInt("etVisibility")))
                et.setVisibility(View.VISIBLE);
        if(View.GONE == Integer.valueOf(savedInstanceState.getInt("tvVisibility")))
            tv.setVisibility(View.GONE);
        else
        if(View.VISIBLE == Integer.valueOf(savedInstanceState.getInt("tvVisibility")))
            tv.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_editar) {
            tv.setVisibility(View.GONE);
            et.setVisibility(View.VISIBLE);
            et.setText(tv.getText());
            return true;
        }else
            if(id == R.id.action_guardar){
                try {
                    guardar(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        return super.onOptionsItemSelected(item);
    }

    public String leerTXT(String datos){
        String out="";
        if(isLegible()){
            try {
                URL url = new URL(datos);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String linea;
                while ((linea = in.readLine())  != null){
                    out += linea + "\n";
                }
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    public void guardar(Uri data) throws IOException {
        if (isModificable() && espacioSuficiente(new File(data.getPath()))) {
            try {
                FileWriter writer = new FileWriter(data.getPath());
                writer.write(et.getText().toString());
                writer.close();
                tostada(getResources().getString(R.string.guardado));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            if(!isModificable())
                tostada(getResources().getString(R.string.error_guardar) + " " + getResources().getString(R.string.error_editable));
            else
                tostada(getResources().getString(R.string.error_guardar) + " " + getResources().getString(R.string.error_espacio));

        et.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        tv.setText(et.getText());
    }

    public boolean isModificable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isLegible() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean espacioSuficiente(File f) {
        double eTotal, eDisponible, porcentaje;
        eTotal = (double) f.getTotalSpace();
        eDisponible = (double) f.getFreeSpace();
        porcentaje = (eDisponible / eTotal) * 100;
        return porcentaje > 10;
    }

    public void tostada(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

}

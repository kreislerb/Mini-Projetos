package com.example.kreislerb.pingpongsensor;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by kreislerb on 15/07/2017.
 */

public class Configure extends AppCompatActivity {

    private  double set_pointX ;
    private  double set_pointY ;
    private  double kp;
    private  double ki;
    private  double kd;
    String FILENAME = "bd.txt";

    EditText ed_spx, ed_spy, ed_kp, ed_ki, ed_kd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure);
        ed_spx = (EditText) findViewById(R.id.editTextDialogUserInput);
        ed_spy = (EditText) findViewById(R.id.editTextDialogUserInput3);
        ed_kp = (EditText) findViewById(R.id.editTextDialogUserInput4);
        ed_ki = (EditText) findViewById(R.id.editTextDialogUserInput5);
        ed_kd = (EditText) findViewById(R.id.editTextDialogUserInput6);

        lerArquivo();

        ed_spx.setText(set_pointX+"");
        ed_spy.setText(set_pointY+"");
        ed_kp.setText(kp+"");
        ed_ki.setText(ki+"");
        ed_kd.setText(kd+"");

    }

    public void lerArquivo(){

        FileInputStream in = null;
        try {
            in = openFileInput(FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            Log.i("FileRead", sb.toString());
            String dadosLidos[] = sb.toString().split("#");
            set_pointX = Double.parseDouble(dadosLidos[0]);
            set_pointY = Double.parseDouble(dadosLidos[1]);
            kp = Double.parseDouble(dadosLidos[2]);
            ki = Double.parseDouble(dadosLidos[3]);
            kd = Double.parseDouble(dadosLidos[4]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toast("Arquivo não encontrado!");
        } catch (IOException e) {
            e.printStackTrace();
            toast("Falha ao abrir!");
        }
        catch (NullPointerException e){
            e.printStackTrace();
            toast("Vetores nulos!");
        }
    }
    public void btnSalvar(View v){

        String string = ed_spx.getText().toString()+
                    "#"+ed_spy.getText().toString()+
                    "#"+ed_kp.getText().toString()+
                    "#"+ed_ki.getText().toString()+
                    "#"+ed_kd.getText().toString();

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
            toast("Alteração realizada com sucesso!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toast("Arquivo não encontrado!");
        }
        catch (IOException e) {
            e.printStackTrace();
            toast("Falha ao salvar!");
        }


    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

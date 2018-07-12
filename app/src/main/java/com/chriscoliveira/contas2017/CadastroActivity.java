package com.chriscoliveira.contas2017;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

@SuppressWarnings("deprecation")
public class CadastroActivity extends ActionBarActivity {
    Toolbar mToolbar;

    Banco banco = new Banco();

    Button btGravar;

    EditText etConta, etValor, etParcela, etDia, etMes, etAno;
    Spinner spTipo, spCategoria;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastrar_activity);

		/*
         * codigo pagina
		 */

        etConta = (EditText) findViewById(R.id.etConta);
        etValor = (EditText) findViewById(R.id.etValor);
        etParcela = (EditText) findViewById(R.id.etParcela);
        etDia = (EditText) findViewById(R.id.etDia);
        etMes = (EditText) findViewById(R.id.etMes);
        etAno = (EditText) findViewById(R.id.etAno);
        spTipo = (Spinner) findViewById(R.id.spTipo);
        spCategoria = (Spinner) findViewById(R.id.spCategoria);

        try {
            etDia.setText(""+banco.RetornaDia());
            etMes.setText(""+banco.RetornaMes());
            etAno.setText(""+banco.RetornaAno());
        }
        catch (Exception e){
            Log.i("erro","E: "+e);
        }
        // Get reference of widgets from XML layout

        spinnerTipo();



        btGravar = (Button) findViewById(R.id.btGravar);
        btGravar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etParcela.getText().toString().equals("")){
                    etParcela.setText("1");
                }
                banco.cadastarNovo(CadastroActivity.this, etConta.getText().toString(), etValor.getText().toString(),
                        etParcela.getText().toString(), etDia.getText().toString(), etMes.getText().toString(),
                        etAno.getText().toString(), spTipo.getSelectedItem().toString(),
                        spCategoria.getSelectedItem().toString());
                etConta.setText("");
                etValor.setText("");
                etParcela.setText("");
                etConta.setFocusable(true);
                etConta.requestFocus();
            }
        });

		/*
		 * fim do codigo
		 */

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Cadastrar");
        mToolbar.setLogo(R.drawable.ico);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void spinnerTipo(){
        final Spinner spinnerTipo = (Spinner) findViewById(R.id.spTipo);
        String[] Tipo = new String[]{"Pagar", "Receber"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinneritem, Tipo);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        spinnerTipo.setAdapter(spinnerArrayAdapter);

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerCategoria(spinnerTipo.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void spinnerCategoria(String spinnertipo) {
        Spinner spinnerCategoria = (Spinner) findViewById(R.id.spCategoria);

        

         String[] CategoriaPagar = new String[]{"Cartao", "Alimentacao", "Educacao", "Lazer", "Moradia", "Roupa", "Saude",
                        "Transporte", "Banco", "Chris", "Mae", "Patty", "Outros"};




        String[] CategoriaReceber = new String[]{"Salario", "13 Salario", "Ferias"};
        if (spinnertipo.equals("Pagar")) {
            ArrayAdapter<String> spinner1ArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinneritem, CategoriaPagar);
            spinner1ArrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            spinnerCategoria.setAdapter(spinner1ArrayAdapter);
        } else {
            ArrayAdapter<String> spinner1ArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinneritem, CategoriaReceber);
            spinner1ArrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            spinnerCategoria.setAdapter(spinner1ArrayAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.getItemId();

        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;

            case R.id.action_rendas:
                startActivity(new Intent(this, RendaActivity.class));
                finish();
                break;

            case R.id.action_despesas:
                startActivity(new Intent(this, DespesaActivity.class));
                finish();
                break;
            case R.id.action_cartao:
                startActivity(new Intent(this, CartaoActivity.class));
                finish();
                break;
            case R.id.action_export_email:
                banco.CriaListaParaExporacao(this);
                break;
            case R.id.action_novo:
                startActivity(new Intent(this, CadastroActivity.class));
                finish();
                break;
            case R.id.action_resumo:
                startActivity(new Intent(this, MesAMesActivity.class));
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(CadastroActivity.this, AjustesActivity.class));
                finish();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

}

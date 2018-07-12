package com.chriscoliveira.contas2017;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

@SuppressWarnings("deprecation")
public class AjustesActivity extends ActionBarActivity {
    Toolbar mToolbar;

    //int pos = -1;
    //int consulta = 0;
    //Button btFiltrar;

    Banco banco = new Banco();
    //ZUtilitarios zutil = new ZUtilitarios();

    //Button btGravar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes_activity);
         /*
         * exibe data de compilação
		 */
        TextView tvVersao = (TextView) findViewById(R.id.tvVersao);
        Date buildDate = new Date(BuildConfig.BUILD_TIME);
        tvVersao.setText(" " + buildDate.toString());

		/*
         * codigo pagina
		 */

        Button btImportar = (Button) findViewById(R.id.btImportar);
        Button btExportar = (Button) findViewById(R.id.btExportar);
        Button btLimpar = (Button) findViewById(R.id.btLimpar);

        btImportar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                banco.importarLista(AjustesActivity.this);
            }
        });
        btExportar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                banco.CriaListaParaExporacao(AjustesActivity.this);
            }
        });
        btLimpar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder CaixaAlerta = new AlertDialog.Builder(AjustesActivity.this);
                CaixaAlerta.setMessage("Confirma a limpeza do banco?");
                CaixaAlerta.setTitle("APAGAR TUDO?");
                CaixaAlerta.setPositiveButton("SIM",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                banco.deletar(AjustesActivity.this);
                            }
                        });
                CaixaAlerta.setNegativeButton("NAO", null);
                CaixaAlerta.show();

            }
        });

		/*
         * fim do codigo
		 */

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Ajustes");
        mToolbar.setLogo(R.drawable.ico);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }
}

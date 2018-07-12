package com.chriscoliveira.contas2017;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MesAMesActivity extends ActionBarActivity {
    Toolbar mToolbar;


    Banco banco = new Banco();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mesames_activity);

		/*
         * codigo pagina
		 */


        int ano = banco.RetornaAno();

        final TextView tvano = (TextView) findViewById(R.id.tvAnoFiltro);
        tvano.setText(ano + "");
        ImageButton btVoltaMes, btAvancaMes;
        btVoltaMes = (ImageButton) findViewById(R.id.btVoltames);
        btAvancaMes = (ImageButton) findViewById(R.id.btAvancaMes);
        banco.SomaExibeMM(MesAMesActivity.this, tvano.getText().toString());

        btVoltaMes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tvano.setText("" + (Integer.parseInt(tvano.getText().toString()) - 1));
                banco.SomaExibeMM(MesAMesActivity.this, tvano.getText().toString());
                Log.i("click", "volta");
            }
        });

        btAvancaMes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tvano.setText("" + (Integer.parseInt(tvano.getText().toString()) + 1));
                banco.SomaExibeMM(MesAMesActivity.this, tvano.getText().toString());
            }
        });

		/*
         * fim do codigo
		 */

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Resumo");
        mToolbar.setSubtitle("Resumo das contas mes a mes");
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

            case R.id.action_despesas:
                startActivity(new Intent(this, DespesaActivity.class));
                finish();
                break;
            case R.id.action_rendas:
                startActivity(new Intent(this, RendaActivity.class));
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
                startActivity(new Intent(this, AjustesActivity.class));
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

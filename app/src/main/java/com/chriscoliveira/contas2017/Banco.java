package com.chriscoliveira.contas2017;

/*
 * 05-11-15 personalizado o resumo da tela inicial  
 * 08-11-15 modificacao na tela de cadastro/ alteração do registro para limpar o campo parcela quando clicado 
 * 20/11/15 corrigido a opcao de cadastrar parcela e valores vazios, caso ocorra sera colocado o valor 1 para ambos os campos
 * 20/11/15 adicionado codigo a pagina de configuração para exibir a data de compilacao da versao.
 * 20/11/15 exibe os regs baseado em 2 campos situacao e conta
 * 04/12/15 adicionado a opcao de exibir o resumo de contas até o dia 20 e depois do dia 20 (pagto e vale)
 * 04/12/15 ordenado a exibição das contas por dia e situacao para melhor visualizacao.
 * 12/01/17 feito ajustes de no codigo para a coloração dos menus.
 * 12/01/17 alterado no menu inicial os valores a serem pagos ate o dia 20 e depois do mesmo para serem subtraidos do valor que ja foi pago
 * 12/01/17 modificado na tela ajustes a informação da data de compilação e adicionado na tela inicial
 * 12/01/17 corrigido spinner conforme selecionado na tela de cadastro
 * 12/01/17 implementado mudanca na parcela caso seja igual a vazio sera cadastro o valor 1
 * 12/01/17 iniciado o processo de separacao das sql's para a classe SQL (assim ficara mais facil de se controlar e evitar codigo repetido)
 * 18/01/17 modificado a cor da toolbar da tela de inicio de acordo com a saude do mes
 * 18/01/17 feito a troca do icone do app
  *08/02/17 corrigido o calculo de total a pagar no mes (meu)
  *27/09/17 adicionado funcao para mais de 1 cartao de credito
  *
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;


public class Banco extends Activity {

    protected SQLiteDatabase bancoDados = null;
    public static String NOME_BANCO = "MinhasContas2014";
    public static String NOME_TABELA_CONTAS = "Contas";
    public static String NOME_TABELA_CATEGORIAS = "Categorias";
    public static String ID = "_id";
    public static String CONTA = "conta";
    public static String VALOR = "valor";
    public static String ANO = "ano";
    public static String MES = "mes";
    public static String DIA = "dia";
    public static String PARCELA = "parcela";
    public static String SITUACAO = "situacao";
    public static String TIPO = "tipo";
    public static String CATEGORIA = "categoria";


    //ListView MostraDados;
    //SimpleCursorAdapter adapterLista;
    Cursor cursor;
    String Valores = "";
    Dialog dialog;
    CheckBox cbSituacao;
    Button btAcaoDialog, btAcaoApagar;
    String txtcb;
    Toolbar mToolbar;


    int diA = RetornaDia();
    int meS = RetornaMes();
    int anO = RetornaAno();

	/*
     *
	 * TODO cria o banco de dados
	 * 
	 */

    SQL sqlClass = new SQL();

    public void AbreCriaBanco(Activity activity) {


        // uso do context devido a classe ser simples e nao poder
        // executar o comando em outra classe
        bancoDados = activity.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
        bancoDados.execSQL(sqlClass.criaTabela);
        bancoDados.execSQL(sqlClass.criaTabelaListadeCategorias);
        bancoDados.close();

    }

	/*
     * TODO abre a conexao com o banco
	 */

    public void AbreBanco(Activity activity) {
        bancoDados = activity.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
    }

	/*
     * TODO Fecha a conexao com o banco
	 */

    public void FechaBanco() {
        bancoDados.close();
    }

	/*
     * TODO cadastro de novos registros
	*/

    public void cadastarNovo(final Activity activity, String conta, String valor, String parcela, String dia,
                             String mes, String ano, String tipo, String categoria) {
        AbreBanco(activity);

        if (parcela.equals(""))
            parcela = "1";
        if (parcela.equals("0"))
            parcela = "1";
        if (valor.equals(""))
            valor = "1";
        if (valor.equals("0"))
            valor = "1";

        ContentValues contentValuesCampos = new ContentValues();
        Integer Parcela = Integer.parseInt(parcela), Mes = Integer.parseInt(mes), Ano = Integer.parseInt(ano);
        String parcelas;

        int anual = Ano, mensal = Mes;
        for (int i = 0; i < Parcela; i++) {
            int numeroParcela = i + 1;
            parcelas = "Parcela " + numeroParcela + "-" + parcela;
            if (i != 0) {
                if (mensal == 0) {
                    mensal = Mes + numeroParcela - 1;
                    anual = Ano;
                } else
                    mensal++;
            }
            if (mensal == 13) {
                mensal = 1;
                anual++;
            }

            contentValuesCampos.put(CONTA, conta);
            contentValuesCampos.put(VALOR, valor);
            contentValuesCampos.put(PARCELA, parcelas);
            contentValuesCampos.put(ANO, anual);
            contentValuesCampos.put(DIA, dia);
            contentValuesCampos.put(MES, mensal);
            contentValuesCampos.put(TIPO, tipo);
            contentValuesCampos.put(CATEGORIA, categoria);
            contentValuesCampos.put(SITUACAO, "_");

            try {
                bancoDados.insert(NOME_TABELA_CONTAS, null, contentValuesCampos);
                Toast.makeText(activity, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(activity, "Erro ao efetuar o cadastro: " + e, Toast.LENGTH_SHORT).show();
            }

        }
    }

	/*
     * TODO Atualiza reg
	 */

    public void atualiza(Activity activity, String tabela, String conta, String categoria, String valor, String parcela,
                         String dia, String mes, String ano, String situacao, String id, String tipo) {
        // Log.i("aviso", "atz");

        String texto = ID + " = " + id;
        AbreBanco(activity);
        ContentValues contentValuesCampos = new ContentValues();
        contentValuesCampos.put(CONTA, conta);
        contentValuesCampos.put(CATEGORIA, categoria);
        contentValuesCampos.put(VALOR, valor);
        contentValuesCampos.put(PARCELA, parcela);
        contentValuesCampos.put(ANO, ano);
        contentValuesCampos.put(DIA, dia);
        contentValuesCampos.put(MES, mes);
        contentValuesCampos.put(SITUACAO, situacao);
        contentValuesCampos.put(TIPO, tipo);
        bancoDados.update(tabela, contentValuesCampos, texto, null);
        FechaBanco();


    }

    /*
     * TODO deleta
     */
    public void delete(Activity activity, String tabela, int id, String tipo) {
        try {
            String texto = ID + " = " + id;
            AbreBanco(activity);
            bancoDados.delete(tabela, texto, null);
            FechaBanco();
            carregaDados(activity, tipo, "nao", "", "");

            //int dia = RetornaDia();
            int mes = RetornaMes();
            int ano = RetornaAno();

            SomaExibe(activity, mes + "", ano + "");
        } catch (Exception e) {
            // Log.i("aviso", "erro " + e);
        }
    }

	/*
     * TODO Verifica a query e exibe o retorno na tela
	 */

    private boolean VerificaRegistro(Activity activity, String tipo, String cartaoSimOuNao, String vMes, String vAno) {
        int mes = 0, ano = 0;
        try {
            if (vMes.equals("")) {

                mes = RetornaMes();
                ano = RetornaAno();
            }
            if (!vMes.equals("")) {
                mes = Integer.parseInt(vMes);
                ano = Integer.parseInt(vAno);
            }
            AbreBanco(activity);

            if (cartaoSimOuNao.equals("sim")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "='Cartao'",
                        null, null, null, SITUACAO + " ASC, " + DIA + " ASC");
            }

            if (cartaoSimOuNao.equals("nao")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "!='Cartao'",
                        null, null, null, SITUACAO + " ASC, " + DIA + " ASC");
            }

            if (cursor.getCount() != 0) // se existir registro
            {
                cursor.moveToFirst(); // movimenta para o 1º registro
                return true;
            } else
                return false;

        } catch (Exception er) {
            return false;
        } finally {
            FechaBanco();
        }

    }

	/*
     * TODO Carrega os dados para o listview com base no mes e ano consultado. *
	 */

    @SuppressWarnings("deprecation")
    public void carregaDados(Activity activity, String tipo, String cartaoSimOuNao, String mes, String ano) {

        ListView MostraDados = (ListView) activity.findViewById(R.id.lvListagem);
        if (VerificaRegistro(activity, tipo, cartaoSimOuNao, mes, ano)) {
            SimpleCursorAdapter adapterLista = new SimpleCursorAdapter(activity, R.layout.tela_listagem_itens, cursor,
                    new String[]{ID, CONTA, VALOR, SITUACAO, CATEGORIA, PARCELA, DIA, MES, ANO},
                    new int[]{R.id.tvId, R.id.tvConta, R.id.tvValor, R.id.tvPago, R.id.tvCategoria, R.id.tvParcel,
                            R.id.tvDia, R.id.tvMes, R.id.tvAno});
            MostraDados.setAdapter(adapterLista); // executa a ação

        } else {
            MostraDados.setAdapter(null);

        }

    }

    @SuppressLint("DefaultLocale")
    public void SomaExibe(Activity activity, String mes, String ano) {
        String valorP, valorR, valorF, valorC;
        double totalP = 0, totalR = 0, totalS, totalF = 0, totalC = 0;
        AbreBanco(activity);
        // Pagar
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + mes + "'", null, null, null, null);
        while (cursor.moveToNext()) {
            valorP = cursor.getString(cursor.getColumnIndex(VALOR));
            valorP = valorP.replaceAll(",", ".");
            totalP += Double.parseDouble(valorP);
        }
        TextView tvRPagar = (TextView) activity.findViewById(R.id.tvPagar);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", totalP));

        // Cartao

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND " + CATEGORIA + " ='Cartao' AND ano ='" + ano + "' and mes='" + mes + "'",
                null, null, null, null);
        while (cursor.moveToNext()) {
            valorC = cursor.getString(cursor.getColumnIndex(VALOR));

            valorC = valorC.replaceAll(",", "."); // troca a , por .

            totalC += Double.parseDouble(valorC);

        }
        TextView tvCartao = (TextView) activity.findViewById(R.id.tvCartao);

        tvCartao.setText(String.format("%.2f", totalC));

        // Receber
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Receber' AND ano ='" + ano + "' and mes='" + mes + "'", null, null, null, null);
        while (cursor.moveToNext()) {
            valorR = cursor.getString(cursor.getColumnIndex(VALOR));
            valorR = valorR.replaceAll(",", "."); // troca a , por .

            totalR += Double.parseDouble(valorR);

        }
        TextView tvRReceber = (TextView) activity.findViewById(R.id.tvReceber);
        tvRReceber.setText("Receber R$" + String.format("%.2f", totalR));

        // Falta
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + mes + "' and situacao ='_'", null, null, null,
                null);
        while (cursor.moveToNext()) {
            valorF = cursor.getString(cursor.getColumnIndex(VALOR));
            valorF = valorF.replaceAll(",", "."); // troca a , por .
            totalF += Double.parseDouble(valorF);
        }
        TextView tvRFalta = (TextView) activity.findViewById(R.id.tvFalta);
        tvRFalta.setText("Falta R$" + String.format("%.2f", totalF));
        totalS = totalR - totalP;
        TextView tvRSaldo = (TextView) activity.findViewById(R.id.tvSaldo);
        tvRSaldo.setText(String.format("%.2f", totalS));

        FechaBanco();

    }

	/*
     * TODO soma e exibe o mes a mes
	 */

    @SuppressLint("DefaultLocale")
    public void SomaExibeMM(Activity activity, String ano) {

        AbreBanco(activity);

        String ValorPagar[] = new String[15], ValorReceber[] = new String[15];
        double TotalPagar[] = new double[15], TotalReceber[] = new double[15];
        TextView tvRPagar, tvRReceber;

        for (int Mes = 1; Mes < 13; Mes++) {
            try {
                cursor = bancoDados.query(NOME_TABELA_CONTAS, null, TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + Mes + "'",
                        null, null, null, null);
                while (cursor.moveToNext()) {
                    ValorPagar[Mes] = cursor.getString(cursor.getColumnIndex(VALOR));
                    ValorPagar[Mes] = ValorPagar[Mes].replaceAll(",", "."); // troca a , por .
                    TotalPagar[Mes] += Double.parseDouble(ValorPagar[Mes]);
                }

                cursor = bancoDados.query(NOME_TABELA_CONTAS, null, TIPO + "='Receber' AND ano ='" + ano + "' and mes='" + Mes + "'",
                        null, null, null, null);
                while (cursor.moveToNext()) {
                    ValorReceber[Mes] = cursor.getString(cursor.getColumnIndex(VALOR));
                    ValorReceber[Mes] = ValorReceber[Mes].replaceAll(",", "."); // troca a , por .
                    TotalReceber[Mes] += Double.parseDouble(ValorReceber[Mes]);
                }
                Log.i("resumo", "valorReceber" + TotalReceber[Mes]);
            } catch (Exception e) {
                Log.i("resumo", "erro " + e);
            }
        }

        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar1);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[1]));
        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar2);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[2]));
        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar3);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[3]));
        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar4);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[4]));
        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar5);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[5]));
        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar6);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[6]));
        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar7);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[7]));
        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar8);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[8]));
        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar9);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[9]));
        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar10);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[10]));
        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar11);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[11]));
        tvRPagar = (TextView) activity.findViewById(R.id.tvPagar12);
        tvRPagar.setText("Pagar R$" + String.format("%.2f", TotalPagar[12]));

        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber1);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[1]));
        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber2);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[2]));
        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber3);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[3]));
        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber4);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[4]));
        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber5);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[5]));
        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber6);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[6]));
        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber7);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[7]));
        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber8);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[8]));
        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber9);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[9]));
        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber10);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[10]));
        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber11);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[11]));
        tvRReceber = (TextView) activity.findViewById(R.id.tvReceber12);
        tvRReceber.setText("Receber R$" + String.format("%.2f", TotalReceber[12]));
        FechaBanco();
    }

	/*
     * TODO Dialog
	 */

    public void dialog(final Activity activity, final String acao, final String tipo, String texto,
                       final int posicao, final String cartaoSimOuNao) {

        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.cadastrar_activity);
        dialog.setTitle(texto);
        mToolbar = (Toolbar) dialog.findViewById(R.id.tb_main);
        final TextView tvId = (TextView) dialog.findViewById(R.id.tvId);
        final EditText etConta = (EditText) dialog.findViewById(R.id.etConta);
        final EditText etValor = (EditText) dialog.findViewById(R.id.etValor);
        final EditText etParcela = (EditText) dialog.findViewById(R.id.etParcela);
        final EditText etDia = (EditText) dialog.findViewById(R.id.etDia);
        final EditText etMes = (EditText) dialog.findViewById(R.id.etMes);
        final EditText etAno = (EditText) dialog.findViewById(R.id.etAno);
        final EditText etTipo = (EditText) dialog.findViewById(R.id.etTipo);
        final EditText etCategoria = (EditText) dialog.findViewById(R.id.etCategoria);
        final Spinner spinnerTipo = (Spinner) dialog.findViewById(R.id.spTipo);
        final Spinner spinnerCategoria = (Spinner) dialog.findViewById(R.id.spCategoria);
        final TextView txtTitulo = (TextView) dialog.findViewById(R.id.txtTitulo);

        txtTitulo.setText("Editar");
        mToolbar.setVisibility(View.INVISIBLE);
        etCategoria.setVisibility(View.VISIBLE);
        etTipo.setVisibility(View.VISIBLE);
        spinnerTipo.setVisibility(View.GONE);
        spinnerCategoria.setVisibility(View.GONE);
        etDia.setText("" + diA);
        etMes.setText("" + meS);
        etAno.setText("" + anO);

        cbSituacao = (CheckBox) dialog.findViewById(R.id.cbPago);
        btAcaoApagar = (Button) dialog.findViewById(R.id.btApagar);
        btAcaoApagar.setVisibility(View.VISIBLE);
        btAcaoApagar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                delete(activity, NOME_TABELA_CONTAS, posicao, tipo);
                carregaDados(activity, tipo, cartaoSimOuNao, "", "");
                dialog.dismiss();
            }
        });

        btAcaoDialog = (Button) dialog.findViewById(R.id.btGravar);
        btAcaoDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbSituacao.isChecked()) {
                    txtcb = "ok";
                } else
                    txtcb = "_";
                atualiza(activity, NOME_TABELA_CONTAS, etConta.getText().toString(), etCategoria.getText().toString(),
                        etValor.getText().toString(), etParcela.getText().toString(), etDia.getText().toString(),
                        etMes.getText().toString(), etAno.getText().toString(), txtcb, tvId.getText().toString(), tipo);
                dialog.cancel();

                carregaDados(activity, tipo, cartaoSimOuNao, "", "");
                SomaExibe(activity, meS + "", anO + "");
            }
        });

        if (acao.equals("atz")) {
            btAcaoDialog.setText("Atualizar");

        }
        dialog.show();

        enviaDadosDialog(activity, NOME_TABELA_CONTAS, posicao);

    }

	/*
     * TODO envia dialog
	 */

    public void enviaDadosDialog(Activity activity, String tabela, int Posicao) {
        AbreBanco(activity);
        cursor = bancoDados.query(tabela, null, ID + "=" + Posicao, null, null, null, null);
        // sql(tabela, ID+"="+Posicao,tipo);
        final EditText etConta = (EditText) dialog.findViewById(R.id.etConta);
        final EditText etCategoria = (EditText) dialog.findViewById(R.id.etCategoria);
        final EditText etValor = (EditText) dialog.findViewById(R.id.etValor);
        final EditText etParcela = (EditText) dialog.findViewById(R.id.etParcela);
        final EditText etDia = (EditText) dialog.findViewById(R.id.etDia);
        final EditText etMes = (EditText) dialog.findViewById(R.id.etMes);
        final EditText etAno = (EditText) dialog.findViewById(R.id.etAno);
        final TextView tvId = (TextView) dialog.findViewById(R.id.tvId);
        final CheckBox cbPago = (CheckBox) dialog.findViewById(R.id.cbPago);
        final EditText etTipo = (EditText) dialog.findViewById(R.id.etTipo);

        while (cursor.moveToNext()) {
            etConta.setText(cursor.getString(cursor.getColumnIndex(CONTA)));
            etValor.setText(cursor.getString(cursor.getColumnIndex(VALOR)));
            etParcela.setText(cursor.getString(cursor.getColumnIndex(PARCELA)));
            etDia.setText(cursor.getString(cursor.getColumnIndex(DIA)));
            etMes.setText(cursor.getString(cursor.getColumnIndex(MES)));
            etAno.setText(cursor.getString(cursor.getColumnIndex(ANO)));
            tvId.setText(cursor.getString(cursor.getColumnIndex(ID)));
            etTipo.setText(cursor.getString(cursor.getColumnIndex(TIPO)));
            etCategoria.setText(cursor.getString(cursor.getColumnIndex(CATEGORIA)));
            if (cursor.getString(cursor.getColumnIndex(SITUACAO)).equals("ok")) {
                cbPago.setChecked(true);
            }
        }

        FechaBanco();

    }

	/*
     * TODO show alert dialog
	 */

    @SuppressWarnings("unused")
    private void createAndShowAlertDialog(final Activity activity, final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção: Deseja excluir o registro??");
        builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                delete(activity, NOME_TABELA_CONTAS, pos, "Receber");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

	/*
     * TODO importar lista
	 */


    @SuppressWarnings("resource")
    public void importarLista(Activity activity) {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "/Download/bancoContas.txt");
        AbreBanco(activity);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line, sql;
            while ((line = br.readLine()) != null) {
                // Log.i("sql", "" + line);
                sql = line;
                bancoDados.execSQL(sql);
            }
            Toast.makeText(activity, "Dados importados com sucesso! ", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity, "Erro ao importar! " + e, Toast.LENGTH_SHORT).show();
        }
        FechaBanco();
    }

    /*
        TODO criar lista exportacao
     */
    public void CriaListaParaExporacao(Activity activity) {
        @SuppressWarnings("unused")
        int contagem = 0;
        AbreBanco(activity);
        Cursor cc = bancoDados.query(NOME_TABELA_CONTAS, null, null, null, null, null, null);

        while (cc.moveToNext()) {
            Valores += "INSERT INTO Contas (conta,valor,parcela,ano,mes,dia,situacao,tipo,categoria) VALUES ('";
            Valores += cc.getString(cc.getColumnIndex("conta")) + "','"
                    + cc.getString(cc.getColumnIndex("valor")) + "','"
                    + cc.getString(cc.getColumnIndex("parcela")) + "','"
                    + cc.getString(cc.getColumnIndex("ano")) + "','"
                    + cc.getString(cc.getColumnIndex("mes")) + "',"
                    + cc.getString(cc.getColumnIndex("dia")) + ",'"
                    + cc.getString(cc.getColumnIndex("situacao")) + "','" + cc.getString(cc.getColumnIndex("tipo"))
                    + "','" + cc.getString(cc.getColumnIndex("categoria")) + "'),; \n";

            contagem++;
        }

        Valores = Valores.replaceAll(",;", ";");

        SalvarArquivo(Valores, activity);
        cc.close();
        Toast.makeText(activity, "Exportação dos dados realizada com sucesso!", Toast.LENGTH_SHORT).show();
        FechaBanco();
    }

    /*
    TODO salvar arquivo
     */

    private void SalvarArquivo(String valor, Activity activity) {

        File arq;
        FileOutputStream fos;
        byte[] dados;
        try {
            arq = new File(Environment.getExternalStorageDirectory(), "/Download/bancoContas.txt");
            fos = new FileOutputStream(arq.toString());

            dados = valor.getBytes();

            fos.write(dados);
            fos.flush();
            fos.close();
            enviarEmail(activity);
        } catch (IOException e) {
            Toast.makeText(activity, "Exportação com erro! " + e, Toast.LENGTH_SHORT).show();

        }
    }


    /*
    TODO ENVIAR EMAIL
     */
    public void enviarEmail(Activity activity) {
        String subject = "Controle de Contas - backup";
        String message = "Segue em anexo um backup dos dados ";

        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "/Download/bancoContas.txt"));

        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, message);
        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.setType("text/plain");
        activity.startActivity(Intent.createChooser(i, "Send mail"));

    }

    /*
    TODO DELETAR
     */

    public void deletar(Activity activity) {
        AbreBanco(activity);
        try {
            bancoDados.delete(NOME_TABELA_CONTAS, null, null);
            Toast.makeText(activity, "Dados apagados com sucesso", Toast.LENGTH_SHORT).show();
        } catch (Exception er) {
            Toast.makeText(activity, "Erro! " + er, Toast.LENGTH_SHORT).show();
        }
        FechaBanco();
    }

	/* Pagar contas cartao do mes selecionado
     */

    public void pagarCartao(Activity activity, String mes, String ano, String acao) {
        String texto = CATEGORIA + " = 'Cartao' AND " + MES + "='" + mes + "' AND " + ANO + "= '" + ano + "'";
        AbreBanco(activity);
        ContentValues contentValuesCampos = new ContentValues();
        contentValuesCampos.put(SITUACAO, acao);

        bancoDados.update(NOME_TABELA_CONTAS, contentValuesCampos, texto, null);
        FechaBanco();
        carregaDados(activity, "Pagar", "sim", "", "");
        SomaExibe(activity, meS + "", anO + "");
    }

  

	/*
 TODO faz a soma dos valores a pagar/receber/saldo/falta pagar e retorna
	 * no metodo verificaregistro()
	 */


    @SuppressLint("DefaultLocale")
    public void SomaExibeMainActivity(Activity activity, String mes, String ano) {
        String valorPagar, valorPagarMeu, valorReceber, valorFalta, valorFaltaMeu, valorCartao, valorPatty, valorMae, valorLazer,
                valorChris, valorate20, valordepois20;
        double totalPagar = 0, totalPagarMeu = 0, totalReceber = 0, totalSaldo, totalFalta = 0,
                totalFaltaMeu = 0, totalCartao = 0, totalPatty = 0, totalMae = 0, totalLazer = 0, totalChris = 0,
                totalate20 = 0, totaldepois20 = 0;
        AbreBanco(activity);

        // até dia 15
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria != 'Patty' AND categoria != 'Mae' AND categoria != 'Chris' AND conta not like 'Mae%'"
                        + " AND conta not like 'Patty%' AND conta not like 'Chris%' AND ano ='" + ano
                        + "' and mes='" + mes + "' AND dia < 21",
                null, null, null, null);
        while (cursor.moveToNext()) {
            valorate20 = cursor.getString(cursor.getColumnIndex(VALOR));
            valorate20 = valorate20.replaceAll(",", ".");
            totalate20 += Double.parseDouble(valorate20);
        }
        TextView tvAte20 = (TextView) activity.findViewById(R.id.tvtotalantes20);
        tvAte20.setText("Antes 20 R$" + String.format("%.2f", totalate20));

        // depois dia 20
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria != 'Patty' AND categoria != 'Mae' AND categoria != 'Chris' AND conta not like 'Mae%'"
                        + " AND conta not like 'Patty%' AND conta not like 'Chris%' AND ano ='" + ano
                        + "' and mes='" + mes + "' AND dia > 20",
                null, null, null, null);
        while (cursor.moveToNext()) {
            valordepois20 = cursor.getString(cursor.getColumnIndex(VALOR));
            valordepois20 = valordepois20.replaceAll(",", ".");
            totaldepois20 += Double.parseDouble(valordepois20);
        }
        TextView tvdepois20 = (TextView) activity.findViewById(R.id.tvtotaldepois20);
        tvdepois20.setText("Depois 20 R$" + String.format("%.2f", totaldepois20));

        // Pagar
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + mes + "'", null, null, null, null);
        while (cursor.moveToNext()) {
            valorPagar = cursor.getString(cursor.getColumnIndex(VALOR));
            valorPagar = valorPagar.replaceAll(",", "."); // troca a , por .
            totalPagar += Double.parseDouble(valorPagar);
        }
        TextView tvRPagar = (TextView) activity.findViewById(R.id.tvPagar);
        tvRPagar.setText("Total a Pagar R$" + String.format("%.2f", totalPagar));

        // Pagar meu
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria != 'Patty' AND categoria != 'Mae' AND categoria != 'Chris' AND conta not like 'Mae%'"
                        + " AND conta not like 'Patty%' AND conta not like 'Chris%' AND ano ='" + ano
                        + "' and mes='" + mes + "'",
                null, null, null, null);
        while (cursor.moveToNext()) {
            valorPagarMeu = cursor.getString(cursor.getColumnIndex(VALOR));
            valorPagarMeu = valorPagarMeu.replaceAll(",", ".");
            totalPagarMeu += Double.parseDouble(valorPagarMeu);
        }
        TextView tvRPagarMeu = (TextView) activity.findViewById(R.id.tvPagarMeu);
        tvRPagarMeu.setText("Pagar R$" + String.format("%.2f", totalPagarMeu));

        // Cartao
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND " + CATEGORIA + " ='Cartao' AND ano ='" + ano + "' and mes='" + mes + "'",
                null, null, null, null);
        while (cursor.moveToNext()) {
            valorCartao = cursor.getString(cursor.getColumnIndex(VALOR));
            valorCartao = valorCartao.replaceAll(",", "."); // troca a , por
            totalCartao += Double.parseDouble(valorCartao);
        }
        TextView tvCartao = (TextView) activity.findViewById(R.id.tvCartao);
        tvCartao.setText("Cartao R$ " + String.format("%.2f", totalCartao));

        if (totalCartao == 0) {
            tvCartao.setVisibility(View.GONE);
        } else {
            tvCartao.setVisibility(View.VISIBLE);
        }

        // Receber
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Receber' AND ano ='" + ano + "' and mes='" + mes + "'", null, null, null, null);
        while (cursor.moveToNext()) {
            valorReceber = cursor.getString(cursor.getColumnIndex(VALOR));
            valorReceber = valorReceber.replaceAll(",", "."); // troca a ,
            totalReceber += Double.parseDouble(valorReceber);
        }
        TextView tvRReceber = (TextView) activity.findViewById(R.id.tvReceber);
        tvRReceber.setText("Total a Receber R$" + String.format("%.2f", totalReceber));

        // Falta
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + mes + "' and situacao ='_'", null, null, null,
                null);
        while (cursor.moveToNext()) {
            valorFalta = cursor.getString(cursor.getColumnIndex(VALOR));
            valorFalta = valorFalta.replaceAll(",", "."); // troca a , por .
            totalFalta += Double.parseDouble(valorFalta);
        }
        TextView tvRFalta = (TextView) activity.findViewById(R.id.tvFalta);
        tvRFalta.setText("Falta Pagar Total R$" + String.format("%.2f", totalFalta));

        // Falta meu
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria != 'Patty' AND categoria != 'Mae' AND categoria != 'Chris' AND conta not like 'Mae%'"
                        + " AND conta not like 'Patty%' AND conta not like 'Chris%' AND ano ='" + ano
                        + "' and mes='" + mes + "' and situacao ='_'",
                null, null, null, null);

        while (cursor.moveToNext()) {
            valorFaltaMeu = cursor.getString(cursor.getColumnIndex(VALOR));
            valorFaltaMeu = valorFaltaMeu.replaceAll(",", "."); // troca a ,
            // por .
            totalFaltaMeu += Double.parseDouble(valorFaltaMeu);
        }
        TextView tvRFaltaMeu = (TextView) activity.findViewById(R.id.tvFaltaMeu);
        tvRFaltaMeu.setText("Falta R$" + String.format("%.2f", totalFaltaMeu));

        // patty

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria='Patty' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorPatty = cursor.getString(cursor.getColumnIndex(VALOR));
            valorPatty = valorPatty.replaceAll(",", "."); // troca a , por .
            totalPatty += Double.parseDouble(valorPatty);
        }

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND conta LIKE 'Patty%' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorPatty = cursor.getString(cursor.getColumnIndex(VALOR));
            valorPatty = valorPatty.replaceAll(",", "."); // troca a , por .
            totalPatty += Double.parseDouble(valorPatty);
        }
        TextView tvPatty = (TextView) activity.findViewById(R.id.tvPatty);
        tvPatty.setText("Patty R$" + String.format("%.2f", totalPatty));
        if (totalPatty == 0) {
            tvPatty.setVisibility(View.GONE);
        } else {
            tvPatty.setVisibility(View.VISIBLE);
        }

        // mae

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria='Mae' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorMae = cursor.getString(cursor.getColumnIndex(VALOR));
            valorMae = valorMae.replaceAll(",", "."); // troca a , por .
            totalMae += Double.parseDouble(valorMae);
        }

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND conta LIKE 'Mae%' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorMae = cursor.getString(cursor.getColumnIndex(VALOR));
            valorMae = valorMae.replaceAll(",", "."); // troca a , por .
            totalMae += Double.parseDouble(valorMae);
        }
        TextView tvMae = (TextView) activity.findViewById(R.id.tvMae);
        tvMae.setText("Mae R$" + String.format("%.2f", totalMae));
        if (totalMae == 0) {
            tvMae.setVisibility(View.GONE);
        } else {
            tvMae.setVisibility(View.VISIBLE);
        }
        // bobeira

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria = 'Outros' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorLazer = cursor.getString(cursor.getColumnIndex(VALOR));
            valorLazer = valorLazer.replaceAll(",", "."); // troca a , por .
            totalLazer += Double.parseDouble(valorLazer);
        }
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria = 'Lazer' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorLazer = cursor.getString(cursor.getColumnIndex(VALOR));
            valorLazer = valorLazer.replaceAll(",", "."); // troca a , por .
            totalLazer += Double.parseDouble(valorLazer);
        }

        TextView tvbobeira = (TextView) activity.findViewById(R.id.tvBobeira);
        tvbobeira.setText("Bobeira R$" + String.format("%.2f", totalLazer));
        if (totalLazer == 0) {
            tvbobeira.setVisibility(View.GONE);
        } else {
            tvbobeira.setVisibility(View.VISIBLE);
        }
        // Chris

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria = 'Chris' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorChris = cursor.getString(cursor.getColumnIndex(VALOR));
            valorChris = valorChris.replaceAll(",", "."); // troca a , por .
            totalChris += Double.parseDouble(valorChris);
        }
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND conta like 'Chris%' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorChris = cursor.getString(cursor.getColumnIndex(VALOR));
            valorChris = valorChris.replaceAll(",", "."); // troca a , por .
            totalChris += Double.parseDouble(valorChris);
        }

        TextView tvChris = (TextView) activity.findViewById(R.id.tvChris);
        tvChris.setText("Chris R$" + String.format("%.2f", totalChris));
        if (totalChris == 0) {
            tvChris.setVisibility(View.GONE);
        } else {
            tvbobeira.setVisibility(View.VISIBLE);
        }

        // saldo
        totalSaldo = totalReceber - totalPagar;
        TextView tvRSaldo = (TextView) activity.findViewById(R.id.tvSaldo);
        tvRSaldo.setText(String.format("%.2f", totalSaldo));

        FechaBanco();

    }

    /*
    TODO RETORNA DIA
     */
    public int RetornaDia() {

        Calendar _calendar = Calendar.getInstance(Locale.getDefault());
        return _calendar.get(Calendar.DAY_OF_MONTH);
    }

    /*
    TODO RETORNA MES
     */
    public int RetornaMes() {

        Calendar _calendar = Calendar.getInstance(Locale.getDefault());
        return _calendar.get(Calendar.MONTH) + 1;
    }

    /*
    TODO RETORNA ANO
     */
    public int RetornaAno() {

        Calendar _calendar = Calendar.getInstance(Locale.getDefault());
        return _calendar.get(Calendar.YEAR);
    }


    /*
    TODO CRIA TABELA DE CARTOES
     */
    public void CriatabelaCategoria(Activity activity) {
        File arq;
        FileOutputStream fos;
        byte[] dados;
        try {
            String importaSQL = "";
            importaSQL = importaSQL + "Insert into Categorias (categorias) values ('Alimentacao'); ";
            importaSQL = importaSQL + "\n Insert into Categorias (categorias) values ('Moradia'); ";
            importaSQL = importaSQL + "\n Insert into Categorias (categorias) values ('Educacao'); ";
            importaSQL = importaSQL + "\n Insert into Categorias (categorias) values ('Lazer'); ";
            importaSQL = importaSQL + "\n Insert into Categorias (categorias) values ('Saude'); ";
            importaSQL = importaSQL + "\n Insert into Categorias (categorias) values ('Outros'); ";


            arq = new File(Environment.getExternalStorageDirectory(), "/Download/bancoCategorias.txt");
            fos = new FileOutputStream(arq.toString());

            // transforma o texto digitado em array de bytes
            dados = importaSQL.getBytes();
            // escreve os dados e fecha o arquivo
            fos.write(dados);
            fos.flush();
            fos.close();
            // enviarEmail(activity);
        } catch (IOException e) {
            //toast("Exportação com erro! ", e);
            // trace("Erro : "; e.getMessage());
        }
    }

    public void importarListaCategoria(Activity activity) {
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "/Download/bancoCategoria.txt");
            AbreBanco(activity);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                Log.i("sql", "" + line);
                String sql = line;
                bancoDados.execSQL(sql);
            }
            toast(activity, "Dados Importados com Sucesso!");
            file.delete();
        } catch (IOException e) {
            toast(activity, "Ocorreu um erro ao importar os dados: " + e);
            Log.i("aviso", "ERRO IMPORTAR:: " + e);
        }

        FechaBanco();
    }


    public void toast(Activity activity, String aviso) {
        Toast.makeText(activity, aviso, Toast.LENGTH_SHORT).show();
    }
    //

}

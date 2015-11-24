package com.alpha.quiztomizador.controles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.controles.CadastrosActivity;
import com.alpha.quiztomizador.controles.CompartilharActivity;
import com.alpha.quiztomizador.controles.OpcoesActivity;
import com.alpha.quiztomizador.controles.QuizActivity;
import com.alpha.quiztomizador.controles.ResultadosActivity;
import com.alpha.quiztomizador.util.WebService;

public class PrincipalActivity  extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        WebService.sincronizarDados(this);
    }


    public void onClickCadastros (View v) {
        Intent i = new Intent(getApplicationContext(),CadastrosActivity.class);
        startActivity(i);
    }

    public void onClickCompartilhar (View v) {
        Intent i = new Intent(getApplicationContext(),CompartilharActivity.class);
        startActivity(i);
    }

    public void onClickQuiz (View v) {
        Intent i = new Intent(getApplicationContext(),QuizActivity.class);
        startActivity(i);
    }

    public void onClickResultados(View v) {
        Intent i = new Intent(getApplicationContext(),ResultadosActivity.class);
        startActivity(i);
    }

    public void onClickOpcoes(View v) {
        Intent i = new Intent(getApplicationContext(),OpcoesActivity.class);
        startActivityForResult(i, 1);
    }

    public  void onClickSair (View v) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(getString(R.string.dialogo_aviso));
        dialogo.setMessage(getString(R.string.dialogo_sair));
        dialogo.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int arg) {
                        finish();
                    }
                });
        dialogo.setNegativeButton("Não", null);
        dialogo.show();

    }

    // perguntar se quer terminar o aplicativo caso o usuário pressione o botão voltar do celular
    @Override
    public void onBackPressed() {
        onClickSair(getCurrentFocus());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // fecha a activity se a tela de login for chamada
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_FIRST_USER){
                finish();
            }
        }
    }
}

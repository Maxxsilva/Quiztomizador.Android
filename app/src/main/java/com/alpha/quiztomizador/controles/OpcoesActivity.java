package com.alpha.quiztomizador.controles;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.View;

import com.alpha.quiztomizador.R;

/**
 * Created by Usuario on 25/09/2015.
 */
public class OpcoesActivity extends PreferenceActivity {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment() {
                @Override
                public void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    addPreferencesFromResource(R.xml.opcoes);
                }
            }).commit();
        }

        public void mudarUsuario(View v) {
            // retorna um valor pra tela principal
            Intent retornoIntent = new Intent(getApplicationContext(),PrincipalActivity.class);
            setResult(RESULT_FIRST_USER, retornoIntent);
            onBackPressed();
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        }

}

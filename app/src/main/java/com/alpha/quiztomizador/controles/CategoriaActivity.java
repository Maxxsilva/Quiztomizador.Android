package com.alpha.quiztomizador.controles;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.modelos.Categoria;
import com.alpha.quiztomizador.modelos.Usuario;
import com.alpha.quiztomizador.util.GenericJSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class CategoriaActivity extends Activity {

    private Categoria categoria;
    private TextView mDescricao;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_categoria);
         Long id = (Long)getIntent().getSerializableExtra("categoriaEdicao");
         categoria = Categoria.procurar("id ="+id);
         mDescricao = (TextView) this.findViewById(R.id.txt_descricao);
         //modo edição
         if (categoria != null) {
             mDescricao.setText(categoria.getDescricao());
         } else {
             categoria = new Categoria();
         }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar_cadastro, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.salvar:
                validarCampos();
              return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void validarCampos() {
        mDescricao.setError(null);
        View focusView = null;
        boolean cancela = false;
        if (mDescricao.getText().toString().isEmpty()) {
            mDescricao.setError(getString(R.string.categoria_erro_obrigatorio));
            focusView = mDescricao;
            cancela = true;
        }
        if (cancela) {
            focusView.requestFocus();
        } else {
            Toast.makeText(getApplicationContext(), "Salvando...", Toast.LENGTH_LONG).show();
          //  Long id = PreferenceManager.getDefaultSharedPreferences(this).getLong("logado", -1);
            categoria.setUsuario(Usuario.getLogado());
            categoria.setDescricao(mDescricao.getText().toString());
            categoria.salvar();
        }
    }

}


//// teste de JSON
//                Gson gson = new GsonBuilder().create();
//                Log.e("GENERICJSON", GenericJSON.toJSON(categoria));
//                Categoria nova = GenericJSON.fromJSON("{\"mId\":123456,\"categoria\":\"\",\"nome\":\"categoria xyz\"}", Categoria.class);
//                Log.e("JSON 2 OBJ" , nova.getId().toString()+nova.toString());
//                nova.salvar();

package com.alpha.quiztomizador.controles;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.modelos.Categoria;
import com.alpha.quiztomizador.modelos.Questionario;
import com.alpha.quiztomizador.modelos.Usuario;
import com.alpha.quiztomizador.util.GenericJSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class QuestionarioActivity extends Activity {

    private Questionario questionario;
    private List<Categoria> listaCategorias = new ArrayList<Categoria>();
    private TextView mDescricao;
    private Spinner mCategorias;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_questionario);

         listaCategorias.add(new Categoria());  // insere 1 na lista em branco
         // listagem de categorias do usuário não excluida
         listaCategorias.addAll(Categoria.listar("excluido=0"));

         mCategorias = (Spinner) findViewById(R.id.cmb_categorias);
         ArrayAdapter<Categoria> adapter = new ArrayAdapter<Categoria>(this, android.R.layout.simple_spinner_item, listaCategorias) {
             @Override
             public View getView(int position, View convertView, ViewGroup parent) {
                 View view = super.getView(position, convertView, parent);
                 TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                 text1.setText(listaCategorias.get(position).getDescricao());
                 return view;
             }
         };
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
         mCategorias.setAdapter(adapter);

         Long id = (Long)getIntent().getSerializableExtra("questionarioEdicao");
         questionario = Questionario.procurar("id="+id);

         mDescricao = (TextView) this.findViewById(R.id.txt_descricao);

         //modo edição
         if (questionario != null) {
             mDescricao.setText(questionario.getDescricao());
             int posicao = listaCategorias.indexOf(questionario.getCategoria());
             mCategorias.setSelection(posicao);
         } else {
             questionario = new Questionario();
         }

     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar_cadastro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            mDescricao.setError(getString(R.string.questionario_erro_obrigatorio));
            focusView = mDescricao;
            cancela = true;
        }
        if (cancela) {
            focusView.requestFocus();
        } else {
            Toast.makeText(getApplicationContext(), "Salvando...", Toast.LENGTH_LONG).show();
           // Long id = PreferenceManager.getDefaultSharedPreferences(this).getLong("logado", -1);
            questionario.setUsuario(Usuario.getLogado());
            questionario.setDescricao(mDescricao.getText().toString());
            Categoria categoria = (Categoria)mCategorias.getSelectedItem();
            if (categoria.toString()==null){ // se a categoria selecionada for vazia
                questionario.setCategoria(null);
            } else {
                questionario.setCategoria(categoria);
            }
            questionario.salvar();

        }
    }

}


/// teste de json
//                String json = GenericJSON.toJSON(questionario);
//                Log.e("OBJ 2 JSON ", json);
//
//                Questionario novo = GenericJSON.fromJSON(json, Questionario.class);
//
//                Log.e("JSON 2 OBJ ", novo.toString());
//
//                String str = "{\"categoria\":{\"descricao\":\"bbbb\",\"nome\":\"portugues\",\"questionarios\":null,\"mId\":1443063199423},\"descricao\":null,\"nome\":\"teste de caligrafia\",\"questoes\":null,\"mId\":1234568789}";
//
//               // str = "{\"mId\":1443114875623,\"nome\":\"teste de matematica";
//                Questionario novo2 = GenericJSON.fromJSON(str, Questionario.class);
//
//                Log.e("JSONGeneric", novo2.toString());
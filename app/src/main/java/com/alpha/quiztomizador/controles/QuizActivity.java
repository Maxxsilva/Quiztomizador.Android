package com.alpha.quiztomizador.controles;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.modelos.Categoria;
import com.alpha.quiztomizador.modelos.Questao;
import com.alpha.quiztomizador.modelos.Questionario;
import com.alpha.quiztomizador.util.GenericJSON;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends Activity implements ActionBar.TabListener {

    private List<Categoria> listaCategorias = new ArrayList<Categoria>();
    private List<Questionario> listaQuestionarios = new ArrayList<Questionario>();
    private ArrayAdapter<Questionario> adapterQuestionario;
    private ArrayAdapter<Categoria> adapterCategoria;

    private ListView listview;
    private TextView rodape;

    private String[] tabs = {"Questionários", "Categorias"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        rodape = new TextView(getApplicationContext());
        rodape.setTextSize(30f);
        listview = (ListView) findViewById(R.id.listagem);
        listview.addFooterView(rodape);
        // gera as listas
        listarQuestionarios();
        listarCategorias();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar = getActionBar();

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

    }

    public void listarQuestionarios() {

        listaQuestionarios.clear();
        listaQuestionarios = Questionario.listar("excluido = 0");

        adapterQuestionario = new ArrayAdapter<Questionario>(this, android.R.layout.simple_list_item_2, android.R.id.text1, listaQuestionarios) {
            // Sobrescrevendo o método para definir os campos de entrada
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setText(listaQuestionarios.get(position).getDescricao());
                int tot = listaQuestionarios.get(position).getQuestoes().size();
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setText("Questões: " + Integer.toString(tot));
                return view;
            }

        };
    }

    public void listarCategorias() {
        listaCategorias.clear();
        listaCategorias = Categoria.listar("excluido = 0");

        adapterCategoria = new ArrayAdapter<Categoria>(this, android.R.layout.simple_list_item_2, android.R.id.text1, listaCategorias) {
            // Sobrescrevendo o método para definir os campos de entrada
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setText(listaCategorias.get(position).getDescricao());

                int tot = Questao.listarPorCategoria(listaCategorias.get(position).getId()).size();

                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setText("Questões: " + Integer.toString(tot));
                return view;
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar_quiz, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.pesquisar).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String s) {

                if (s.length() >= 4) {
                    Toast.makeText(getApplicationContext(), "Procura " + s, Toast.LENGTH_LONG).show();
                    //submitLocationQuery(s);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // submitLocationQuery(query);
                Toast.makeText(getApplicationContext(), "Procura tudo " + query, Toast.LENGTH_LONG).show();

                searchView.clearFocus();
                // esconder a caixa de texto de busca
                ///searchView.onActionViewCollapsed();
                return true;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.pesquisar:
                Toast.makeText(getApplicationContext(), "Localizar", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        if (tab.getPosition() == 0) {
            listview.setAdapter(adapterQuestionario);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    //Pega o item que foi selecionado.
                    Questionario item = (Questionario) parent.getItemAtPosition(position);
                    int total = listaQuestionarios.get(position).getQuestoes().size();
                    if (total == 0) {
                        exibirMensagem();
                    } else {
                        Intent i = new Intent(getApplicationContext(), TesteActivity.class);
                        i.putExtra("questionario", item.getId());
                        startActivity(i);
                    }
                }
            });
        }
        if (tab.getPosition() == 1) {
            listview.setAdapter(adapterCategoria);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    //Pega o item que foi selecionado.
                    Categoria item = (Categoria) parent.getItemAtPosition(position);
                    int total = Questao.listarPorCategoria(listaCategorias.get(position).getId()).size();
                    if (total == 0) {
                        exibirMensagem();
                    } else {
                        Intent i = new Intent(getApplicationContext(), TesteActivity.class);
                        i.putExtra("categoria", item.getId());
                        startActivity(i);
                    }


                }

            });
        }
        /// total do rodapé
        if (rodape != null) rodape.setText("Total: " + (listview.getCount() - 1));
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }


    public void exibirMensagem() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Alerta");
        dialogo.setMessage("Não é possível realizar o Teste!");
        dialogo.setNeutralButton("OK", null);
        dialogo.show();
    }

}
package com.alpha.quiztomizador.controles;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.modelos.Categoria;
import com.alpha.quiztomizador.modelos.Questionario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompartilharActivity extends Activity implements ActionBar.TabListener {

    private List<Categoria> listaCategorias = new ArrayList<Categoria>();
    private List<Questionario> listaQuestionarios = new ArrayList<Questionario>();
    private List<Questionario> meusQuestionarios = new ArrayList<Questionario>();


    private ExpandableListView listview;


    private String[] abas = { "Categorizados", "Questionarios"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartilhar);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : abas) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        listview = (ExpandableListView) findViewById(R.id.lst_resultados);


    }

//    @Override
//    public void onResume() {  // After a pause OR at startup
//        super.onResume();
//        //atualizar Listas
//        onTabSelected(getActionBar().getSelectedTab(), null);
//    }



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

            listaCategorias.clear();
            listaCategorias = Categoria.listar();

            HashMap<String, List<Questionario>> agrupado = new HashMap<String, List<Questionario>>();
            for (Categoria c: listaCategorias) {
                agrupado.put(c.getDescricao().toString(), c.getQuestionarios());
            }
//            List groupList = new ArrayList<String>();
//            groupList.add("portugues");
//            groupList.add("matematica");
            List listaP = new ArrayList<String>();
            listaP.add("teste portugues@82");
            listaP.add("prova portugues@63");
            listaP.add("trabalho portugues@40");
            List listaM = new ArrayList<String>();
            listaM.add("teste matematica@130");
            listaM.add("prova matematica@78");
            HashMap<String, List<String>> childList = new HashMap<String, List<String>>();

            childList.put("portugues", listaP);
            childList.put("matematica", listaM);


            listview = (ExpandableListView) findViewById(R.id.lst_resultados);
            final AgrupadoAdapter expListAdapter = new AgrupadoAdapter(
                    this, childList);
            listview.setAdapter(expListAdapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {

                    //Pega o item que foi selecionado.
                    Categoria item = (Categoria) parent.getItemAtPosition(position);

                    //Demostração
                    Toast.makeText(getApplicationContext(), "SELECIONADO " + item.getDescricao(), Toast.LENGTH_LONG).show();

                }

            });

        }
        if (tab.getPosition() == 1) {


            meusQuestionarios.clear();
            meusQuestionarios = Questionario.listar();
            // adicionando para teste
            meusQuestionarios.add(new Questionario("meu questionario1@20",null));
            meusQuestionarios.add(new Questionario( "meu questionario2@45", null));
            meusQuestionarios.add(new Questionario( "meu questionario3@18", null));


            listaQuestionarios.clear();
            listaQuestionarios = Questionario.listar();

            // adcionado para teste
            listaQuestionarios.add(new Questionario("compartilhado questionario x@23",null));
            listaQuestionarios.add(new Questionario("compartilhado questionario y@39", null));
            listaQuestionarios.add(new Questionario("compartilhado questionario z@65", null));


            HashMap<String, List<Questionario>> agrupado = new HashMap<String, List<Questionario>>();
            agrupado.put("Meus Questionarios", meusQuestionarios);
            agrupado.put("Compartilhados", listaQuestionarios);
            final AgrupadoAdapter expListAdapter = new AgrupadoAdapter(
                    this, agrupado);
            listview.setAdapter(expListAdapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {

                    //Pega o item que foi selecionado.
                    Questionario item = (Questionario) parent.getItemAtPosition(position);

                    //Demostração
                    Toast.makeText(getApplicationContext(), "BAIXANDO... " + item.getDescricao(), Toast.LENGTH_LONG).show();

                }

            });

        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }



}
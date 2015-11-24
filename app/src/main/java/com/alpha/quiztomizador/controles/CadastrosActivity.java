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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.modelos.Categoria;
import com.alpha.quiztomizador.modelos.Questao;
import com.alpha.quiztomizador.modelos.Questionario;

import java.util.ArrayList;
import java.util.List;

public class CadastrosActivity extends Activity implements ActionBar.TabListener {

    private List<Categoria> listaCategorias = new ArrayList<Categoria>();
    private List<Questionario> listaQuestionarios = new ArrayList<Questionario>();
    private ArrayAdapter<Questionario> adapterQuestionario;
    private ArrayAdapter<Categoria> adapterCategoria;

    private ListView listView;
    private TextView rodape;

    private String[] tabs = {"Questionários", "Categorias"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastros);

        rodape = new TextView(getApplicationContext());
        rodape.setTextSize(30f);
        listView = (ListView) findViewById(R.id.listagem);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.addFooterView(rodape);

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

    //  inclusão da barra de açao
    public void incluir(View v) {
        if (getActionBar().getSelectedTab().getText().equals("Questionários")) {
            Intent i = new Intent(getApplicationContext(), QuestionarioActivity.class);
            startActivity(i);
        }
        if (getActionBar().getSelectedTab().getText().equals("Categorias")) {
            Intent i = new Intent(getApplicationContext(), CategoriaActivity.class);
            startActivity(i);
        }
    }

    public void listarQuestionarios() {
        listaQuestionarios.clear();
        listaQuestionarios = Questionario.listar("excluido = 0");

        adapterQuestionario = new ArrayAdapter<Questionario>(this, R.layout.lista_quiz, R.id.txt1, listaQuestionarios) {
            // Sobrescrevendo o método para definir os campos de entrada
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(R.id.txt1);
                TextView text2 = (TextView) view.findViewById(R.id.txt2);

                text1.setText(listaQuestionarios.get(position).getDescricao());
                int tot = listaQuestionarios.get(position).getQuestoes().size();
                text2.setText("Questões: "+Integer.toString(tot));

                return view;
            }
        };
    }


    public void listarCategorias() {
        listaCategorias.clear();
        listaCategorias = Categoria.listar("excluido = 0");

        adapterCategoria = new ArrayAdapter<Categoria>(this, R.layout.lista_quiz, R.id.txt1, listaCategorias) {
            // Sobrescrevendo o método para definir os campos de entrada
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(R.id.txt1);
                TextView text2 = (TextView) view.findViewById(R.id.txt2);

                text1.setText(listaCategorias.get(position).getDescricao());
                int tot = listaCategorias.get(position).getQuestionarios().size();
                text2.setText("Questionários: "+Integer.toString(tot));
                return view;
            }
        };

    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        //atualizar Listas
        onTabSelected(getActionBar().getSelectedTab(), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar_principal, menu);
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
            case R.id.adicionar:
                incluir(getCurrentFocus());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        if (tab.getPosition() == 0) {

            listView.setAdapter(adapterQuestionario);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    //Pega o item que foi selecionado.
                    Questionario item = (Questionario) parent.getItemAtPosition(position);

                    Intent i = new Intent(getApplicationContext(), QuestoesActivity.class);
                    i.putExtra("questionarioEdicao", item.getId());
                    startActivity(i);
                }
            });
        }
        if (tab.getPosition() == 1) {
            listView.setAdapter(adapterCategoria);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    //Pega o item que foi selecionado.
                    Categoria item = (Categoria) parent.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(), "SELECIONADO " + item.getDescricao(), Toast.LENGTH_LONG).show();
                }
            });
        }
        /// total do rodapé
        if (rodape != null) rodape.setText("Total: " + (listView.getCount() - 1));
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public void onClickMenu(final View v) {

        final View parentRow = (View) v.getParent();
        ImageView img = (ImageView) parentRow.findViewById(R.id.imgMenu);
        // ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);

        final String selecionada = getActionBar().getSelectedTab().getText().toString();

        PopupMenu popup = new PopupMenu(this, img);
        popup.getMenu().add("Editar");
        popup.getMenu().add("Apagar");
        if (selecionada.equals("Questionários")) {
            popup.getMenu().add("Incluir Questão");
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().equals("Apagar")) {
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(v.getContext());
                    dialogo.setTitle("Aviso");
                    dialogo.setMessage("Deseja excluir?");
                    dialogo.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface di, int arg) {
                                    if (selecionada.equals("Questionários")) {
                                        Questionario obj = listaQuestionarios.get(position);
                                        adapterQuestionario.getItem(position).excluir();
                                        listaQuestionarios.remove(obj); // remove da lista caso seja excluido=true
                                        adapterQuestionario.notifyDataSetChanged();
                                    }
                                    if (selecionada.equals("Categorias")) {
                                        Categoria obj = listaCategorias.get(position);
                                        adapterCategoria.getItem(position).excluir();
                                        listaCategorias.remove(obj); // remove da lista caso seja excluido=true
                                        adapterCategoria.notifyDataSetChanged();
                                    }
                                    //atualizar Listas
                                   // onTabSelected(getActionBar().getSelectedTab(),null);
                                }
                            });
                    dialogo.setNegativeButton("Não", null);
                    dialogo.show();
                }

                if (item.getTitle().equals("Editar")) {
                    if (selecionada.equals("Questionários")) {
                        Intent i = new Intent(getApplicationContext(), QuestionarioActivity.class);
                        Questionario obj = listaQuestionarios.get(position);
                        i.putExtra("questionarioEdicao", obj.getId());
                        startActivity(i);
                    }
                    if (selecionada.equals("Categorias")) {
                        Intent i = new Intent(getApplicationContext(), CategoriaActivity.class);
                        Categoria obj = listaCategorias.get(position);
                        i.putExtra("categoriaEdicao", obj.getId());
                        startActivity(i);
                    }
                }
                if (item.getTitle().equals("Incluir Questão")) {
                    Intent i = new Intent(getApplicationContext(), QuestaoActivity.class);
                    Questionario obj = (Questionario) listaQuestionarios.get(position);
                    i.putExtra("questionarioEdicao", obj.getId());
                    startActivity(i);
                }
                return true;
            }
        });

        popup.show();
    }


}
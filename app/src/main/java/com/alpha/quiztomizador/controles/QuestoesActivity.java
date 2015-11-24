package com.alpha.quiztomizador.controles;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.alpha.quiztomizador.modelos.Questao;
import com.alpha.quiztomizador.modelos.Questionario;

import java.util.ArrayList;
import java.util.List;

public class QuestoesActivity extends Activity {

    private Questionario questionario;
    private List<Questao> listaQuestoes = new ArrayList<Questao>();
    private ListView listview;
    private TextView rodape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questoes);
        rodape = new TextView(getApplicationContext());
        rodape.setTextSize(30f);
        listview = (ListView) findViewById(R.id.lst_resultados);
        listview.addFooterView(rodape);

        Long id = (Long) getIntent().getSerializableExtra("questionarioEdicao");
        if (id == null) {
            id = PreferenceManager.getDefaultSharedPreferences(this).getLong("questionario", -1);
        } else {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putLong("questionario",id).commit();
        }
        questionario = Questionario.procurar("id="+id);
        atualizarLista();
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
                incluir();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void incluir() {
        Intent i = new Intent(getApplicationContext(), QuestaoActivity.class);
        Bundle extras = new Bundle();
        extras.putLong("questionarioEdicao", questionario.getId());
        i.putExtras(extras);
        startActivity(i);

    }

     public void atualizarLista() {

         listaQuestoes.clear();
         listaQuestoes = questionario.getQuestoes();

         listview = (ListView) findViewById(R.id.lst_resultados);

            final ArrayAdapter<Questao> adapter = new ArrayAdapter<Questao>(this, R.layout.lista_quiz, R.id.txt1, listaQuestoes) {
                // Sobrescrevendo o método para definir os campos de entrada
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(R.id.txt1);
                    TextView text2 = (TextView) view.findViewById(R.id.txt2);

                    text1.setText(listaQuestoes.get(position).getTitulo().toString());
                    text2.setText(listaQuestoes.get(position).getTipo().toString());
                    return view;
                }

            };

            listview.setAdapter(adapter);

         listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

             @Override
             public void onItemClick(AdapterView<?> parent, final View view,
                                     int position, long id) {
                 //Pega o item que foi selecionado.
                 editar(position);
             }

         });

            /// total do rodapé
            if (rodape != null) rodape.setText("Total: " + (listview.getCount() - 1));

        }


    public void onClickMenu(View v) {

        View parentRow = (View) v.getParent();
        ImageView img = (ImageView) parentRow.findViewById(R.id.imgMenu);
        // ListView listView = (ListView) parentRow.getParent();
        final int position = listview.getPositionForView(parentRow);

        PopupMenu popup = new PopupMenu(this, img); //parentrow
        popup.getMenu().add("Editar");
        popup.getMenu().add("Apagar");
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Apagar")) {
                    apagar(position);
                }
                if (item.getTitle().equals("Editar")) {
                    editar(position);
                }
                return true;
            }
        });
        popup.show();
    }

    public void editar(int pos) {
        Intent i = new Intent(getApplicationContext(), QuestaoActivity.class);
        Questao obj = listaQuestoes.get(pos);
        Bundle extras = new Bundle();
        extras.putLong("questionarioEdicao", obj.getQuestionario().getId());
        extras.putLong("questaoEdicao", obj.getId());
        i.putExtras(extras);
        startActivity(i);
    }

    public void apagar(final int pos) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Aviso");
        dialogo.setMessage("Deseja excluir?");
        dialogo.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int arg) {
                        Questao obj = listaQuestoes.get(pos);
                        obj.excluir();

                        atualizarLista();
                    }
                });
        dialogo.setNegativeButton("Não", null);
        dialogo.show();
    }

}
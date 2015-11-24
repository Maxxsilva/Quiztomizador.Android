package com.alpha.quiztomizador.controles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.modelos.Alternativa;
import com.alpha.quiztomizador.modelos.Questao;
import com.alpha.quiztomizador.modelos.Questionario;
import com.alpha.quiztomizador.modelos.Tipo;

import java.util.ArrayList;
import java.util.List;

public class QuestaoActivity extends Activity {

    private Questionario questionario;
    private Questao questao;
    private TextView titulo;
    private Spinner tipoQuestao;
    private Spinner verdadeirofalso;
    private View viewObjetiva;
    private View viewBooleana;
    private ListView lvAlternativas;
    private ArrayAdapter<Alternativa> alternativasAdapter;
    private List<Alternativa> listaAlternativas = new ArrayList<Alternativa>();

     @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_questao);
         titulo = (TextView)findViewById(R.id.titulo_questao);
         viewObjetiva = (View)findViewById(R.id.tipoObjetiva);
         viewBooleana = (View)findViewById(R.id.tipoBooleana);
         tipoQuestao = (Spinner)findViewById(R.id.spn_filtro);
         Bundle extras = getIntent().getExtras();
         Long id1 = extras.getLong("questionarioEdicao");
         questionario = Questionario.procurar("id ="+id1);
         Long id2 = extras.getLong("questaoEdicao");
         questao = Questao.procurar(id2);
         //modo edição
         if (questao != null) {
             titulo.setText(questao.getTitulo());
             tipoQuestao.setSelection(questao.getTipo().ordinal());
             listaAlternativas = questao.getAlternativas();
         } else {
             questao = new Questao("",Tipo.Objetiva);
             questao.setQuestionario(questionario);
         }
         if (listaAlternativas == null) {
             listaAlternativas = new ArrayList<Alternativa>();
         }
         tipoQuestao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 viewObjetiva.setVisibility(View.GONE);
                 viewBooleana.setVisibility(View.GONE);
                 if (parent.getSelectedItem().equals("Objetiva")) {
                     viewObjetiva.setVisibility(View.VISIBLE);
                     exibirAlternativaObjetiva();
                 } else if (parent.getSelectedItem().equals("Verdadeiro_Falso")) {
                     viewBooleana.setVisibility(View.VISIBLE);
                     exibirAlternativaBooleana();
                 }
             }
             @Override
             public void onNothingSelected(AdapterView<?> parent) {
             }
         });
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
                Toast.makeText(getApplicationContext(), "Salvando...", Toast.LENGTH_LONG).show();

                questao.setTitulo(titulo.getText().toString());
                Tipo t = Tipo.values()[tipoQuestao.getSelectedItemPosition()];
                questao.setTipo(t);
                questao.salvar();

                if (t.toString().equals("Verdadeiro_Falso")) {
                    int posicao1 = verdadeirofalso.getSelectedItemPosition();
                    int posicao2 = ((posicao1+1) % 2);  // soma+1 pq divisao por zero o resto é sempre 0
                    if (listaAlternativas.size()>0) {
                        // alteração apenas da marcação
                        listaAlternativas.get(posicao1).setCerta(true);
                        listaAlternativas.get(posicao2).setCerta(false);
                    }else {
                        listaAlternativas.add(new Alternativa(questao, verdadeirofalso.getItemAtPosition(posicao1).toString(),true));
                        listaAlternativas.add(new Alternativa(questao, verdadeirofalso.getItemAtPosition(posicao2).toString(),false));
                    }
                }
                // salva as alternativas alteradas e novas
                for (Alternativa alt : listaAlternativas) {
                    alt.salvar();
                }
                // exclui as alternativas do banco que foram deletadas
                for (Alternativa alt_anterior: questao.getAlternativas()) {
                    if (listaAlternativas.indexOf(alt_anterior)<0) {
                        alt_anterior.excluir();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void exibirAlternativaBooleana() {
        verdadeirofalso = (Spinner)findViewById(R.id.verdadeiro_falso);
        int selection = 0;
        if (listaAlternativas.size() > 0) {
            // verifica qual alternativa foi marcada 0=verdadeiro ou 1 =falso
            int marcada = listaAlternativas.get(0).getCerta() ? 0 : 1;
            selection = ((ArrayAdapter<String>) verdadeirofalso.getAdapter()).getPosition(listaAlternativas.get(marcada).getAlternativa());
        }
        verdadeirofalso.setSelection(selection);
    }

    public void exibirAlternativaObjetiva() {
        lvAlternativas = (ListView)findViewById(R.id.listaAlternativas);
        alternativasAdapter = new ArrayAdapter<Alternativa>(this, R.layout.lista_alternativas, R.id.texto1, listaAlternativas) {
            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                final TextView texto = (TextView) view.findViewById(R.id.texto1);
                CheckBox check = (CheckBox) view.findViewById(R.id.testeSelecao);
                check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        try {
                            alternativasAdapter.getItem(position).setCerta(isChecked);
                        } catch (Exception e) {
                            // exceções tipo valor nulo ou indice invalido
                        }
                    }
                });
                try {
                    texto.setText(alternativasAdapter.getItem(position).getAlternativa());
                    check.setChecked(alternativasAdapter.getItem(position).getCerta());
                } catch (Exception e) {
                    // ignora caso seja nulo
                }
                return view;
            }
        };
        lvAlternativas.setAdapter(alternativasAdapter);
        lvAlternativas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editarAlternativa(position);
            }
        });
    }

    public  void onClickAlternativaIncluir(View v) {
        // maximo de alternativas = 5
        if (listaAlternativas.size()<5) {
            lvAlternativas.clearFocus();
            editarAlternativa(-1);
        } else {
            Toast.makeText(getApplicationContext(), "Maximo de 5 alternativas!", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickAlternativaExcluir(View v) {
        View parentRow = (View) v.getParent();
        int position = lvAlternativas.getPositionForView(parentRow);
        alternativasAdapter.remove(alternativasAdapter.getItem(position));
        alternativasAdapter.notifyDataSetChanged();
    }

    public void editarAlternativa(final int pos) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Alternativa");
        dialogo.setMessage("Digite a alternativa:");
        final EditText input = new EditText(this);
        if (pos >=0 ) {
            input.setText(alternativasAdapter.getItem(pos).getAlternativa());
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        dialogo.setView(input);
        dialogo.setIcon(android.R.drawable.ic_menu_edit);
        dialogo.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int arg) {
                        Toast.makeText(getApplicationContext(),
                                "Alternativa editada", Toast.LENGTH_SHORT).show();
                        if (pos >=0 ) {
                            alternativasAdapter.getItem(pos).setAlternativa(input.getText().toString());
                        } else {
                            alternativasAdapter.add(new Alternativa(questao, input.getText().toString(), false));
                        }
                        alternativasAdapter.notifyDataSetChanged();
                    }
                });
        dialogo.setNegativeButton("Cancelar", null);
        dialogo.show();
    }

}
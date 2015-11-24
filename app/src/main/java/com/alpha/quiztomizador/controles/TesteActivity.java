package com.alpha.quiztomizador.controles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.modelos.Alternativa;
import com.alpha.quiztomizador.modelos.Categoria;
import com.alpha.quiztomizador.modelos.Questao;
import com.alpha.quiztomizador.modelos.Questionario;
import com.alpha.quiztomizador.modelos.Teste;
import com.alpha.quiztomizador.modelos.Usuario;
import com.alpha.quiztomizador.util.WebService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Usuario on 25/09/2015.
 */
public class TesteActivity extends Activity {

    private long tempoInicial = 0L;
    private long tempoDecorrido = 0L;
    private Handler customHandler = new Handler();

    private int posicao = 0;
    private int erros = 0;
    private int acertos = 0;

    private TextView mTitulo;
    private TextView mRelogio;
    private ListView listview;
    private Teste teste;
    private List<Questionario> listaQuestionarios = new ArrayList<Questionario>();
    private List<Questao> listaQuestoes = new ArrayList<Questao>();
    private List<Alternativa> listaAlternativas = new ArrayList<Alternativa>();
    private List<Boolean> listaRespostas =  new ArrayList<Boolean>();;
    private ArrayAdapter<Alternativa> adapterAlternativa;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_teste);

            mTitulo = (TextView)findViewById(R.id.testeTitulo);
            mRelogio = (TextView)findViewById(R.id.testeRelogio);
            tempoInicial = SystemClock.uptimeMillis();
            customHandler.postDelayed(atualizaTempo, 0);

            listview = (ListView) findViewById(R.id.testeAlternativas);
            String descricao = null;
            Long id = null;
            if (getIntent().hasExtra("questionario")) {
                id = (Long)getIntent().getSerializableExtra("questionario");
                listaQuestionarios.add(Questionario.procurar("id="+id));
                listaQuestoes = Questao.listarPorQuestionario(id);
                descricao = "Questionário: "+listaQuestionarios.get(0).getDescricao();

            }else if (getIntent().hasExtra("categoria"))  {
                id = (Long)getIntent().getSerializableExtra("categoria");
                listaQuestionarios = Questionario.listarPorCategoria(id);
                listaQuestoes = Questao.listarPorCategoria(id);
                descricao = "Categoria: "+listaQuestionarios.get(0).getCategoria().getDescricao();
            }
           // id = PreferenceManager.getDefaultSharedPreferences(this).getLong("logado", -1);
            teste = new Teste(Usuario.getLogado(),listaQuestionarios, descricao, null ,null , 0, 0);
            exibirQuestao(posicao);
        }

    private Runnable atualizaTempo = new Runnable() {
        public void run() {
            tempoDecorrido = SystemClock.uptimeMillis() - tempoInicial;
            int seg = (int) (tempoDecorrido / 1000);
            int min = seg / 60;
            seg = seg % 60;
            int milliseconds = (int) (tempoDecorrido % 1000);
            mRelogio.setText("" + min + ":"+String.format("%02d", seg) );
            customHandler.postDelayed(this, 0);
        }
    };


    private void exibirQuestao(int pos) {
        // atualiza as alternativas e as respostas
        listaAlternativas = listaQuestoes.get(posicao).getAlternativas();
        listaRespostas.clear();
        for (int i =0 ; i < listaAlternativas.size(); i++) {
            listaRespostas.add(false);
        }
        mTitulo.setText(listaQuestoes.get(pos).getTitulo().toUpperCase());
        adapterAlternativa = new ArrayAdapter<Alternativa>(this, R.layout.lista_alternativas_teste, R.id.texto1, listaAlternativas) {
            // Sobrescrevendo o método para definir os campos de entrada
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView texto = (TextView) view.findViewById(R.id.texto1);
                texto.setText(listaAlternativas.get(position).getAlternativa().toString());
                CheckBox check = (CheckBox) view.findViewById(R.id.testeSelecao);
                check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        listaRespostas.set(position, isChecked);
                    }
                });
                return view;
            }
        };
        listview.setAdapter(adapterAlternativa);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CheckBox box = (CheckBox) view.findViewById(R.id.testeSelecao);
                    boolean marcado = listaRespostas.get(position)==false;
                    box.setChecked(marcado);
                }
        });

    }

    public  void onClickProxima(View v) {
        calcularResultado(posicao);
        posicao++;
        if ( posicao >= listaQuestoes.size()) {
            exibirResultado();
        } else {
            exibirQuestao(posicao);
        }
    }

    public void calcularResultado(int pos) {
        boolean acerto;
        int i = 0;
        do {
            acerto = listaRespostas.get(i) == listaAlternativas.get(i).getCerta();
            i++;
        } while (i < listaAlternativas.size() && (acerto != false));

        if (acerto == true) {
            acertos++;
        } else {
            erros++;
        }
    }

    public  void onClickFinalizar(View v) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Aviso");
        dialogo.setMessage("Deseja Finalizar o Teste ?");
        dialogo.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int arg) {
                        // Exibe o resumo se foi feito no minimo uma questão
                        if (erros+acertos > 0) {
                            exibirResultado();
                        } else {
                            finish();
                        }
                    }
                });
        dialogo.setNegativeButton("Não", null);
        dialogo.show();
    }

    public void exibirResultado() {
        /// exibe o resultado do teste,  salva e termina
        SimpleDateFormat dataFormato = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date inicio = null;
        Date termino = null;
        try {
            inicio = dataFormato.parse(dataFormato.format(tempoInicial));
            termino = dataFormato.parse(dataFormato.format(SystemClock.uptimeMillis()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        teste.setInicio(inicio);
        teste.setTermino(termino);
        teste.setErros(erros);
        teste.setAcertos(acertos);
        teste.salvar();
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Resultado");
        dialogo.setMessage("Total de Acertos: " + String.valueOf(acertos) +
                "\nTotal de  Erros: " + String.valueOf(erros));
        dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int arg) {
                        finish();
                    }
                });
        dialogo.setNeutralButton("POSTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int arg) {
                /// envia para o servidor o resultado
                WebService.postarResultado(getApplicationContext(), teste);
                finish();
            }
        });
        dialogo.show();
    }
}

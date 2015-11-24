package com.alpha.quiztomizador.controles;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.grafico.GraficoPizza;
import com.alpha.quiztomizador.grafico.Pizza;
import com.alpha.quiztomizador.modelos.Questionario;
import com.alpha.quiztomizador.modelos.Teste;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Usuario on 25/09/2015.
 */
public class ResultadosActivity extends Activity {

    private ListView mLista;
    private View mViewGrafico;
    private View mViewResultados;
    private GraficoPizza mGraficoPizza;
    private TextView mCorretas, mErradas, mInicio, mTermino, mQuestionario;

    private List<Teste> listaResultados = new ArrayList<Teste>();
    private ArrayAdapter<Teste> testeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        listaResultados = Teste.listar();
        mLista = (ListView) findViewById(R.id.listagem);
        TextView emptyView = (TextView) findViewById(android.R.id.empty);
        mLista.setEmptyView(emptyView);
        testeAdapter = new ArrayAdapter<Teste>(this, R.layout.lista_resultados, R.id.texto1, listaResultados)
        {
            // Sobrescrevendo o método para definir os campos de entrada
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView texto1 = (TextView) view.findViewById(R.id.texto1);
                texto1.setText(listaResultados.get(position).getDescricao());
                TextView texto2 = (TextView) view.findViewById(R.id.texto2);
                Integer acertos = listaResultados.get(position).getAcertos();
                Integer erros = listaResultados.get(position).getErros();
                texto2.setText("Acertos: "+acertos.toString()+ " de: "+ String.valueOf(erros+acertos));
                RatingBar estrelas = (RatingBar) view.findViewById(R.id.estrelas);
                // total = (acertos/(erros+acertos)) * 5
                Float total = Float.valueOf(acertos / (Float.valueOf(erros + acertos))) * estrelas.getNumStars();
                estrelas.setRating(total);
                return view;
            }
        };

        mLista.setAdapter(testeAdapter);
        mLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Pega o item que foi selecionado e exibe o grafico de pizza do teste
                Teste item = (Teste) parent.getItemAtPosition(position);
                exibirGraficoPizza(item);

            }
        });
        // Configuração da Tela
        mViewGrafico = findViewById(R.id.view_grafico);
        mViewResultados = findViewById(R.id.view_resultados);
        mGraficoPizza = (GraficoPizza)findViewById(R.id.grafico_pizza);
        mCorretas = (TextView) findViewById(R.id.txt_corretas);
        mErradas = (TextView)findViewById(R.id.txt_erradas);
        mInicio = (TextView)findViewById(R.id.txt_inicio);
        mTermino = (TextView)findViewById(R.id.txt_termino);
        mQuestionario = (TextView) findViewById(R.id.txt_questionario);

        mudarVisibilidade(getCurrentFocus());
    }


    public void mudarVisibilidade(View v) {
        mViewGrafico.setVisibility(View.GONE);
        mViewResultados.setVisibility(View.VISIBLE);
    }


    public void exibirGraficoPizza(Teste teste) {
        mGraficoPizza.removeSlices();

        // Corretas
        Pizza pizza1 = new Pizza();
        pizza1.setColor(Color.GREEN);
        pizza1.setValue(teste.getAcertos());
        mGraficoPizza.addSlice(pizza1);
        mCorretas.setText(getString(R.string.resultados_corretas) + teste.getAcertos());

        //Erradas
        Pizza pizza2 = new Pizza();
        pizza2.setColor(Color.RED);
        pizza2.setValue(teste.getErros());
        mGraficoPizza.addSlice(pizza2);
        mErradas.setText(getString(R.string.resultados_erradas) + teste.getErros());

        // salva os valores nas variaveis e exibe
        SimpleDateFormat dataFormato = new SimpleDateFormat("HH:mm:ss");
        String inicio = dataFormato.format(teste.getInicio());
        String termino = dataFormato.format(teste.getTermino());
        mInicio.setText(getString(R.string.resultados_inicio) + inicio);
        mTermino.setText(getString(R.string.resultados_termino) + termino);
        mQuestionario.setText(teste.getDescricao());

        // exibe a view do grafico
        mViewGrafico.setVisibility(View.VISIBLE);
        mViewResultados.setVisibility(View.GONE);

        for (Questionario q: teste.getQuestionarios()) {
            Toast.makeText(this, q.getDescricao(), Toast.LENGTH_SHORT).show();
        }

    }

    public void onClickLimparResultados(View v) {
        // exclui do banco e atualiza lista
        for (Teste teste: listaResultados) {
            teste.excluir();
        }
        testeAdapter.clear();
        testeAdapter.notifyDataSetChanged();
    }

}

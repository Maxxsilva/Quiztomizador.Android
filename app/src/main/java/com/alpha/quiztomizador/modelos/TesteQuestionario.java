package com.alpha.quiztomizador.modelos;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Mauro on 02/11/2015.
 */
@Table(name="teste_questionario", id = "id")
public class TesteQuestionario extends Model {

    @Column(name = "idteste", onDelete= Column.ForeignKeyAction.CASCADE)
    public Teste teste;

    @Column(name = "idquestionario", onDelete= Column.ForeignKeyAction.CASCADE)
    public Questionario questionario;

    public TesteQuestionario(Teste teste, Questionario questionario) {
        super();
        this.teste = teste;
        this.questionario = questionario;
    }

    // retorna todos os questionarios de um teste específico
    public static List<Questionario> getQuestionarios(Long idTeste) {
        // SELECT * FROM questionario AS q LEFT JOIN testequestionario AS a ON q.id = a.idquestionario WHERE a.idteste =?
        return new Select()
                .from(Questionario.class).as("q")
                .leftJoin(TesteQuestionario.class).as("a")
                .on("q.id = a.idquestionario")
                .where("a.idteste = ?", idTeste)
                .execute();
    }

    // retorna todos os testes de um questionario específico
    public static List<Teste> getTestes(Long idQuestionario) {
        // SELECT * FROM teste AS t LEFT JOIN testequestionario AS a ON t.id = a.idteste WHERE a.idtquestionario =?
        return new Select()
                .from(Teste.class).as("t")
                .leftJoin(TesteQuestionario.class).as("a")
                .on("t.id = a.idteste")
                .where("a.idquestionario = ?", idQuestionario)
                .execute();
    }

}
package com.alpha.quiztomizador.modelos;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Created by Usuario on 09/09/2015.
 */

@Table(name="questao", id="id")
public class Questao extends Model {


    @Column(name="uid")
    private Long uid;

    @Column(name = "idquestionario", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Questionario questionario;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "tipo")
    private Tipo tipo;

    @Column(name = "atualizacao")
    private Date atualizacao;

    @Column(name = "sincronizacao")
    private Date sincronizacao;

    @Column(name= "excluido")
    private boolean excluido;

   // private List<Alternativa> alternativas;

    public Questao() {
        super();
    }

    public Questao(String titulo, Tipo tipo) {
        super();
        this.titulo = titulo;
        this.tipo = tipo;
      //  this.alternativas = alternativas;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Questionario getQuestionario() {
        return questionario;
    }

    public void setQuestionario(Questionario questionario) {
        this.questionario = questionario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Date getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(Date atualizacao) {
        this.atualizacao = atualizacao;
    }

    public Date getSincronizacao() {
        return sincronizacao;
    }

    public void setSincronizacao(Date sincronizacao) {
        this.sincronizacao = sincronizacao;
    }

    public boolean getExcluido() {
        return excluido;
    }

    public void setExcluido(boolean excluido) {
        this.excluido = excluido;
    }

    @Override
    public String toString() {
        return getTitulo();
    }

    //=========== CRUDS ==============

    public static List<Questao> listar(String...clausula) {
        String clause = "1=1";
        for (int i=0; i < clausula.length; i++) {
            clause+=" AND "+clausula[i];
        }
        return new Select()
                .from(Questao.class)
                .where(clause)
                .orderBy("titulo ASC")
                .execute();
    }

    public static List<Questao> listarPorQuestionario(Long idQuestionario) {
        return listar("idquestionario = "+idQuestionario.toString(), "excluido=0");
    }

    public static List<Questao> listarPorCategoria(Long idCategoria) {
        // SELECT * FROM questoes AS c LEFT JOIN questionario AS q ON c.idquestionario = q.id WHERE q.categoria =? and c.excluido = false
        return new Select()
                .from(Questao.class).as("c")
                .leftJoin(Questionario.class).as("q")
                .on("c.idquestionario = q.id")
                .where("q.idcategoria = ?", idCategoria)
                .and("c.excluido = 0")
                .execute();
    }

    public static Questao procurar(Long id) {
        return new Select()
                .from(Questao.class)
                .where("id = ?", String.valueOf(id))
                .executeSingle();
    }

    // os dados so podem ser excluidos fisicamente se nunca foram sincronicados
    // ou se ja a data de sincronização for maior que a data de atualização
    public void excluir() {
        if (sincronizacao == null) {
            excluido = true;
            super.save();
            //super.delete();
        } else {
            if (sincronizacao.after(atualizacao)) {
                super.delete();
            } else {
                excluido = true;
                super.save();
            }
        }
    }
    // sempre que salvar grava a data/hora atual
    public Long salvar() {
        atualizacao = new Date();
        return super.save();
    }

    public List<Alternativa> getAlternativas() {
        return Alternativa.listar("idquestao = "+this.getId());
    }

}

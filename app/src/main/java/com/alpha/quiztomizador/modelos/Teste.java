package com.alpha.quiztomizador.modelos;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Created by Usuario on 09/09/2015.
 */

@Table(name="teste", id="id")
public class Teste extends Model {

    @Column(name="uid")
    private Long uid;

    @Column(name="idusuario")
    private Usuario usuario;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "inicio")
    private Date inicio;

    @Column(name = "termino")
    private Date termino;

    @Column(name = "acertos")
    private Integer acertos;

    @Column(name = "erros")
    private Integer erros;

    private List<Questionario> questionarios;

    public Teste() {
       super();
    }

    public Teste(Usuario usuario, List<Questionario> questionarios, String descricao, Date inicio, Date termino, Integer acertos, Integer erros) {
        super();
        this.usuario = usuario;
        this.questionarios = questionarios;
        this.descricao = descricao;
        this.inicio = inicio;
        this.termino = termino;
        this.acertos = acertos;
        this.erros = erros;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Questionario> getQuestionarios() {
        if (questionarios == null) {
            questionarios = TesteQuestionario.getQuestionarios(this.getId());
        }
        return questionarios;
    }

    public void setQuestionarios(List<Questionario> questionarios) {
        this.questionarios = questionarios;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }

    public Integer getAcertos() {
        return acertos;
    }

    public void setAcertos(Integer acertos) {
        this.acertos = acertos;
    }

    public Integer getErros() {
        return erros;
    }

    public void setErros(Integer erros) {
        this.erros = erros;
    }

//=========== CRUDS ==============

    public static List<Teste> listar(String...clausula) {
        String clause = "idusuario = "+Usuario.getLogado().getId();
        for (int i=0; i < clausula.length; i++) {
            clause+=" and "+clausula[i];
        }
        return new Select()
                .from(Teste.class)
                .where(clause)
                .orderBy("descricao ASC")
                .execute();
    }

    public static Teste procurar(Long id) {
        return new Select()
                .from(Teste.class)
                .where("id = ?", String.valueOf(id))
                .executeSingle();
    }

    public void excluir() {
        super.delete();
    }

    public Long salvar() {
        Long id = super.save();
        for (Questionario q: questionarios) {
            TesteQuestionario tq = new TesteQuestionario(this, q);
            tq.save();
        }
        return id;
    }

}

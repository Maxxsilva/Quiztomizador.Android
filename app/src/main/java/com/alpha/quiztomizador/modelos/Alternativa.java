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

@Table(name="alternativa", id="id")
public class Alternativa extends Model {

    @Column(name="uid")
    private Long uid;

    @Column(name = "idquestao", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Questao questao;

    @Column(name = "alternativa")
    private String alternativa;

    @Column(name = "certa")
    private Boolean certa;

    @Column(name = "atualizacao")
    private Date atualizacao;

    @Column(name = "sincronizacao")
    private Date sincronizacao;

    @Column(name= "excluido")
    private boolean excluido;


    public Alternativa() {
        super();
    }

    public Alternativa(Questao questao, String alternativa, Boolean certa) {
        super();
        this.questao = questao;
        this.alternativa = alternativa;
        this.certa = certa;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Questao getQuestao() {
        return questao;
    }

    public void setQuestao(Questao questao) {
        this.questao = questao;
    }

    public String getAlternativa() {
        return alternativa;
    }

    public void setAlternativa(String alternativa) {
        this.alternativa = alternativa;
    }

    public Boolean getCerta() {
        return certa;
    }

    public void setCerta(Boolean certa) {
        this.certa = certa;
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

    //=========== CRUDS ==============

    public static List<Alternativa> listar(String...clausula) {
        String clause = "1=1";
        for (int i=0; i < clausula.length; i++) {
            clause+=" and "+clausula[i];
        }
        return new Select()
                .from(Alternativa.class)
                .where(clause)
                .execute();
    }

    public static Alternativa procurar(Long id) {
        return listar("id = "+id.toString()).get(0);
    }

    // os dados so podem ser excluidos fisicamente se nunca foram sincronicados
    // ou se ja a data de sincronização for maior que a data de atualização
    public void excluir() {
        if (sincronizacao == null) {
            super.delete();
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

}

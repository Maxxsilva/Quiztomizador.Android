package com.alpha.quiztomizador.modelos;

import android.provider.BaseColumns;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.alpha.quiztomizador.modelos.Categoria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Created by Usuario on 09/09/2015.
 */

@Table(name="questionario", id = "id") //BaseColumns._ID)
public class Questionario extends Model {

    @Column(name="uid")
    private Long uid;

    @Column(name="idusuario")
    private Usuario usuario;

    @Column(name="idcriador")
    private Usuario criador;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "idcategoria", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.SET_DEFAULT)
    private Categoria categoria;

    @Column(name = "atualizacao")
    private Date atualizacao;

    @Column(name = "sincronizacao")
    private Date sincronizacao;

    @Column(name= "excluido")
    private boolean excluido;

    private List<Questao> questoes;

    public Questionario() {
        super();
    }

    public Questionario(String descricao, Categoria categoria) {
        super();
        this.descricao = descricao;
        this.categoria = categoria;
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

    public Usuario getCriador() {
        return criador;
    }

    public void setCriador(Usuario criador) {
        this.criador = criador;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
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
        return getDescricao()+"@"+getQuestoes().size();
    }

    //=========== CRUDS ==============

    public static List<Questionario> listar(String...clausula) {
        //String clause = clausula.length > 0 ? clausula[0] : "1=1";
        String clause = "idusuario = "+Usuario.getLogado().getId();
        for (int i=0; i < clausula.length; i++) {
            clause+=" AND "+clausula[i];
        }
        return new Select()
                .from(Questionario.class)
                .where(clause)
                .orderBy("descricao ASC")
                .execute();
    }

    public static List<Questionario> listarPorCategoria(Long idCategoria) {
        return listar("idcategoria = " + idCategoria);
    }

    public List<Questao> getQuestoes() {
//        if (questoes == null || questoes.size()==0) {
//            questoes = Questao.listar("idquestionario = "+this.getId(), "excluido = 0");
//        }
        return Questao.listar("idquestionario = "+this.getId(), "excluido = 0");
    }

    public static Questionario procurar(String clausula) {
        return new Select()
                .from(Questionario.class)
                .where(clausula)
                .executeSingle();
    }
    // os dados so podem ser excluidos fisicamente se nunca foram sincronizados
    // ou se ja a data de sincronização for maior que a data de atualização
    public void excluir() {
        if (sincronizacao == null) {
            super.delete();
        } else {
            excluido = true;
            super.save();
        }
    }

    // sempre que salvar grava a data/hora atual
    public Long salvar() {
        atualizacao = new Date();
        return super.save();
    }


}

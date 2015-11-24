package com.alpha.quiztomizador.modelos;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

/**
 * Created by Usuario on 09/09/2015.
 */

@Table(name="categoria", id = "id")
public class Categoria  extends Model {

    @Column(name="uid")
    private Long uid;

    @Column(name="idusuario")
    private Usuario usuario;
f
    @Column(name="idcriador")
    private Usuario criador;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "atualizacao")
    private Date atualizacao;

    @Column(name = "sincronizacao")
    private Date sincronizacao;

    @Column(name= "excluido")
    private boolean excluido;

    public Categoria() {
        super();
    }

    public Categoria(String descricao) {
        super();
        this.descricao = descricao;
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

    public String getDescricao() { return descricao;}

    public void setDescricao(String descricao) {this.descricao = descricao;}

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

    public boolean isExcluido() {
        return excluido;
    }

    public void setExcluido(boolean excluido) {
        this.excluido = excluido;
    }

    @Override
    public String toString() {
       return getDescricao();
    }
    //=========== CRUDS ==============

    // listar as categorias do usuário
    public static List<Categoria> listar(String...clausula) {
        String clause = "idusuario = "+Usuario.getLogado().getId();
        for (int i=0; i < clausula.length; i++) {
            clause+=" and "+clausula[i];
        }
        return new Select()
                .from(Categoria.class)
                .where(clause)
                .orderBy("descricao ASC")
                .execute();
    }

    public static Categoria procurar(String clausula) {
        return new Select()
                .from(Categoria.class)
                .where(clausula)
                .executeSingle();
    }

    // os dados so podem ser excluidos fisicamente se nunca foram sincronicados
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

//    public List getQuestionarios() {
//        return getMany(Questionario.class, "categoria");
//    }
    public List<Questionario> getQuestionarios() {
        return Questionario.listar("idcategoria = "+this.getId(), "excluido=0");
    }

}

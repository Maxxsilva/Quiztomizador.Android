package com.alpha.quiztomizador.modelos;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Usuario on 09/09/2015.
 */

@Table(name="usuario", id="id")
public class Usuario extends Model {

    @Column(name="uid")
    private Long uid;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    private static transient Usuario logado;

    public Usuario() {
        super();
    }

    public Usuario(String nome, String email) {
        super();
        this.nome = nome;
        this.email = email;
    }
    public Long getUid() { return  uid;}

    public void setUid(Long uid) { this.uid = uid; }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public static Usuario getLogado() {
        return logado;
    }

    public void setLogado() {
        Usuario.logado = this;
    }

    @Override
    public String toString() {
       return getId()+"@"+getNome()+"@"+getEmail();
    }

    //=========== CRUDS ==============

    public static List<Usuario> listar(String...clausula) {
        String clause = "1=1";
        for (int i=0; i < clausula.length; i++) {
            clause+=" and "+clausula[i];
        }
        return new Select()
                .from(Usuario.class)
                .where(clause)
                .orderBy("nome ASC")
                .execute();
    }

    public static Usuario procurar(String clausula) {
            return new Select()
                    .from(Questao.class)
                    .where(clausula)
                    .executeSingle();
        }

    public void excluir() {
        super.delete();
    }

    public Long salvar() {
        return super.save();
    }

}

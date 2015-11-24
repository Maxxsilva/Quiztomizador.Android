package com.alpha.quiztomizador.modelos;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Usuario on 09/09/2015.
 */

public class Grupo {

    private Long id;
    private String nome;
    private String descricao;
    private Usuario criador;


    public Grupo() {
    }

    public Grupo(String nome, String descricao, Usuario criador) {
        this.nome = nome;
        this.descricao = descricao;
        this.criador = criador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Usuario getCriador() {
        return criador;
    }

    public void setCriador(Usuario criador) {
        this.criador = criador;
    }
}

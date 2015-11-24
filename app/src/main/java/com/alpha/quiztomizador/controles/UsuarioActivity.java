package com.alpha.quiztomizador.controles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.modelos.Usuario;
import com.alpha.quiztomizador.util.Util;
import com.alpha.quiztomizador.util.WebService;

import java.util.HashMap;
import java.util.Map;

/**
 * Tela de login
 */
public class UsuarioActivity extends Activity  {

    private EditText mNome;
    private EditText mEmail;
    private EditText mSenha;
    private EditText mConfirmarSenha;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        // Configuração da tela
        mNome = (EditText) findViewById(R.id.txt_nome);
        mNome.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.cadastro_usuario || id == EditorInfo.IME_NULL) {
                    validarCampos();
                    return true;
                }
                return false;
            }
        });
        mEmail = (EditText) findViewById(R.id.txt_email);
        mEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.cadastro_usuario || id == EditorInfo.IME_NULL) {
                    validarCampos();
                    return true;
                }
                return false;
            }
        });
        mSenha = (EditText) findViewById(R.id.txt_senha);
        mSenha.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.cadastro_usuario || id == EditorInfo.IME_NULL) {
                    validarCampos();
                    return true;
                }
                return false;
            }
        });
        mConfirmarSenha = (EditText) findViewById(R.id.txt_confirmar_senha);
        mConfirmarSenha.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.cadastro_usuario || id == EditorInfo.IME_NULL) {
                    validarCampos();
                    return true;
                }
                return false;
            }
        });
        btnEnviar = (Button) findViewById(R.id.btn_enviar);
        btnEnviar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                validarCampos();
            }
        });
    }

    /**
     * valida os campos de  login (email invalido, campos vazios, etc)
     */
    public void validarCampos() {
        // Reseta os erros.
        mNome.setError(null);
        mEmail.setError(null);
        mSenha.setError(null);
        mConfirmarSenha.setError(null);

        // salva os valores nas variaveis
        String nome = mNome.getText().toString();
        String email = mEmail.getText().toString();
        String senha = mSenha.getText().toString();
        String confirmarSenha = mConfirmarSenha.getText().toString();

        boolean cancela = false;
        View focusView = null;

        // verifica se o nome esta vazio
        if (TextUtils.isEmpty(nome)) {
            mNome.setError(getString(R.string.usuario_erro_obrigatorio));
            focusView = mNome;
            cancela = true;
        }
        // verifica se o email é valido
        else if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.usuario_erro_obrigatorio));
            focusView = mEmail;
            cancela = true;
        }
        else if (!emailValido(email)) {
            mEmail.setError(getString(R.string.usuario_erro_email));
            focusView = mEmail;
            cancela = true;
        }
        // verifica se a senha é valida
        else if (TextUtils.isEmpty(senha)) {
            mSenha.setError(getString(R.string.usuario_erro_obrigatorio));
            focusView = mSenha;
            cancela = true;
        }
        else if (!senhaValida(senha)) {
            mSenha.setError(getString(R.string.usuario_erro_senha));
            focusView = mSenha;
            cancela = true;
        }

        // verifica se a confirmação da senha é valida
        else if (!confirmarSenha.equals(senha)) {
            mConfirmarSenha.setError(getString(R.string.usuario_erro_confirmar));
            focusView = mConfirmarSenha;
            cancela = true;
        }

        if (cancela) {
            // se teve algum erro, mantem o foco no campo
            focusView.requestFocus();
        } else {
            // envia o cadastro do usuário
            enviarCadastro(nome, email, senha);
        }
    }

    // regras de negocio e validação
    private boolean emailValido(String email) {
        return email.contains("@");
    }

    private boolean senhaValida(String senha) {
        return senha.length() > 5;
    }


    public void enviarCadastro(String nome, String email, String senha) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Conectando...");
        pDialog.show();
        try {
            String resposta = WebService.cadastrarUsuario(this, nome, email, senha);
            Toast.makeText(getApplicationContext(), "Cadastramento efetuado com sucesso!", Toast.LENGTH_LONG).show();
            Usuario usuario = new Usuario(nome, email);
            usuario.setUid(Long.valueOf(resposta));
            usuario.salvar();
            pDialog.cancel();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            pDialog.cancel();
        }
    }
}




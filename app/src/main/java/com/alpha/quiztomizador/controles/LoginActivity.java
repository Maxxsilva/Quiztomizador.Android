package com.alpha.quiztomizador.controles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.modelos.Usuario;
import com.alpha.quiztomizador.util.GenericJSON;
import com.alpha.quiztomizador.util.WebService;

import org.json.JSONObject;

import java.util.List;


/**
 * Tela de login
 */
public class LoginActivity extends Activity  {

    private AutoCompleteTextView mEmail;
    private EditText mSenha;
    private Button btnLogar;
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Configuração da tela de login
        setContentView(R.layout.activity_login);
        mEmail = (AutoCompleteTextView) findViewById(R.id.txt_email);
        mSenha = (EditText) findViewById(R.id.txt_senha);
        mSenha.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    validarLogin();
                    return true;
                }
                return false;
            }
        });
        btnLogar = (Button) findViewById(R.id.btn_logar);
        btnLogar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                validarLogin();
            }
        });
        btnCadastrar = (Button) findViewById(R.id.btn_cadastrar);
        btnCadastrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),UsuarioActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * valida os campos de  login (email invalido, campos vazios, etc)
     */
    public void validarLogin() {
        // Reseta os erros.
        mEmail.setError(null);
        mSenha.setError(null);

        // salva os valores nas variaveis
        String email = mEmail.getText().toString();
        String senha = mSenha.getText().toString();

        boolean cancela = false;
        View focusView = null;

        // verifica se a senha é valida
        if (!TextUtils.isEmpty(senha) && !senhaValida(senha)) {
            mSenha.setError(getString(R.string.login_erro_senha));
            focusView = mSenha;
            cancela = true;
        }
        // verifica o email de email.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.login_erro_obrigatorio));
            focusView = mEmail;
            cancela = true;
        } else if (!emailValido(email)) {
            mEmail.setError(getString(R.string.login_erro_email));
            focusView = mEmail;
            cancela = true;
        }

        if (cancela) {
            // se teve algum erro, mantem o foco no campo
            focusView.requestFocus();
        } else {
            // Autentica o usuário
            autenticar(email, senha);
        }
    }

    // regras de negocio e validação
    private boolean emailValido(String email) {
        return email.contains("@");
    }

    private boolean senhaValida(String senha) {
        return senha.length() > 5;
    }

    public void autenticar(String email, String senha) {
        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Conectando...");
        pDialog.show();
        try {
            String resposta = WebService.logarUsuario(this, email, senha);
            JSONObject jo = new JSONObject(resposta);
            Long uid = jo.getLong("uid");
            String nome = jo.getString("nome");
            Usuario usuario = new Usuario(nome, email);
            usuario.setUid(uid);
            // vefifica e salva o usuario no banco
            List<Usuario> listaUsuario = Usuario.listar("uid = "+ uid);
            if (listaUsuario.size() == 0) {
                usuario.salvar();
            } else {
                usuario = listaUsuario.get(0);
            }
            usuario.setLogado();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putLong("logado", usuario.getId()).commit();
            Intent i = new Intent(getApplicationContext(), PrincipalActivity.class);
            startActivity(i);
            pDialog.cancel();
            finish();
        } catch (Exception e) {
            if (e.getMessage().contains("500")) {
                Toast.makeText(getApplicationContext(), getString(R.string.login_erro_incorreto), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            pDialog.cancel();
        }
    }

}




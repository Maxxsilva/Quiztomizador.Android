package com.alpha.quiztomizador.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;

import com.alpha.quiztomizador.R;
import com.alpha.quiztomizador.modelos.Categoria;
import com.alpha.quiztomizador.modelos.Questionario;
import com.alpha.quiztomizador.modelos.Teste;
import com.alpha.quiztomizador.modelos.Usuario;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestTickle;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.error.VolleyError;
import com.android.volley.error.VolleyErrorHelper;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.VolleyTickle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mauro on 01/11/2015.
 */
public class WebService  {

    // TODO metodo de compartilharQuestionario - o webservice altera o status de privado para público

    // TODO metodo de baixarQuestionario - o webservice cria um registro na tabela usuario_questionario

    // TODO metodo de listarQuestionario - o webservice lista todos questionários associados usuario_questionario
        // categorias também????

   // OBS um usuário não pode excluir uma categoria que não seja dele, até pq a lista de categorias so exibem os questionários dele

    // OBS2 um usuario pode excluir da base um questionario que foi "baixado", deve-se excluir da tabela usuario_questionario
    // e caso ele seja o dono deve-se mudar para excluido=1

    private enum Metodo {
        Criar,
        Alterar,
        Excluir
    }

    public static String logarUsuario(Context ctx, final String email, final String senha) throws Exception {
        String retorno = null;
        final String TAG = "obj_req";
        String url =  ctx.getString(R.string.web_service)+"/Usuarios.asmx/Logar";
        StringRequest request = new StringRequest(Request.Method.POST,url, null, null)
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email.toString());
                params.put("senha", Util.criptografar(senha));
                Log.e("SENHA", Util.criptografar(senha));
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestTickle mRequestTickle = VolleyTickle.newRequestTickle(ctx);
        mRequestTickle.add(request);
        NetworkResponse response = mRequestTickle.start();

            if(response.statusCode == 200){
                retorno = VolleyTickle.parseResponse(response);
            } else if (response.statusCode == 500) {
                throw new Exception(response.statusCode + VolleyTickle.parseResponse(response));
            } else {
                throw new Exception(mRequestTickle.getError().getMessage());
                //Toast.makeText(ctx, retorno, Toast.LENGTH_LONG).show();
            }
        return retorno;
    }

    public static String cadastrarUsuario(Context ctx, final String nome, final String email, final String senha) throws Exception {
        String retorno = null;
        final String TAG = "obj_req";
        String url =  ctx.getString(R.string.web_service)+"/Usuarios.asmx/Criar";
        StringRequest request = new StringRequest(Request.Method.POST, url, null, null)
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nome", nome.toString());
                params.put("email", email.toString());
                params.put("senha", Util.criptografar(senha));
                Log.e("SENHA", Util.criptografar(senha));
                return params;
            }
        };
        RequestTickle mRequestTickle = VolleyTickle.newRequestTickle(ctx);
        mRequestTickle.add(request);
        NetworkResponse response = mRequestTickle.start();

        if(response.statusCode == 200){
            retorno = VolleyTickle.parseResponse(response);
        } else if (response.statusCode == 500) {
            throw new Exception(response.statusCode + VolleyTickle.parseResponse(response));
        } else {
            throw new Exception(response.statusCode + mRequestTickle.getError().getMessage());
        }
        return retorno;
    }

    public static void postarResultado(Context ctx, final Teste teste)  {
        final String TAG = "obj_req";
        String url =  ctx.getString(R.string.web_service)+"/Testes.asmx/Postar";
        StringRequest request = new StringRequest(Request.Method.POST, url, null, null)
        {
            @Override
            protected Map<String, String> getParams() {
                SimpleDateFormat dataFormato = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUsuario", teste.getUsuario().getUid().toString());
                params.put("descricao", teste.getDescricao());
                params.put("inicio", dataFormato.format(teste.getInicio()));
                params.put("termino", dataFormato.format(teste.getTermino()));
                params.put("acertos", teste.getAcertos().toString());
                params.put("erros", teste.getErros().toString());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue queue = Volley.newRequestQueue(ctx);
        queue.add(request);
        queue.start();

    }

    public static void sincronizarDados(Context ctx) {
        sincronizarTodasCategorias(ctx);
        sincronizarTodosQuestionarios(ctx);
//        sincronizarQuestao();
//        sincronizarAlternativa();
//        sincronizarMobile();
    }

    private static void sincronizarTodasCategorias(Context ctx) {
        // Gera as Listas do MOBILE e sincroniza com o WebService
        List<Categoria> incluidas = Categoria.listar("excluido=0", "sincronizacao is null");
        //TODO verificar retorno do servidor ao tentar excluir, pois se o retorno for "registro não encontrado" significa
        // que ja foi excluido em algum momento
        List<Categoria> excluidas = Categoria.listar("excluido=1");
        List<Categoria> atualizadas = Categoria.listar("excluido=0", "atualizacao > sincronizacao");

        for (Categoria categoria : incluidas) {
            sincronizarCategoria(ctx, categoria, Metodo.Criar);
        }
        for (Categoria categoria : excluidas) {
            sincronizarCategoria(ctx, categoria, Metodo.Excluir);
        }
        for (Categoria categoria : atualizadas) {
            sincronizarCategoria(ctx, categoria, Metodo.Alterar);
        }
        // lista todos do servidor e atualiza no mobile se necessario
        listarCategorias(ctx);

    }

    private static void sincronizarTodosQuestionarios(Context ctx) {
        // Gera as Listas do MOBILE e sincroniza com o WebService
        List<Questionario> incluidos = Questionario.listar("excluido=0", "sincronizacao is null");
        List<Questionario> excluidos = Questionario.listar("excluido=1");
        List<Questionario> atualizados = Questionario.listar("excluido=0", "atualizacao > sincronizacao");

        for (Questionario questionario : incluidos) {
            sincronizarQuestionario(ctx, questionario, Metodo.Criar);
        }
        for (Questionario questionario : excluidos) {
            sincronizarQuestionario(ctx, questionario, Metodo.Excluir);
        }
            for (Questionario questionario : atualizados) {
            sincronizarQuestionario(ctx, questionario, Metodo.Alterar);
        }
        // lista todos do servidor e atualiza no mobile se necessario
        listarQuestionarios(ctx);
    }


    private static synchronized void sincronizarCategoria(Context ctx, final Categoria categoria, final Metodo metodo) {
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Sincronizando Categoria...");
        pDialog.show();
        String url = ctx.getString(R.string.web_service)+"/Categorias.asmx/" + metodo.toString();
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                try {
                    if (metodo.equals(Metodo.Criar)) {
                        JSONObject jo = new JSONObject(jsonObject);
                        Integer uid = (Integer) jo.get("uid");
                        categoria.setUid(uid.longValue());
                        categoria.setSincronizacao(new Date());
                        categoria.save();
                    } else if (metodo.equals(Metodo.Alterar)) {
                        categoria.setSincronizacao(new Date());
                        categoria.save();
                    } else if (metodo.equals(Metodo.Excluir)) {
                        categoria.delete();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.cancel();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                pDialog.cancel();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                if (metodo.toString().equals("Criar")) {
                    params.put("descricao", categoria.getDescricao());
                    params.put("idUsuario", categoria.getUsuario().getUid().toString());
                    params.put("idCriador", categoria.getCriador().getUid().toString());
                } else if (metodo.toString().equals("Alterar")) {
                    params.put("idCategoria", categoria.getUid().toString());
                    params.put("descricao", categoria.getDescricao());
                } else if (metodo.toString().equals("Excluir")) {
                    params.put("idCategoria", categoria.getUid().toString());
                }
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ctx);
        queue.add(req);
        queue.start();
    }

    private static synchronized void sincronizarQuestionario(Context ctx, final Questionario questionario, final Metodo metodo) {
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Sincronizando Questionario...");
        pDialog.show();
        String url = ctx.getString(R.string.web_service)+"/Questionarios.asmx/" + metodo.toString();
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                try {
                    if (metodo.equals(Metodo.Criar)) {
                        JSONObject jo = new JSONObject(jsonObject);
                        Integer uid = (Integer) jo.get("uid");
                        questionario.setUid(uid.longValue());
                        questionario.setSincronizacao(new Date());
                        questionario.save();
                    } else if (metodo.equals(Metodo.Alterar)) {
                        questionario.setSincronizacao(new Date());
                        questionario.save();
                    } else if (metodo.equals(Metodo.Excluir)) {
                        questionario.delete();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.cancel();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                pDialog.cancel();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                if (metodo.toString().equals("Criar")) {
                    params.put("descricao", questionario.getDescricao());
                    params.put("idUsuario", questionario.getUsuario().getUid().toString());
                    params.put("idCriador", questionario.getCriador().getUid().toString());
                    params.put("idCategoria", questionario.getCategoria().getUid().toString());
                } else if (metodo.toString().equals("Alterar")) {
                    params.put("idQuestionario", questionario.getUid().toString());
                    params.put("descricao", questionario.getDescricao());
                } else if (metodo.toString().equals("Excluir")) {
                    params.put("idQuestionario", questionario.getUid().toString());
                }
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ctx);
        queue.add(req);
        queue.start();
    }


    private static List<Categoria> listarCategorias(Context ctx) {
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Sincronizando Categorias...");
        pDialog.show();
        final List<Categoria> listaCategoria = new ArrayList<Categoria>();
        String url = ctx.getString(R.string.web_service)+"/Categorias.asmx/Retorna";
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonArray) {
                try {
                    JSONArray ja = new JSONArray(jsonArray);
                    for (int i=0; i<ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        Long uid = jo.getLong("uid");
                        Long uidCriador = jo.getLong("criador");
                        String descricao = jo.getString("descricao");
                        Categoria cat = new Categoria();
                        cat.setUid(uid);
                        cat.setCriador(Usuario.procurar("uid ="+uidCriador));
                        cat.setDescricao(descricao);
                        cat.setUsuario(Usuario.getLogado());
                        listaCategoria.add(cat);
                    }
                    // salva as categorias inexistentes no banco mobile
                    for (Categoria categoria: listaCategoria) {
                        Categoria cat = Categoria.procurar("uid = " + categoria.getUid());
                        if (cat == null) {
                            categoria.setAtualizacao(new Date());
                            categoria.setSincronizacao(new Date());
                            categoria.save();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                pDialog.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                pDialog.cancel();
            }
        }){
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUsuario", Usuario.getLogado().getUid().toString());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ctx);
        queue.add(req);
        queue.start();
        return listaCategoria;
    }

    private static List<Questionario> listarQuestionarios(Context ctx) {
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Sincronizando questionarios...");
        pDialog.show();
        final List<Questionario> listaQuestionario = new ArrayList<Questionario>();
        String url = ctx.getString(R.string.web_service)+"/Questionarios.asmx/Retorna";
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonArray) {
                try {
                    JSONArray ja = new JSONArray(jsonArray);
                    for (int i=0; i<ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        Long uid = jo.getLong("uid");
                        String descricao = jo.getString("descricao");
                        Long uidCriador = jo.getLong("criador");
                        Long uidCategoria = jo.getLong("categoria");
                        Questionario quest = new Questionario();
                        quest.setUid(uid);
                        quest.setDescricao(descricao);
                        quest.setCriador(Usuario.procurar("uid ="+uidCriador));
                        quest.setCategoria(Categoria.procurar("uid ="+uidCategoria));
                        quest.setUsuario(Usuario.getLogado());
                        listaQuestionario.add(quest);
                    }
                    // salva as questionarios inexistentes no banco mobile
                    for (Questionario questionario: listaQuestionario) {
                        Questionario quest = Questionario.procurar("uid = " + questionario.getUid());
                        if (quest == null) {
                            questionario.setAtualizacao(new Date());
                            questionario.setSincronizacao(new Date());
                            questionario.save();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                pDialog.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                pDialog.cancel();
            }
        }){
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUsuario", Usuario.getLogado().getUid().toString());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ctx);
        queue.add(req);
        queue.start();
        return listaQuestionario;
    }

}

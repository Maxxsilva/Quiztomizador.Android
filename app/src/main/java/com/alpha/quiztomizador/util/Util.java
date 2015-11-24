package com.alpha.quiztomizador.util;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Mauro on 31/10/2015.
 */
public class Util {

    public static String criptografar(String senha) {
        MessageDigest mdSha1 = null;
        try {
            mdSha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        mdSha1.update(senha.getBytes());
        BigInteger hash = new BigInteger(1,mdSha1.digest());
        String retorno = hash.toString(16);
        return (retorno);
    }


}

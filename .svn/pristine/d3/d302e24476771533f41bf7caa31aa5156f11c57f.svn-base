package com.ar.echoafcavlapplication.Utils;

import com.ar.echoafcavlapplication.Services.TransactionsFileHandler;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtility {
    private static EncryptionUtility instance;
    private static Logger log = LoggerFactory.getLogger(EncryptionUtility.class);
    private String key = "Ar500@2020fh6j9kRMGHDSH27623728B";
    private String initializationVector="8214367569854723";

    public static EncryptionUtility getInstance(){
        if (instance == null)
            instance = new EncryptionUtility();
        return instance;
    }

    public byte[] encrypt(String plainText){
        return encrypt(plainText.getBytes(Charset.forName("UTF-8")));
    }

    public byte[] encrypt(byte[] plaintext){
        try{
            byte[] tDesKeyData=key.getBytes();
            byte[] myIV=initializationVector.getBytes();
            Cipher c3des=Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec myKey=new SecretKeySpec(tDesKeyData,"AES");
            IvParameterSpec ivspec=new IvParameterSpec(myIV);
            c3des.init(Cipher.ENCRYPT_MODE, myKey, ivspec);
            byte[] cipherText=c3des.doFinal(plaintext);
            return new Base64().encode(cipherText);
        }catch(Exception ex){
            log.error("EncryptionUtility --> encrypt(byte):" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return null;
    }

    public byte[] decrypt(String plainText){
        try{
            byte[] plaintext=new Base64().decode(plainText.getBytes(Charset.forName("UTF-8")));
            byte[] tDesKeyData=key.getBytes();
            byte[] myIV=initializationVector.getBytes();
            Cipher c3des=Cipher.getInstance("AES/CBC/PKCS5Padding"); //DESede
            SecretKeySpec myKey=new SecretKeySpec(tDesKeyData,"AES");
            IvParameterSpec ivspec=new IvParameterSpec(myIV);
            c3des.init(Cipher.DECRYPT_MODE,myKey,ivspec);
            byte[] cipherText=c3des.doFinal(plaintext);
            return new Base64().encode(cipherText);
        }catch(Exception ex){
            log.error("EncryptionUtility --> decrypt():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return null;
    }
}

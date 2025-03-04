/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.config;

import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author herma
 */
public class GenerateKey {
    public static void main(String[] args) {
        try {
            // Kulcsgenerálás HMAC-SHA256 algoritmussal
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256); // 256 bites kulcs
            SecretKey secretKey = keyGen.generateKey();
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            
            // Generált kulcs kiíratása
            System.out.println("Generalt SIGN key: " + encodedKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

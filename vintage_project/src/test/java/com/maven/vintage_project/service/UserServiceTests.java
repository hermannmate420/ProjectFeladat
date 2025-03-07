/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.service;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
/**
 *
 * @author herma
 */
public class UserServiceTests {
    @Test
    void testValidEmails() {
        assertTrue(UserService.isValidEmail("Testelek@gmail.com"));
        assertTrue(UserService.isValidEmail("user.name+tag@sub.domain.co.uk"));
    }
    
    @Test
    void testInvalidEmails() {
        assertFalse(UserService.isValidEmail(null));
        assertFalse(UserService.isValidEmail(""));
        assertFalse(UserService.isValidEmail("plainaddress"));
        assertFalse(UserService.isValidEmail("@missinguser.com"));
        assertFalse(UserService.isValidEmail("user@.com"));
    }
    
    @Test
    void testValidPasswords() {
        assertTrue(UserService.isValidPassword("Alma123!"));
        assertTrue(UserService.isValidPassword("EzJojelszo123?"));
    }
    
    @Test
    void testInvalidPasswords() {
        assertFalse(UserService.isValidPassword("Nemrakokszamokat!"));
        assertFalse(UserService.isValidPassword("WithoutSpecialCh123"));
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.model;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

/**
 *
 * @author herma
 */
public class UserModelTest {
    @Mock
    private EntityManager em;

    @Mock
    private StoredProcedureQuery spq;

    @InjectMocks
    private User userModel;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    /*@Test
    void testRegisterUser_Success() {
        // Arrange
        User testUser = new User(1, "testuser", "Test", "User", "test@example.com", "+36701234567", "password", false, false, null, null);
        
        when(em.createStoredProcedureQuery("registration")).thenReturn(spq);
        doNothing().when(spq).registerStoredProcedureParameter(anyString(), eq(String.class), eq(ParameterMode.IN));
        when(spq.execute()).thenReturn(true);

        // Act
        boolean result = userModel.registerUser(testUser);

        // Assert
        assertTrue(result);
        verify(spq, times(1)).execute();
    }
    
    @Test
    void testRegisterUser_Failure() {
        // Arrange
        User testUser = new User(1, "testuser", "Test", "User", "test@example.com", "+36701234567", "password", false, false, null, null);
        
        when(em.createStoredProcedureQuery("registration")).thenReturn(spq);
        when(spq.execute()).thenThrow(new RuntimeException("DB error"));

        // Act
        boolean result = userModel.registerUser(testUser);

        // Assert
        assertFalse(result);
    }*/
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.controller;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author herma
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.maven.vintage_project.controller.MessageController.class);
        //resources.add(com.iakk.backendvizsga.controller.UserController.class);
        resources.add(com.maven.vintage_project.controller.ProductController.class);
        resources.add(com.maven.vintage_project.controller.TicketController.class);
        resources.add(com.maven.vintage_project.controller.UserController.class);
    }
    
}

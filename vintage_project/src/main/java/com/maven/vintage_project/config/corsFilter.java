/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

/**
 *
 * @author herma
 */
public class corsFilter implements ContainerResponseFilter{
    private static final boolean DEBUG_MODE = false;
    private static final List<String> ALLOWED_URLS = Arrays.asList(
            "https://localhost:8088"
    );

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (DEBUG_MODE) {
            // Minden kérést átenged
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        } else {
            // Csak engedélyezett URL-eket enged át
            String url = requestContext.getHeaderString("Origin");
            if (url != null && ALLOWED_URLS.contains(url)) {
                responseContext.getHeaders().add("Access-Control-Allow-Origin", url);
            }
        }

        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization, token, x-requested-with, cache-control, Pragma, attachmenturl, Expires");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }

}

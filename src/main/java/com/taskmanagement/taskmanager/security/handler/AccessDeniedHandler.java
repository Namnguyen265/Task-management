package com.taskmanagement.taskmanager.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

public interface AccessDeniedHandler {

    public void handle(HttpServletRequest rs, HttpServletResponse rp, AccessDeniedException ex) throws IOException;
}

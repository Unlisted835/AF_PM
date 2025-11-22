package com.example.af;

import java.util.List;

public class ApplicationContext {
    private ApplicationContext() {}
    private static ApplicationContext _instance;
    public static ApplicationContext instance() {
        if (_instance == null) {
            _instance = new ApplicationContext();
        }
        return _instance;
    }
    public Remedio remedioAtual;
    public List<Remedio> listaAtual;
}

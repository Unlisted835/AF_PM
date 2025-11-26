package com.example.af;

import java.time.LocalTime;
import java.util.ArrayList;
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
    public Remedio remedioAtual = new Remedio();
    public List<Remedio> listaAtual = new ArrayList<>();
    public LocalTime now() {
        return LocalTime.now().minusHours(3).withNano(0);
    }
    public String geminiApiKey = "";
    public final String geminiInstructions = "Você é um farmacêutico criativo que deve criar descrições adequadas, curtas e interessantes de remédios baseando-se apenas nos nomes destes.";
    public final String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    public final String geminiApiKeyHeader = "x-goog-api-key";
}

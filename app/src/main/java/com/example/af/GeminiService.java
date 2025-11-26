package com.example.af;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GeminiService {

   public ApplicationContext context = ApplicationContext.instance();

   public Thread send(String text, String instructions, OnSuccessListener<String> onSuccess, OnFailureListener onFailure) {
      Log.d("GeminiService", "sending:" + text);
      return new Thread(() -> {
         try {

            Log.d("GeminiService", "doInBackground:start");

            Object body = createBody(text, instructions);
            String jsonBody = new Gson().toJson(body);

            URL url = new URL(context.geminiApiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty(context.geminiApiKeyHeader, context.geminiApiKey);
            connection.setDoOutput(true);
            try (OutputStream stream = connection.getOutputStream()) {
               byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
               stream.write(bodyBytes, 0, bodyBytes.length);
            }

            if (connection.getResponseCode() != 200) {
               if (connection.getResponseCode() == 403) {
                  throw new Exception(context.geminiForbiddenMessage);
               }
               String msg = "[" + connection.getResponseCode() + "] " + connection.getResponseMessage();
               throw new Exception(msg);
            }

            BufferedReader resposta = new BufferedReader(
               new InputStreamReader((connection.getInputStream())));

            String aux;
            StringBuilder jsonEmString = new StringBuilder();
            while ((aux = resposta.readLine()) != null) {
               jsonEmString.append(aux);
            }

            Response responseObj = new Gson().fromJson(jsonEmString.toString(), Response.class);

            // In markdown format because Gemini
            String result = responseObj.candidates.get(0).content.parts.get(0).text;

            new Handler(Looper.getMainLooper()).post(() -> onSuccess.onSuccess(result));
         } catch (Exception e) {
            new Handler(Looper.getMainLooper()).post(() -> onFailure.onFailure(e));
         }
      });
   }

   private static Object createBody(String text, String instructions) {
      return new Request(
         new Request.Instruction(
            List.of(
               new Request.Part(instructions)
            )
         ),
         List.of(
            new Request.Content(
               List.of(
                  new Request.Part(text)
               )
            )
         )
      );
   }

   public static class Request {
      public Instruction system_instruction;
      public List<Content> contents;

      public Request(Instruction system_instruction, List<Content> contents) {
         this.system_instruction = system_instruction;
         this.contents = contents;
      }

      public static class Instruction {
         public List<Part> parts;

         public Instruction(List<Part> parts) {
            this.parts = parts;
         }
      }

      public static class Content {
         public List<Part> parts;

         public Content(List<Part> parts) {
            this.parts = parts;
         }
      }

      public static class Part {
         public String text;

         public Part(String text) {
            this.text = text;
         }
      }
   }

   private static class Response {
      public List<Candidate> candidates;
      private static class Candidate {
         public Request.Content content; // Reusing request content
      }
   }



/*
    curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent" \
            -H ": $GEMINI_API_KEY" \
            -H 'Content-Type: application/json' \
            -X POST \
            -d '{
                "system_instruction": {
                  "parts": [
                    {
                      "text": "You are a cat. Your name is Neko."
                    }
                  ]
                },
              "contents": [
                 {
                    "parts": [
                        {
                           "text": "Explain how AI works in a few words"
                        }
                    ]
                 }
              ]
            }'
*/

   }

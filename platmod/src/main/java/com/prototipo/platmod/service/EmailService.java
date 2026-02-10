package com.prototipo.platmod.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY}") // Lee la clave de Render
    private String apiKey;

    public void enviarCodigoVerificacion(String correoDestino, String codigo) {
        try {
            // El JSON que pide Resend
            String body = String.format("""
                {
                    "from": "onboarding@resend.dev",
                    "to": ["%s"],
                    "subject": "Código de Verificación - Platmod",
                    "html": "<p>Tu código es: <strong>%s</strong></p>"
                }
                """, correoDestino, codigo);

            // Crear la petición HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            // Enviar
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("✅ Correo enviado con Resend a: " + correoDestino);
            } else {
                System.err.println("❌ Error Resend: " + response.body());
            }

        } catch (Exception e) {
            System.err.println("❌ Error fatal enviando correo: " + e.getMessage());
        }
    }
}
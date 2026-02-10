package com.prototipo.platmod.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCodigoVerificacion(String correoDestino, String codigo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("tu_correo@gmail.com"); // El mismo que pusiste en application.properties
            message.setTo(correoDestino);
            message.setSubject("Codigo de Verificación - Platmod");
            message.setText("Hola,\n\nTu codigo de verificacion es: " + codigo +
                    "\n\nPor favor ingresalo en la aplicación para activar tu cuenta.\n\nSaludos,\nEquipo Platmod");

            mailSender.send(message);
            System.out.println("📧 Correo enviado a: " + correoDestino);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar correo: " + e.getMessage());
        }
    }
}

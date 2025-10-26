package org.example;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Utilitário para envio de e-mails via SMTP (JavaMail / Jakarta Mail).
 * Lê remetente e senha de variáveis de ambiente:
 *   EMAIL_USER  -> e-mail remetente (ex: seuemail@gmail.com)
 *   EMAIL_PASS  -> senha de app (app password)
 */
public class EmailUtil {

    public static void enviar(String destinatario, String assunto, String corpo) throws Exception {
        // Lê credenciais das variáveis de ambiente (mais seguro)
        final String remetente = System.getenv("EMAIL_USER");
        final String senha = System.getenv("EMAIL_PASS");

        if (remetente == null || senha == null) {
            throw new IllegalStateException("Variáveis de ambiente EMAIL_USER e EMAIL_PASS não definidas.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remetente, senha);
            }
        });

        // Habilite se quiser ver o diálogo SMTP para depuração
        // session.setDebug(true);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(remetente));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject(assunto);
        message.setText(corpo);

        Transport.send(message);
    }
}

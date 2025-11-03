/*
 * Copyright 2024 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sosd.insightnews.email.impl;


import com.sosd.insightnews.email.Email;
import com.sosd.insightnews.email.EmailConfig;
import com.sosd.insightnews.email.EmailService;
import com.sun.mail.smtp.SMTPSSLTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class DefaultEmailService implements EmailService {

    private final Logger logger = LoggerFactory.getLogger(DefaultEmailService.class);
    private final ExecutorService sender = Executors.newScheduledThreadPool(1);

    @Resource
    private EmailConfig emailConfig;

    @Override
    public void send(Email email) {

        SMTPSSLTransport t = null;
        try {
            Properties prop = System.getProperties();
            Session session = Session.getInstance(prop, null);

            email.setSenderEmailAddress(emailConfig.getSender());

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(email.getSenderEmailAddress()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getRecipientsString(), false));
            msg.setSubject(email.getSubject());
            msg.setDataHandler(new DataHandler(new HTMLDataSource(email.getBody())));

            String host = emailConfig.getHost();
            String sender = emailConfig.getSender();
            String password = emailConfig.getPassword();

            t = (SMTPSSLTransport) session.getTransport(emailConfig.getProtocol());
            t.connect(host, sender, password);
            msg.saveChanges();
            t.sendMessage(msg, msg.getAllRecipients());
            logger.debug("email response: {}", t.getLastServerResponse());
        } catch (Exception e) {
            logger.error("send email failed.", e);
        } finally {
            if (t != null) {
                try {
                    t.close();
                } catch (Exception e) {
                    // nothing
                }
            }
        }
    }

    static class HTMLDataSource implements DataSource {

        private final String html;

        HTMLDataSource(String htmlString) {
            html = htmlString;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (html == null) {
                throw new IOException("html message is null!");
            }
            return new ByteArrayInputStream(html.getBytes());
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("This DataHandler cannot write HTML");
        }

        @Override
        public String getContentType() {
            return "text/html";
        }

        @Override
        public String getName() {
            return "HTMLDataSource";
        }
    }
}

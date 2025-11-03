package com.sosd.insightnews.email.builder;

import com.sosd.insightnews.email.Email;

import java.util.Collections;
import java.util.List;

public abstract class AbstractEmailBuilder {
    /**
     * email subject
     */
    protected abstract String subject();

    /**
     * email body content
     */
    protected abstract String emailContent();

    public Email build(List<String> recipients) {
        Email email = new Email();
        email.setSubject(subject());
        email.setRecipients(recipients);
        email.setBody(emailContent());

        return email;
    }

    public Email build(String recipient) {
        Email email = new Email();
        email.setSubject(subject());
        email.setRecipients(Collections.singletonList(recipient));
        email.setBody(emailContent());

        return email;
    }
}

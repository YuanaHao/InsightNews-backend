package com.sosd.insightnews.email.builder;

public class FeedbackEmailBuilder extends AbstractEmailBuilder {

    private final String feedback;

    public FeedbackEmailBuilder(String feedback) {
        this.feedback = feedback;
    }

    @Override
    protected String subject() {
        return "【冰鉴】用户反馈";
    }

    @Override
    protected String emailContent() {
        return feedback;
    }

}

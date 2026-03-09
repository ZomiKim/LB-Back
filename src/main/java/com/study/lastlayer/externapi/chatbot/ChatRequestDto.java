package com.study.lastlayer.externapi.chatbot;

public class ChatRequestDto {

    private String question;
    private String name;
    private Long point;

    public ChatRequestDto() {}

    public ChatRequestDto(String question, String name, Long point) {
        this.question = question;
        this.name = name;
        this.point = point;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPoint() {
        return point;
    }

    public void setPoint(Long point) {
        this.point = point;
    }
}

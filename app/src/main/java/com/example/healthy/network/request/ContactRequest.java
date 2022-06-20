package com.example.healthy.network.request;

public class ContactRequest {
    private Long type;
    private String content;
    private String email;

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ContactRequest(Long type, String content, String email) {
        this.type = type;
        this.content = content;
        this.email = email;
    }
}

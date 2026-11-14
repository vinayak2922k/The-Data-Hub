package com.datahub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating or updating a Post.
 * Validates incoming JSON payload fields.
 */
public class PostRequest {

    @NotBlank(message = "Title is required and cannot be blank")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;

    @NotBlank(message = "Content is required and cannot be blank")
    @Size(min = 5, message = "Content must be at least 5 characters long")
    private String content;

    @NotBlank(message = "Author is required and cannot be blank")
    @Size(min = 2, max = 60, message = "Author name must be between 2 and 60 characters")
    private String author;

    public PostRequest() {
    }

    public PostRequest(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

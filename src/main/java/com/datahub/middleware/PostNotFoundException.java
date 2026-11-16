package com.datahub.middleware;

/**
 * Custom runtime exception thrown when a requested Post ID is not found in the in-memory database.
 */
public class PostNotFoundException extends RuntimeException {

    private final Long postId;

    public PostNotFoundException(Long postId) {
        super("Post not found with ID: " + postId);
        this.postId = postId;
    }

    public Long getPostId() {
        return postId;
    }
}


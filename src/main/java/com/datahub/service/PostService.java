package com.datahub.service;

import com.datahub.dto.PostRequest;
import com.datahub.middleware.PostNotFoundException;
import com.datahub.model.Post;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service managing in-memory blog posts.
 * Uses an AtomicLong for thread-safe ID generation and synchronized blocks
 * over the internal ArrayList to maintain complete thread safety.
 */
@Service
public class PostService {

    // In-memory data store holding posts as required
    private final List<Post> blogPosts = new ArrayList<>();

    // Atomic counter for sequential, thread-safe ID allocation
    private final AtomicLong idCounter = new AtomicLong(0);

    @Value("${app.seed:false}")
    private boolean seedEnabled;

    /**
     * Initializes seed data on startup ONLY when app.seed=true.
     * By default, app.seed is false so the in-memory list starts empty.
     */
    @PostConstruct
    public void init() {
        if (seedEnabled) {
            seedInitialPosts();
        }
    }

    private void seedInitialPosts() {
        createPost(new PostRequest(
                "Welcome to The Data Hub",
                "The Data Hub is a high-performance RESTful API server built with Spring Boot 3 and Java 17+.",
                "System Architect"
        ));
        createPost(new PostRequest(
                "In-Memory State & Thread Safety",
                "Learn how AtomicLong and synchronised collections power low-latency in-memory data structures.",
                "Lead Engineer"
        ));
        createPost(new PostRequest(
                "Microservices & Modern Architecture",
                "Best practices for containerization, middleware filters, and standardized API response formats.",
                "DevOps Specialist"
        ));
    }

    /**
     * Retrieves all posts.
     * Returns a defensive copy to prevent external mutation.
     */
    public List<Post> getAllPosts() {
        synchronized (blogPosts) {
            return new ArrayList<>(blogPosts);
        }
    }

    /**
     * Retrieves a single post by its unique ID.
     * Throws PostNotFoundException if the ID does not exist.
     */
    public Post getPostById(Long id) {
        synchronized (blogPosts) {
            return blogPosts.stream()
                    .filter(post -> post.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new PostNotFoundException(id));
        }
    }

    /**
     * Creates a new post with a generated unique ID and timestamps.
     */
    public Post createPost(PostRequest request) {
        Long nextId = idCounter.incrementAndGet();
        LocalDateTime now = LocalDateTime.now();

        Post newPost = new Post(
                nextId,
                request.getTitle(),
                request.getContent(),
                request.getAuthor(),
                now,
                now
        );

        synchronized (blogPosts) {
            blogPosts.add(newPost);
        }

        return newPost;
    }

    /**
     * Updates an existing post by ID.
     * Throws PostNotFoundException if not found.
     */
    public Post updatePost(Long id, PostRequest request) {
        synchronized (blogPosts) {
            Post existingPost = blogPosts.stream()
                    .filter(post -> post.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new PostNotFoundException(id));

            existingPost.setTitle(request.getTitle());
            existingPost.setContent(request.getContent());
            existingPost.setAuthor(request.getAuthor());
            existingPost.setUpdatedAt(LocalDateTime.now());

            return existingPost;
        }
    }

    /**
     * Deletes a post by ID using removeIf.
     * Throws PostNotFoundException if no element was removed.
     */
    public void deletePost(Long id) {
        synchronized (blogPosts) {
            boolean removed = blogPosts.removeIf(post -> post.getId().equals(id));
            if (!removed) {
                throw new PostNotFoundException(id);
            }
        }
    }

    /**
     * Returns total count of posts in memory.
     */
    public int count() {
        synchronized (blogPosts) {
            return blogPosts.size();
        }
    }
}

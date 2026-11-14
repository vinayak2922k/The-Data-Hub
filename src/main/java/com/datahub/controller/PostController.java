package com.datahub.controller;

import com.datahub.dto.ApiResponse;
import com.datahub.dto.PostRequest;
import com.datahub.model.Post;
import com.datahub.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Blog Posts.
 * Provides endpoints for Full CRUD: Create, Read All, Read One, Update, Delete.
 * All responses wrap data in a standardized ApiResponse format { success, message, data }.
 */
@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * GET /posts
     * Retrieves all posts stored in memory.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(ApiResponse.success("Posts retrieved successfully", posts));
    }

    /**
     * GET /posts/{id}
     * Retrieves a single post by its unique ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(ApiResponse.success("Post retrieved successfully", post));
    }

    /**
     * POST /posts
     * Creates a new post after validating title, content, and author.
     * Returns HTTP 201 Created with the new resource.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Post>> createPost(@Valid @RequestBody PostRequest request) {
        Post createdPost = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Post created successfully", createdPost));
    }

    /**
     * PUT /posts/{id}
     * Updates an existing post's title, content, and author.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> updatePost(@PathVariable Long id,
                                                        @Valid @RequestBody PostRequest request) {
        Post updatedPost = postService.updatePost(id, request);
        return ResponseEntity.ok(ApiResponse.success("Post updated successfully", updatedPost));
    }

    /**
     * DELETE /posts/{id}
     * Deletes a post by its ID and returns a confirmation message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully with ID: " + id, null));
    }
}

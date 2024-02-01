package com.nayoon.activity_service.post.repository;

import com.nayoon.activity_service.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}

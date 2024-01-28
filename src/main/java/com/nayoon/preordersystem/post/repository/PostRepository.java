package com.nayoon.preordersystem.post.repository;

import com.nayoon.preordersystem.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}

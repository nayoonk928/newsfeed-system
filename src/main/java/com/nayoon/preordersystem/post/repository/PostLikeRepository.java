package com.nayoon.preordersystem.post.repository;

import com.nayoon.preordersystem.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  boolean existsByPostIdAndUserId(Long postId, Long userId);

}

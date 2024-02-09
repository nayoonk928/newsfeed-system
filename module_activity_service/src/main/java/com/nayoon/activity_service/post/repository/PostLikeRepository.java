package com.nayoon.activity_service.post.repository;

import com.nayoon.activity_service.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  boolean existsByPostIdAndUserId(Long postId, Long userId);

}

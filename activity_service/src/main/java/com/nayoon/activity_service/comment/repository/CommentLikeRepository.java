package com.nayoon.activity_service.comment.repository;

import com.nayoon.activity_service.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

  boolean existsByCommentIdAndUserId(Long postId, Long userId);

}

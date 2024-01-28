package com.nayoon.preordersystem.comment.repository;

import com.nayoon.preordersystem.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

  boolean existsByCommentIdAndUserId(Long postId, Long userId);

}

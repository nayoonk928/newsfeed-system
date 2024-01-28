package com.nayoon.preordersystem.comment.repository;

import com.nayoon.preordersystem.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}

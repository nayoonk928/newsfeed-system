package com.nayoon.activity_service.comment.repository;

import com.nayoon.activity_service.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}

package com.nayoon.preordersystem.newsfeed.repository;

import com.nayoon.preordersystem.newsfeed.entity.Newsfeed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsfeedRepository extends JpaRepository<Newsfeed, Long>, NewsfeedQRepository {

}

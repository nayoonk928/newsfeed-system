package com.nayoon.activity_service.newsfeed.repository;

import com.nayoon.activity_service.newsfeed.entity.Newsfeed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsfeedRepository extends JpaRepository<Newsfeed, Long>, NewsfeedQRepository {

}

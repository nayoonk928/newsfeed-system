package com.nayoon.user_service.newsfeed.repository;

import com.nayoon.user_service.newsfeed.entity.Newsfeed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsfeedRepository extends JpaRepository<Newsfeed, Long>, NewsfeedQRepository {

}

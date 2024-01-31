package com.nayoon.user_service.newsfeed.repository;

import com.nayoon.user_service.newsfeed.entity.Newsfeed;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsfeedQRepository {

  Page<Newsfeed> filterNewsfeeds(Long userId, List<Long> followingIds, Pageable pageable);

}

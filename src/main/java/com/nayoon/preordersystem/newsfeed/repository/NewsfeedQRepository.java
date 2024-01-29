package com.nayoon.preordersystem.newsfeed.repository;

import com.nayoon.preordersystem.newsfeed.entity.Newsfeed;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsfeedQRepository {

  Page<Newsfeed> filterNewsfeeds(Long userId, List<Long> followingIds, Pageable pageable);

}

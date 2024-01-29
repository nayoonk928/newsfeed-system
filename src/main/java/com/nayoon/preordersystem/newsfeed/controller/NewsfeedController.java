package com.nayoon.preordersystem.newsfeed.controller;

import com.nayoon.preordersystem.newsfeed.service.NewsfeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/newsfeeds")
public class NewsfeedController {

  private final NewsfeedService newsfeedService;

}

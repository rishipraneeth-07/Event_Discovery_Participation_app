package com.college.eventapp.service;

import java.util.List;

public interface UserInterestService {
    List<String> getUserInterests(Long userId);
    List<String> replaceUserInterests(Long userId, List<String> interests);
}

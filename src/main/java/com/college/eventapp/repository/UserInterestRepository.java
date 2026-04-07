package com.college.eventapp.repository;

import com.college.eventapp.model.User;
import com.college.eventapp.model.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUserOrderByInterestAsc(User user);
    void deleteByUser(User user);
}

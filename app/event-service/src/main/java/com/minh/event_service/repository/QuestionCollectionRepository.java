package com.minh.event_service.repository;

import com.minh.event_service.entity.QuestionCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionCollectionRepository extends JpaRepository<QuestionCollection, String> {

}

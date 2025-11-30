package com.minh.event_service.repository;

import com.minh.event_service.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    @Query(value = """
            select q from Question q where q.collectionId = :id
            """)
    List<Question> findQuestionByQuestionCollectionId(@Param("id") String id);


    @Modifying
    @Query(value = """
            delete from Question q where q.collectionId = :id
            """)
    void deleteAllByCollectionId(@Param("id") String id);
}

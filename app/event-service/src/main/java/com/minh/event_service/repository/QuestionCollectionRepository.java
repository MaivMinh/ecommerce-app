package com.minh.event_service.repository;

import com.minh.event_service.entity.QuestionCollection;
import com.minh.event_service.payload.request.SearchQuestionCollectionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionCollectionRepository extends JpaRepository<QuestionCollection, String> {

    @Query(value = """
            select qc from QuestionCollection  qc
            where (coalesce(:#{#request.title}, null) is null or qc.title like %:#{#request.title}%)
            """)
    Page<QuestionCollection> searchQuestionCollections(@Param("request") SearchQuestionCollectionRequest request, Pageable pageable);
}

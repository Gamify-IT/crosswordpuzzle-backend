package com.crosswordservice.repositories;

import com.crosswordservice.baseClasses.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Class to interact with the table question of the db
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>{
    public List<Question> findByInternalId(long internalId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Question WHERE internalId = :internalId")
    Integer deleteByInternalId(long internalId);
}

package com.crosswordservice.repositories;

import com.crosswordservice.data.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * Class to interact with the table question of the db
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>{
}

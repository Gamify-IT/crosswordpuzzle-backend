package de.unistuttgart.crosswordbackend.repositories;

import de.unistuttgart.crosswordbackend.data.Question;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Class to interact with the table question of the db
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {}

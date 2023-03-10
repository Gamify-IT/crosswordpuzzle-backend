package de.unistuttgart.crosswordbackend.repositories;

import de.unistuttgart.crosswordbackend.data.GameResult;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, UUID> {}

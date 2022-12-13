package de.unistuttgart.crosswordbackend.repositories;

import de.unistuttgart.crosswordbackend.data.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, UUID> {
}

package de.unistuttgart.crosswordbackend.repositories;

import de.unistuttgart.crosswordbackend.data.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Class to interact with the table configuration of the db
 */
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, UUID>{
}
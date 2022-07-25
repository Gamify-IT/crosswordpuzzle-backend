package de.unistuttgart.crosswordbackend.repositories;

import de.unistuttgart.crosswordbackend.data.Configuration;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Class to interact with the table configuration of the db
 */
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, UUID> {}

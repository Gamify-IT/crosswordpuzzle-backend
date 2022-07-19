package com.crosswordservice.repositories;

import com.crosswordservice.data.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Class to interact with the table configuration of the db
 */
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long>{
    Configuration findByName(String name);

}
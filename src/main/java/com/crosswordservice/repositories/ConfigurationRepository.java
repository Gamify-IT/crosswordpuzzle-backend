package com.crosswordservice.repositories;

import com.crosswordservice.baseClasses.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Class to interact with the table configuration of the db
 */
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long>{
    Configuration findByName(String name);

}
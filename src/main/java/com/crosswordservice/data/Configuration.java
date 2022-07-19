package com.crosswordservice.data;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Configuration for a crosswordpuzzle with a name
 */
@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "configuration")
public class Configuration {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(
            name = "id",
            updatable = false
    )
    private long id;
    @Column(
            nullable = false,
            unique = true
    )
    private String name;

    public Configuration(String name){
        this.name = name;
    }
}

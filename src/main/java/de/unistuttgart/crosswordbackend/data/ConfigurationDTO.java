package de.unistuttgart.crosswordbackend.data;

import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigurationDTO {

    @Nullable
    UUID id;

    String name;

    Set<QuestionDTO> questions;

    /**
     * The volume level that is setted by the player.
     */
    Integer volumeLevel;

    public ConfigurationDTO(final String name, final Set<QuestionDTO> questions) {
        this.name = name;
        this.questions = questions;
    }
}

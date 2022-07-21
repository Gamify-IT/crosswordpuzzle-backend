package de.unistuttgart.crosswordbackend.mapper;

import de.unistuttgart.crosswordbackend.data.Configuration;
import de.unistuttgart.crosswordbackend.data.ConfigurationDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConfigurationMapper {
    ConfigurationDTO configurationToConfigurationDTO(final Configuration configuration);

    Configuration configurationDTOToConfiguration(final ConfigurationDTO configurationDTO);

    List<ConfigurationDTO> configurationsToConfigurationDTOs(final List<Configuration> configurations);
}

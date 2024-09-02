package de.unistuttgart.crosswordbackend.data;

/**
 * The KeybindingDTO.class contains all data that is retrieved from the overworld-backend
 */
public class KeybindingDTO {
    private String binding;
    private String key;

    public KeybindingDTO() {}

    public KeybindingDTO(String binding, String key) {
        this.binding = binding;
        this.key = key;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

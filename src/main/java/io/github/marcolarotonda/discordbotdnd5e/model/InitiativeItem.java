package io.github.marcolarotonda.discordbotdnd5e.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InitiativeItem {

    private final String name;
    private int initiativeValue;
    private String description = ""; //TODO
    private int damageTaken;
}

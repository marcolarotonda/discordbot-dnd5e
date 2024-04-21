package io.github.marcolarotonda.discordbotdnd5e.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EnemySaver {
    private String name;
    private String description;
    private int initiativeModifier;
    private int quantity;
}

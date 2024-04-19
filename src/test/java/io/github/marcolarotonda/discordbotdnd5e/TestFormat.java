package io.github.marcolarotonda.discordbotdnd5e;

import io.github.marcolarotonda.discordbotdnd5e.model.InitiativeItem;

public class TestFormat {

    public static void main(String[] args) {
        String format = "%-10s%-20s%-10s%-10s";
        String initiativeHeader = String.format(format, "Order", "Name", "Damage", "Value");
        InitiativeItem ii = InitiativeItem.builder()
                .name("Ciccio")
                .initiativeValue(15)
                .damageTaken(0)
                .build();
        String initiativeMessage = String.format(format, 0, ii.getName(), ii.getDamageTaken(), ii.getInitiativeValue());
        String message = initiativeHeader + "\n" + initiativeMessage;

        System.out.println(message);


    }
}

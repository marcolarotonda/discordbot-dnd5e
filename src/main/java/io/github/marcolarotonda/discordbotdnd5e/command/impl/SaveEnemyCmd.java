package io.github.marcolarotonda.discordbotdnd5e.command.impl;

import io.github.marcolarotonda.discordbotdnd5e.command.Command;
import io.github.marcolarotonda.discordbotdnd5e.model.EnemySaver;
import io.github.marcolarotonda.discordbotdnd5e.service.EnemyService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SaveEnemyCmd implements Command {

    private final EnemyService enemyService;

    @Autowired
    public SaveEnemyCmd(EnemyService enemyService) {
        this.enemyService = enemyService;
    }

    @Override
    public String getName() {
        return "enemy-save";
    }

    @Override
    public String getDescription() {
        return "save new enemies";
    }

    @Override
    public Optional<List<OptionData>> getOptions() {
        OptionData name = new OptionData(OptionType.STRING,
                "name",
                "name of the type of enemy",
                true);
        OptionData initiative = new OptionData(OptionType.INTEGER,
                "initiative",
                "the initiative modifier of the enemy",
                true);
        OptionData quantity = new OptionData(OptionType.INTEGER,
                "quantity",
                "the amount of enemies of this type you want to generate",
                true);
        OptionData description = new OptionData(OptionType.STRING,
                "description",
                "optional description of the enemy",
                false);
        return Optional.of(List.of(name, initiative, quantity, description));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String name = event.getOption("name").getAsString();
        String description;
        try {
            description = event.getOption("description").getAsString();
        } catch (NullPointerException e) {
            description = "";
        }
        int initiative;
        try {
            initiative = event.getOption("initiative").getAsInt();
        } catch (NullPointerException e) {
            initiative = 0;
        }
        int quantity;
        try {
            quantity = event.getOption("quantity").getAsInt();
        } catch (NullPointerException e) {
            quantity = 1;
        }

        EnemySaver enemySaver = EnemySaver.builder()
                .name(name)
                .description(description)
                .initiativeModifier(initiative)
                .quantity(quantity)
                .build();

        enemyService.saveEnemies(enemySaver);

        event.reply("Saved!").queue();


    }
}

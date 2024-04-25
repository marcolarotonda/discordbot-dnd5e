package io.github.marcolarotonda.discordbotdnd5e.command.impl;

import io.github.marcolarotonda.discordbotdnd5e.command.Command;
import io.github.marcolarotonda.discordbotdnd5e.service.EnemyTypeService;
import io.github.marcolarotonda.dnd5e.entity.EnemyTypeEntity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GetEnemyTypeCmd implements Command {

    private final EnemyTypeService enemyTypeService;

    @Autowired
    public GetEnemyTypeCmd(EnemyTypeService enemyTypeService) {
        this.enemyTypeService = enemyTypeService;
    }
    @Override
    public String getName() {
        return "enemy-type-get";
    }

    @Override
    public String getDescription() {
        return "get all previously saved types of enemy";
    }

    @Override
    public Optional<List<OptionData>> getOptions() {
        return Optional.empty();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<EnemyTypeEntity> enemyTypes = enemyTypeService.getEnemyTypes();
        String s = enemyTypeService.formatEnemyTypes(enemyTypes);
        event.reply(s).queue();
    }
}

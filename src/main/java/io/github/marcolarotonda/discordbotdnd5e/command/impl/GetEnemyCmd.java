package io.github.marcolarotonda.discordbotdnd5e.command.impl;

import io.github.marcolarotonda.discordbotdnd5e.command.Command;
import io.github.marcolarotonda.discordbotdnd5e.service.EnemyFormatterService;
import io.github.marcolarotonda.dnd5e.entity.EnemyEntity;
import io.github.marcolarotonda.dnd5e.service.EnemyService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GetEnemyCmd implements Command {

    private final EnemyService enemyService;
    private final EnemyFormatterService enemyFormatterService;

    @Autowired
    public GetEnemyCmd(EnemyService enemyService,
            EnemyFormatterService enemyFormatterService) {
        this.enemyService = enemyService;
        this.enemyFormatterService = enemyFormatterService;
    }

    @Override
    public String getName() {
        return "enemy-get";
    }

    @Override
    public String getDescription() {
        return "get all previously saved enemies";
    }

    @Override
    public Optional<List<OptionData>> getOptions() {
        return Optional.empty();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<EnemyEntity> allByAliveTrue = enemyService.findAllByAliveTrue();
        event.reply(enemyFormatterService.getEnemiesFormatted(allByAliveTrue)).queue();
    }
}

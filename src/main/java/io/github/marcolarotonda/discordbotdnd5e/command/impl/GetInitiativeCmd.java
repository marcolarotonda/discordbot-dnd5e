package io.github.marcolarotonda.discordbotdnd5e.command.impl;

import io.github.marcolarotonda.discordbotdnd5e.command.Command;
import io.github.marcolarotonda.dnd5e.entity.InitiativeItemEntity;
import io.github.marcolarotonda.discordbotdnd5e.service.InitiativeService;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GetInitiativeCmd implements Command {

    private final InitiativeService initiativeService;

    @Autowired
    public GetInitiativeCmd(InitiativeService initiativeService) {
        this.initiativeService = initiativeService;
    }

    @Override
    public String getName() {
        return "initiative";
    }

    @Override
    public String getDescription() {
        return "get initiative order";
    }

    @Override
    public Optional<List<OptionData>> getOptions() {
        return Optional.empty();
    }

    @Override
    public void execute(@Nonnull SlashCommandInteractionEvent event) {
        initiativeService.calculateInitiative();
        List<InitiativeItemEntity> initiative = initiativeService.getInitiative();
        if (initiative.isEmpty()) {
            initiative = initiativeService.getCalculatedInitiative();
        }
        String initiativeFormatted = initiativeService.getInitiativeFormatted(initiative);
        event.reply(initiativeFormatted).queue();
    }
}

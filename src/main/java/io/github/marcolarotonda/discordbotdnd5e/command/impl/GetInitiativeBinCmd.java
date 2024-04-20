package io.github.marcolarotonda.discordbotdnd5e.command.impl;

import io.github.marcolarotonda.discordbotdnd5e.command.Command;
import io.github.marcolarotonda.dnd5e.model.InitiativeItem;
import io.github.marcolarotonda.discordbotdnd5e.service.InitiativeService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GetInitiativeBinCmd implements Command {

    private final InitiativeService initiativeService;

    @Autowired
    public GetInitiativeBinCmd(InitiativeService initiativeService) {
        this.initiativeService = initiativeService;
    }
    @Override
    public String getName() {
        return "initiative-bin";
    }

    @Override
    public String getDescription() {
        return "get all the combatant previously removed from initiative";
    }

    @Override
    public Optional<List<OptionData>> getOptions() {
        return Optional.empty();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<InitiativeItem> removedFromInitiative = initiativeService.getInitiativeBin();
        String initiativeBin = initiativeService.getInitiativeFormatted(removedFromInitiative);
        event.reply(initiativeBin).queue();
    }
}

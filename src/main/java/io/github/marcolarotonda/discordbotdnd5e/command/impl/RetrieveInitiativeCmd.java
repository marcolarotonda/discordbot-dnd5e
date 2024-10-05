package io.github.marcolarotonda.discordbotdnd5e.command.impl;

import io.github.marcolarotonda.discordbotdnd5e.command.Command;
import io.github.marcolarotonda.discordbotdnd5e.service.InitiativeService;
import io.github.marcolarotonda.dnd5e.service.InitiativeItemService;
import io.github.marcolarotonda.dnd5e.entity.InitiativeItemEntity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RetrieveInitiativeCmd implements Command {

    private final InitiativeService initiativeService;
    private final InitiativeItemService initiativeItemService;

    @Autowired
    public RetrieveInitiativeCmd(InitiativeService initiativeService,
                                 InitiativeItemService initiativeItemService) {
        this.initiativeService = initiativeService;
        this.initiativeItemService = initiativeItemService;
    }
    @Override
    public String getName() {
        return "initiative-retrieve";
    }

    @Override
    public String getDescription() {
        return "retrieve the initiative order previously saved";
    }

    @Override
    public Optional<List<OptionData>> getOptions() {
        return Optional.empty();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<InitiativeItemEntity> all = initiativeItemService.findAll();

        String message;
        if (all.isEmpty()) {
            message = "There isn't any initiative item to retrieve!";
        } else {
            initiativeService.setInitiative(all);
            message = initiativeService.getInitiativeFormatted(initiativeService.getInitiative());
        }

        event.reply(message).queue();


    }
}

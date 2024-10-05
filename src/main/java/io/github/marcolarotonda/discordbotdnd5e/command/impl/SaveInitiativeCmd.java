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
public class SaveInitiativeCmd implements Command {

    private final InitiativeItemService initiativeItemService;
    private final InitiativeService initiativeService;

    @Autowired
    public SaveInitiativeCmd(InitiativeItemService initiativeItemService,
                             InitiativeService initiativeService) {
        this.initiativeItemService = initiativeItemService;
        this.initiativeService = initiativeService;
    }
    @Override
    public String getName() {
        return "initiative-save";
    }

    @Override
    public String getDescription() {
        return "save the current initiative order";
    }

    @Override
    public Optional<List<OptionData>> getOptions() {
        return Optional.empty();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<InitiativeItemEntity> initiative = initiativeService.getInitiative();
        initiativeItemService.saveInitiative(initiative);
        int size = initiativeItemService.findAll().size();
        event.reply(String.format("Saved %d initiative items!", size)).queue();
    }
}

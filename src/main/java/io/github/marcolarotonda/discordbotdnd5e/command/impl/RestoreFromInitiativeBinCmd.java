package io.github.marcolarotonda.discordbotdnd5e.command.impl;

import io.github.marcolarotonda.discordbotdnd5e.command.Command;
import io.github.marcolarotonda.discordbotdnd5e.model.InitiativeItem;
import io.github.marcolarotonda.discordbotdnd5e.service.InitiativeService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static io.github.marcolarotonda.discordbotdnd5e.service.InitiativeService.INITIATIVE_STARTING_INDEX;

@Component
public class RestoreFromInitiativeBinCmd implements Command {

    private final InitiativeService initiativeService;

    @Autowired
    public RestoreFromInitiativeBinCmd(InitiativeService initiativeService) {
        this.initiativeService = initiativeService;
    }
    @Override
    public String getName() {
        return "initiative-restore-from-bin";
    }

    @Override
    public String getDescription() {
        return "put back into initiative order a previously removed combatant";
    }

    @Override
    public Optional<List<OptionData>> getOptions() {
        OptionData binIndex = new OptionData(OptionType.INTEGER,
                "bin-index",
                "the actual index from the bin of the combatant you want to restore",
                true)
                .setMinValue(INITIATIVE_STARTING_INDEX);
        OptionData desiredIndex = new OptionData(OptionType.INTEGER,
                "initiative-index",
                "the new initiative index of the combatant",
                true)
                .setMinValue(INITIATIVE_STARTING_INDEX);
        return Optional.of(List.of(binIndex, desiredIndex));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        int binIndex = event.getOption("bin-index").getAsInt();
        int desiredInitiativeIndex = event.getOption("initiative-index").getAsInt();

        int initiativeMaxIndex = initiativeService.getInitiativeMaxIndex();
        int binMaxIndex = initiativeService.getBinMaxIndex();

        if (binIndex > binMaxIndex) {
            event.reply(String.format("You are trying to restore an item with index %d, while bin has %d as max index", binIndex, binMaxIndex)).queue();
        } else if (desiredInitiativeIndex > initiativeMaxIndex + INITIATIVE_STARTING_INDEX) {
            event.reply(String.format("You set %d as desired index, but it could be at most %d", desiredInitiativeIndex, initiativeMaxIndex + INITIATIVE_STARTING_INDEX)).queue();
        } else {
            initiativeService.restoreFromBin(binIndex, desiredInitiativeIndex);
            List<InitiativeItem> initiative = initiativeService.getInitiative();
            String initiativeFormatted = initiativeService.getInitiativeFormatted(initiative);
            event.reply(initiativeFormatted).queue();
        }


    }
}

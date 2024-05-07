package io.github.marcolarotonda.discordbotdnd5e.command.impl;

import io.github.marcolarotonda.discordbotdnd5e.command.Command;
import io.github.marcolarotonda.dnd5e.entity.InitiativeItemEntity;
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
public class RemoveFromInitiativeCmd implements Command {

    private final InitiativeService initiativeService;

    @Autowired
    public RemoveFromInitiativeCmd(InitiativeService initiativeService) {
        this.initiativeService = initiativeService;
    }
    @Override
    public String getName() {
        return "initiative-remove";
    }

    @Override
    public String getDescription() {
        return "remove a combatant from the initiative";
    }

    @Override
    public Optional<List<OptionData>> getOptions() {
        OptionData index = new OptionData(OptionType.INTEGER,
                "index",
                "index of the combatant you want to remove from initiative",
                true)
                .setMinValue(INITIATIVE_STARTING_INDEX);
        return Optional.of(List.of(index));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        int index = event.getOption("index").getAsInt();
        int maxIndex = initiativeService.getInitiativeMaxIndex();
        if (index > maxIndex) {
            event.reply(String.format("You are trying to remove a combatant with index %d, which is greater than the max index", index)).queue();
        } else {
            initiativeService.removeFromInitiative(index);
            List<InitiativeItemEntity> initiative = initiativeService.getInitiative();
            String initiativeFormatted = initiativeService.getInitiativeFormatted(initiative);
            event.reply(initiativeFormatted).queue();
        }

    }
}

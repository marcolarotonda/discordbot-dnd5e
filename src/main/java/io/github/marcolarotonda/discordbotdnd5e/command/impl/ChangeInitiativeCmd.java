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
public class ChangeInitiativeCmd implements Command {

    private final InitiativeService initiativeService;

    @Autowired
    public ChangeInitiativeCmd(InitiativeService initiativeService) {
        this.initiativeService = initiativeService;
    }

    @Override
    public String getName() {
        return "initiative-change";
    }

    @Override
    public String getDescription() {
        return "move an element from a starting position to a target position";
    }

    @Override
    public Optional<List<OptionData>> getOptions() {

        OptionData startingPosition = new OptionData(OptionType.INTEGER,
                "initial",
                "initial position of the character you want to change order",
                true)
                .setMinValue(INITIATIVE_STARTING_INDEX);


        OptionData finalPosition = new OptionData(OptionType.INTEGER,
                "final",
                "final position of the character you want to change order",
                true)
                .setMinValue(INITIATIVE_STARTING_INDEX);

        return Optional.of(List.of(startingPosition, finalPosition));


    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (initiativeService.getInitiative().isEmpty()) {
            event.reply("Initiative has not been calculated yet").queue();
        } else {
            int startingPosition = event.getOption("initial").getAsInt();
            int finalPosition = event.getOption("final").getAsInt();

            int maxIndex = initiativeService.getMaxIndex();

            if (startingPosition > maxIndex) {
                event.reply(String.format("You input %d as initial position, but initiative has only elements up to index %d", startingPosition, maxIndex)).queue();
            } else if (finalPosition > maxIndex) {
                event.reply(String.format("You input %d as final position, but initiative has only elements up to index %d", finalPosition, maxIndex)).queue();
            } else if (startingPosition == finalPosition) {
                event.reply(String.format("Attention! You want to change initial position = %d with final position = %d!",
                        startingPosition, finalPosition)).queue();
            } else {
                initiativeService.changeInitiative(startingPosition, finalPosition);
                List<InitiativeItem> initiative = initiativeService.getInitiative();
                String initiativeFormatted = initiativeService.getInitiativeFormatted(initiative);
                event.reply(initiativeFormatted).queue();
            }
        }
    }
}

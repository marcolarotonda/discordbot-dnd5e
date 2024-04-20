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
public class AddDamageCmd implements Command {

    private final InitiativeService initiativeService;

    @Autowired
    public AddDamageCmd(InitiativeService initiativeService) {
        this.initiativeService = initiativeService;
    }
    @Override
    public String getName() {
        return "initiative-damage";
    }

    @Override
    public String getDescription() {
        return "increase the damage taken by a combatant";
    }

    @Override
    public Optional<List<OptionData>> getOptions() {
        OptionData index = new OptionData(OptionType.INTEGER,
                "index",
                "index of the combatant you want to damage",
                true)
                .setMinValue(INITIATIVE_STARTING_INDEX);
        OptionData damage = new OptionData(OptionType.INTEGER,
                "damage",
                "the amount of damage (positive or negative) you want to apply",
                true);
        return Optional.of(List.of(index, damage));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        int maxIndex = initiativeService.getInitiativeMaxIndex();
        int index = event.getOption("index").getAsInt();
        int damage = event.getOption("damage").getAsInt();
        if (index > maxIndex) {
            event.reply(String.format("You cannot apply damage to combatant with index %d, while the max index is %d", index, maxIndex)).queue();
        } else if (damage == 0) {
            event.reply(String.format("You are applying %d damage", damage)).queue();
        } else {
            initiativeService.addDamageToInitiativeItem(index, damage);
            List<InitiativeItem> initiative = initiativeService.getInitiative();
            String initiativeFormatted = initiativeService.getInitiativeFormatted(initiative);
            event.reply(initiativeFormatted).queue();
        }
    }
}

package io.github.marcolarotonda.discordbotdnd5e.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Optional;

public interface Command {

    String getName();
    String getDescription();
    Optional<List<OptionData>> getOptions();
    void execute(SlashCommandInteractionEvent event);
}

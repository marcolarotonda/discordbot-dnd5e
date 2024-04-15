package io.github.marcolarotonda.discordbotdnd5e.service;

import io.github.marcolarotonda.discordbotdnd5e.command.impl.Command;
import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Listener extends ListenerAdapter {
    private final List<Command> commandList;
    @Autowired
    public Listener(List<Command> commandList) {
        this.commandList = commandList;
    }

    @Override
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        for (Command command : commandList) {
            CommandCreateAction commandCreateAction = event.getGuild().upsertCommand(command.getName(), command.getDescription());
            command.getOptions().ifPresentOrElse(
                    options -> commandCreateAction.addOptions(options).queue(),
                    commandCreateAction::queue);
        }
    }


    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        commandList.stream()
                .filter(command -> command.getName().equals(event.getName()))
                .findFirst()
                .ifPresent(command -> command.execute(event));
    }

}

package io.github.marcolarotonda.discordbotdnd5e.configuration;

import io.github.marcolarotonda.discordbotdnd5e.command.impl.Command;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CommandListProvider {

    @Bean
    public List<Command> commandList() {
        return new ArrayList<>();
    }
}

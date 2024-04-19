package io.github.marcolarotonda.discordbotdnd5e;

import io.github.marcolarotonda.discordbotdnd5e.service.Listener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;


@Service
public class BotRunner implements ApplicationRunner {

    private final String token;
    private final Listener listener;

    @Autowired
    public BotRunner(@Value("${token}") String token,
                     Listener listener) {
        this.listener = listener;
        this.token = token;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        JDABuilder.createLight(token)
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(listener)
                .build();
    }
}

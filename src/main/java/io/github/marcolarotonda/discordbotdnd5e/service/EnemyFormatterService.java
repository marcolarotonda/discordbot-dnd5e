package io.github.marcolarotonda.discordbotdnd5e.service;

import io.github.marcolarotonda.dnd5e.entity.EnemyEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.marcolarotonda.discordbotdnd5e.utils.StringUtils.BLOCK_DELIMITER;
import static io.github.marcolarotonda.discordbotdnd5e.utils.StringUtils.BOLD_DELIMITER;

@Service
public class EnemyFormatterService {


    public String getEnemiesFormatted(List<EnemyEntity> enemies) {
        return formatEnemies(enemies);
    }

    public String formatEnemies(List<EnemyEntity> enemies) {
        String format = getStringFormatter(enemies);

        StringBuilder message = new StringBuilder();
        message.append(BOLD_DELIMITER)
                .append(BLOCK_DELIMITER)
                .append(String.format(format, "Name", "Tag", "Initiative Modifier", "Count"))
                .append(BLOCK_DELIMITER)
                .append(BOLD_DELIMITER);

        if (!enemies.isEmpty()) {
            Map<List<Object>, Long> collect = enemies.stream()
                    .collect(Collectors.groupingBy(enemyEntity ->
                                    List.of(enemyEntity.getName(),
                                            enemyEntity.getTag(),
                                            enemyEntity.getInitiativeModifier()),
                            Collectors.counting()));

            String collect1 = collect.entrySet()
                    .stream()
                    .map(entry -> String.format(format,
                            entry.getKey().get(0), //Name
                            ((Optional<String>) (entry.getKey().get(1))).orElse(""), //Tag
                            entry.getKey().get(2), //Initiative modifier
                            entry.getValue() //Count
                    ))
                    .collect(Collectors.joining("\n"));

            message.append(BLOCK_DELIMITER)
                    .append(collect1)
                    .append(BLOCK_DELIMITER);
        }

        return message.toString();

    }

    private String getStringFormatter(List<EnemyEntity> enemies) {
        return "%-" + getSizeOfLongestName(enemies) + "s%-" + getSizeOfLongestTag(enemies) + "s%-23s%-10s\n";
    }

    private int getSizeOfLongestName(List<EnemyEntity> enemies) {
        return enemies
                .stream()
                .map(enemyEntity -> enemyEntity.getName().length())
                .max(Comparator.naturalOrder())
                .orElse(10) + 5;
    }

    private int getSizeOfLongestTag(List<EnemyEntity> enemies) {
        return enemies
                .stream()
                .map(enemy -> enemy.getTag().orElse("").length())
                .max(Comparator.naturalOrder())
                .orElse(10) + 10;
    }


}

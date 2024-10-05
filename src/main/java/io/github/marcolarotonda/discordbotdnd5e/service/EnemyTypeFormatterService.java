package io.github.marcolarotonda.discordbotdnd5e.service;

import io.github.marcolarotonda.dnd5e.entity.EnemyTypeEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.marcolarotonda.discordbotdnd5e.utils.StringUtils.BLOCK_DELIMITER;
import static io.github.marcolarotonda.discordbotdnd5e.utils.StringUtils.BOLD_DELIMITER;

@Service
public class EnemyTypeFormatterService {

    public String formatEnemyTypes(List<EnemyTypeEntity> enemyTypes) {
        String format = getStringFormatter(enemyTypes);

        StringBuilder message = new StringBuilder();
        message.append(BOLD_DELIMITER)
                .append(BLOCK_DELIMITER)
                .append(String.format(format, "Name", "Initiative Modifier"))
                .append(BLOCK_DELIMITER)
                .append(BOLD_DELIMITER);

        if (!enemyTypes.isEmpty()) {
            String collect = enemyTypes.stream()
                    .map(et -> String.format(format,
                            et.getName(),
                            et.getInitiativeModifier()))
                    .collect(Collectors.joining("\n"));
            message.append(BLOCK_DELIMITER)
                    .append(collect)
                    .append(BLOCK_DELIMITER);
        }

        return message.toString();
    }

    private String getStringFormatter(List<EnemyTypeEntity> enemyTypes) {
        return "%-" + getSizeOfLongestName(enemyTypes) + "s%-10s\n";
    }

    private int getSizeOfLongestName(List<EnemyTypeEntity> enemyTypes) {
        return enemyTypes
                .stream()
                .map(enemyTypeEntity -> enemyTypeEntity.getName().length())
                .max(Comparator.naturalOrder())
                .orElse(10) + 5;
    }
}

package io.github.marcolarotonda.discordbotdnd5e.service;

import io.github.marcolarotonda.discordbotdnd5e.model.EnemySaver;
import io.github.marcolarotonda.dnd5e.entity.EnemyEntity;
import io.github.marcolarotonda.dnd5e.entity.EnemyTypeEntity;
import io.github.marcolarotonda.dnd5e.repository.EnemyRepository;
import io.github.marcolarotonda.dnd5e.service.EnemyTypeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.marcolarotonda.discordbotdnd5e.utils.StringUtils.BLOCK_DELIMITER;
import static io.github.marcolarotonda.discordbotdnd5e.utils.StringUtils.BOLD_DELIMITER;

@Service
public class EnemyService {
    private final EnemyRepository enemyRepository;
    private final EnemyTypeService enemyTypeService;

    @Autowired
    public EnemyService(EnemyRepository enemyRepository,
                        EnemyTypeService enemyTypeService) {
        this.enemyRepository = enemyRepository;
        this.enemyTypeService = enemyTypeService;
    }

    public List<EnemyEntity> getEnemies() {
        return enemyRepository.findAllByAliveTrue();
    }

    public String getEnemiesFormatted() {
        return formatEnemies(enemyRepository.findAllByAliveTrue());
    }

    @Transactional
    public void saveEnemies(EnemySaver enemySaver) {


        String name = enemySaver.getName();
        int initiativeModifier = enemySaver.getInitiativeModifier();
        Optional<EnemyTypeEntity> byNameAndInitiativeModifier = enemyTypeService.findByNameAndInitiativeModifier(name, initiativeModifier);

        EnemyTypeEntity enemyType;
        if (byNameAndInitiativeModifier.isPresent()) {
            enemyType = byNameAndInitiativeModifier.get();
        } else {
            enemyType = new EnemyTypeEntity();
            enemyType.setName(name);
            enemyType.setInitiativeModifier(initiativeModifier);
        }

        List<EnemyEntity> enemies = new ArrayList<>();
        int quantity = enemySaver.getQuantity();
        for (int i = 0; i < quantity; i++) {
            EnemyEntity enemy = new EnemyEntity();
            enemy.setEnemyType(enemyType);
            enemies.add(enemy);
        }

        enemyRepository.saveAll(enemies);

    }

    public String formatEnemies(List<EnemyEntity> enemies) {
        String format = getStringFormatter();

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

    private String getStringFormatter() {
        return "%-" + getSizeOfLongestName() + "s%-" + getSizeOfLongestTag() + "s%-23s%-10s\n";
    }

    private int getSizeOfLongestName() {
        return enemyRepository.findAllByAliveTrue()
                .stream()
                .map(enemyEntity -> enemyEntity.getName().length())
                .max(Comparator.naturalOrder())
                .orElse(10) + 5;
    }

    private int getSizeOfLongestTag() {
        return enemyRepository.findAllByAliveTrue()
                .stream()
                .map(enemy -> enemy.getTag().orElse("").length())
                .max(Comparator.naturalOrder())
                .orElse(10) + 10;
    }


}

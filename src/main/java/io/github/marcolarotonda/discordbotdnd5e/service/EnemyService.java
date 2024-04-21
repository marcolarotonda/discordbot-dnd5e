package io.github.marcolarotonda.discordbotdnd5e.service;

import io.github.marcolarotonda.discordbotdnd5e.model.EnemySaver;
import io.github.marcolarotonda.dnd5e.entity.EnemyEntity;
import io.github.marcolarotonda.dnd5e.entity.EnemyTypeEntity;
import io.github.marcolarotonda.dnd5e.repository.EnemyRepository;
import io.github.marcolarotonda.dnd5e.repository.EnemyTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.marcolarotonda.discordbotdnd5e.utils.StringUtils.BLOCK_DELIMITER;
import static io.github.marcolarotonda.discordbotdnd5e.utils.StringUtils.BOLD_DELIMITER;

@Service
public class EnemyService {
    private final EnemyRepository enemyRepository;
    private final EnemyTypeRepository enemyTypeRepository;

    @Autowired
    public EnemyService(EnemyRepository enemyRepository,
                        EnemyTypeRepository enemyTypeRepository) {
        this.enemyRepository = enemyRepository;
        this.enemyTypeRepository = enemyTypeRepository;
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
        Optional<EnemyTypeEntity> byNameAndInitiativeModifier = enemyTypeRepository.findByNameAndInitiativeModifier(name, initiativeModifier);

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
                .append(String.format(format, "Name", "Initiative Modifier"))
                .append(BLOCK_DELIMITER)
                .append(BOLD_DELIMITER);

        if (!enemies.isEmpty()) {
            String collect = enemies.stream()
                    .map(enemy -> String.format(format,
                            enemy.getName(),
                            enemy.getInitiativeModifier()))
                    .collect(Collectors.joining("\n"));
            message.append(BLOCK_DELIMITER)
                    .append(collect)
                    .append(BLOCK_DELIMITER);
        }

        return message.toString();

    }

    private String getStringFormatter() {
        return "%-" + getSizeOfLongestName() + "s%-10s\n";
    }

    private int getSizeOfLongestName() {
        return enemyRepository.findAllByAliveTrue()
                .stream()
                .map(enemyEntity -> enemyEntity.getName().length())
                .max(Comparator.naturalOrder())
                .orElse(10) + 5;
    }


}

package io.github.marcolarotonda.discordbotdnd5e.service;

import io.github.marcolarotonda.dnd5e.entity.InitiativeItemEntity;
import io.github.marcolarotonda.dnd5e.repository.InitiativeItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InitiativeItemService {

    private final InitiativeItemRepository repository;

    @Autowired
    public InitiativeItemService(InitiativeItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void saveInitiative(List<InitiativeItemEntity> items) {
        repository.deleteAll();
        repository.saveAll(items);
    }

    public List<InitiativeItemEntity> findAll() {
        return repository.findAll();
    }
}

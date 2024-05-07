package io.github.marcolarotonda.discordbotdnd5e.service;

import io.github.marcolarotonda.dnd5e.entity.InitiativeItemEntity;
import io.github.marcolarotonda.dnd5e.service.ComputeInitiativeService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.marcolarotonda.discordbotdnd5e.utils.StringUtils.BLOCK_DELIMITER;
import static io.github.marcolarotonda.discordbotdnd5e.utils.StringUtils.BOLD_DELIMITER;

@Service
public class InitiativeService {

    private final ComputeInitiativeService computeInitiativeService;

    @Getter
    private List<InitiativeItemEntity> initiative;

    @Getter
    private final List<InitiativeItemEntity> initiativeBin;

    public static final int INITIATIVE_STARTING_INDEX = 1;

    @Autowired
    public InitiativeService(ComputeInitiativeService computeInitiativeService) {
        this.computeInitiativeService = computeInitiativeService;
        initiative = new ArrayList<>();
        initiativeBin = new ArrayList<>();
    }

    public void removeFromInitiative(int index) {
        InitiativeItemEntity toRemove = initiative.remove(index - INITIATIVE_STARTING_INDEX);
        initiativeBin.add(toRemove);
    }

    public void restoreFromBin(int binIndex, int desiredIndex) {
        InitiativeItemEntity fromBin = initiativeBin.remove(binIndex - INITIATIVE_STARTING_INDEX);
        initiative.add(desiredIndex - INITIATIVE_STARTING_INDEX, fromBin);
    }

    public void addDamageToInitiativeItem(int index, int damageToAdd) {
        InitiativeItemEntity initiativeItem = initiative.get(index - INITIATIVE_STARTING_INDEX);
        int currentDamage = initiativeItem.getDamageTaken();
        initiativeItem.setDamageTaken(currentDamage + damageToAdd);
    }

    /**
     * Calls the pipeline of the dependency ComputeInitiativeService to calculate initiative
     */
    public void calculateInitiative() {
        if (initiative.isEmpty()) {
            computeInitiativeService.execute();
        }
    }

    /**
     * Creates end the returns a String representing the initiative order in a tabular format
     *
     * @param initiative List&lt;InitiativeItem&gt;
     * @return: String
     */
    public String getInitiativeFormatted(List<InitiativeItemEntity> initiative) {
        String format = getStringFormatter();

        StringBuilder message = new StringBuilder();
        message.append(BOLD_DELIMITER)
                .append(BLOCK_DELIMITER)
                .append(String.format(format, "Order", "Name", "Damage", "Value"))
                .append(BLOCK_DELIMITER)
                .append(BOLD_DELIMITER);

        if (!initiative.isEmpty()) {
            String collect = IntStream.range(0, initiative.size())
                    .mapToObj(i -> {
                        InitiativeItemEntity initiativeItem = initiative.get(i);
                        return String.format(format,
                                INITIATIVE_STARTING_INDEX + i,
                                initiativeItem.getName(),
                                initiativeItem.getDamageTaken(),
                                initiativeItem.getInitiativeValue());
                    })
                    .collect(Collectors.joining("\n"));
            message.append(BLOCK_DELIMITER)
                    .append(collect)
                    .append(BLOCK_DELIMITER);
        }

        return message.toString();
    }

    /**
     * Reset the initiative
     */
    public void clear() {
        initiative.clear();
        initiativeBin.clear();
    }

    public void changeInitiative(int actualPosition, int desiredPosition) {
        InitiativeItemEntity removed = initiative.remove(actualPosition - INITIATIVE_STARTING_INDEX);
        initiative.add(desiredPosition - INITIATIVE_STARTING_INDEX, removed);

    }

    public List<InitiativeItemEntity> getCalculatedInitiative() {
        initiative = computeInitiativeService.getInitiative();
        return initiative;
    }

    private int getSizeOfLongestName() {
        return initiative.stream()
                .map(initiativeItem -> initiativeItem.getName().length())
                .max(Comparator.naturalOrder())
                .orElse(10) + 5;
    }

    private String getStringFormatter() {
        return "%-10s%-" + getSizeOfLongestName() + "s%-10s%-10s\n";
    }

    public int getInitiativeMaxIndex() {
        return getMaxIndex(initiative);
    }

    public int getBinMaxIndex() {
        return getMaxIndex(initiativeBin);
    }

    private int getMaxIndex(List<InitiativeItemEntity> list) {
        // L'ordine di iniziativa parte da 1!
        return list.size();
    }


}

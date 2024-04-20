package io.github.marcolarotonda.discordbotdnd5e.service;

import io.github.marcolarotonda.dnd5e.model.InitiativeItem;
import io.github.marcolarotonda.dnd5e.service.ComputeInitiativeService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class InitiativeService {

    private final ComputeInitiativeService computeInitiativeService;

    @Getter
    private List<InitiativeItem> initiative;

    @Getter
    private final List<InitiativeItem> initiativeBin;

    public static final int INITIATIVE_STARTING_INDEX = 1;

    @Autowired
    public InitiativeService(ComputeInitiativeService computeInitiativeService) {
        this.computeInitiativeService = computeInitiativeService;
        initiative = new ArrayList<>();
        initiativeBin = new ArrayList<>();
    }

    public void removeFromInitiative(int index) {
        InitiativeItem toRemove = initiative.remove(index - INITIATIVE_STARTING_INDEX);
        initiativeBin.add(toRemove);
    }

    public void restoreFromBin(int binIndex, int desiredIndex) {
        InitiativeItem fromBin = initiativeBin.remove(binIndex - INITIATIVE_STARTING_INDEX);
        initiative.add(desiredIndex - INITIATIVE_STARTING_INDEX, fromBin);
    }

    public void addDamageToInitiativeItem(int index, int damageToAdd) {
        InitiativeItem initiativeItem = initiative.get(index - INITIATIVE_STARTING_INDEX);
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
    public String getInitiativeFormatted(List<InitiativeItem> initiative) {
        String blockDelimiter = "```";
        String boldDelimiter = "**";
        String format = getStringFormatter();

        StringBuilder message = new StringBuilder();
        message.append(boldDelimiter)
                .append(blockDelimiter)
                .append(String.format(format, "Order", "Name", "Damage", "Value"))
                .append(blockDelimiter)
                .append(boldDelimiter);

        if (!initiative.isEmpty()) {
            String collect = IntStream.range(0, initiative.size())
                    .mapToObj(i -> {
                        InitiativeItem initiativeItem = initiative.get(i);
                        return String.format(format,
                                INITIATIVE_STARTING_INDEX + i,
                                initiativeItem.getName(),
                                initiativeItem.getDamageTaken(),
                                initiativeItem.getInitiativeValue());
                    })
                    .collect(Collectors.joining("\n"));
            message.append(blockDelimiter)
                    .append(collect)
                    .append(blockDelimiter);
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
        InitiativeItem removed = initiative.remove(actualPosition - INITIATIVE_STARTING_INDEX);
        initiative.add(desiredPosition - INITIATIVE_STARTING_INDEX, removed);

    }

    public List<InitiativeItem> getCalculatedInitiative() {
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

    private int getMaxIndex(List<InitiativeItem> list) {
        // L'ordine di iniziativa parte da 1!
        return list.size();
    }


}

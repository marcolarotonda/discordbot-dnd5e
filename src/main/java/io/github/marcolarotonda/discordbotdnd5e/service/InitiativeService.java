package io.github.marcolarotonda.discordbotdnd5e.service;

import io.github.marcolarotonda.discordbotdnd5e.model.InitiativeItem;
import io.github.marcolarotonda.dnd5e.entity.Combatant;
import io.github.marcolarotonda.dnd5e.service.ComputeInitiativeService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class InitiativeService {

    private final ComputeInitiativeService computeInitiativeService;

    @Getter
    private List<InitiativeItem> initiative;

    @Getter
    private List<InitiativeItem> removedFromInitiative;

    public static final int INITIATIVE_STARTING_INDEX = 1;

    @Autowired
    public InitiativeService(ComputeInitiativeService computeInitiativeService) {
        this.computeInitiativeService = computeInitiativeService;
        initiative = new ArrayList<>();
        removedFromInitiative = new ArrayList<>();
    }

    public void removeFromInitiative(int index) {
        InitiativeItem toRemove = initiative.remove(index - INITIATIVE_STARTING_INDEX);
        removedFromInitiative.add(toRemove);
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

    public String getInitiativeFormatted(List<InitiativeItem> initiative) {
        return formatInitiative(initiative);
    }

    /**
     * Reset the initiative
     */
    public void clearInitiative() {
        initiative.clear();
    }

    public void changeInitiative(int actualPosition, int desiredPosition) {
        InitiativeItem removed = initiative.remove(actualPosition - INITIATIVE_STARTING_INDEX);
        initiative.add(desiredPosition - INITIATIVE_STARTING_INDEX, removed);

    }

    /**
     * Given a list List &lt Map.Entry &lt Combatant, Integer &gt &gt, each element of this list is transformed into an InitiativeItem
     * object.
     *
     * @return: Initiative order represented as List &lt InitiativeItem &gt
     */
    public List<InitiativeItem> getCalculatedInitiative() {

        Function<Integer, InitiativeItem> getInitiativeItem = i -> {
            Map.Entry<Combatant, Integer> entry = getInitiativeAsMapEntries().get(i);
            return InitiativeItem.builder()
                    .name(entry.getKey().getName())
                    .description("")
                    .damageTaken(0)
                    .initiativeValue(entry.getValue())
                    .build();
        };

        initiative = IntStream.range(0, getInitiativeAsMapEntries().size())
                .boxed()
                .map(getInitiativeItem)
                .collect(Collectors.toList());

        return initiative;
    }


    /**
     * Each element of the initiative is represented by an Entry (Combatant, Integer), where the Combatant is the subject of
     * the initiative, and the Integer is its position in the initiative order.
     *
     * @return initiative order as List &lt Map.Entry &lt Combatant, Integer &gt &gt. Elements of the returned list are ordered
     * by their initiative position ascending.
     */
    private List<Map.Entry<Combatant, Integer>> getInitiativeAsMapEntries() {
        return computeInitiativeService.getInitiativeOrder();
    }

    /**
     * Creates end the returns a String representing the initiative order in a tabular format
     *
     * @param initiative List &lt InitiativeItem &gt
     * @return: String
     */
    public String formatInitiative(List<InitiativeItem> initiative) {
        String blockDelimiter = "```";
        String boldDelimiter = "**";
        String format = getStringFormatter();

        StringBuilder message = new StringBuilder();
        message.append(boldDelimiter)
                .append(blockDelimiter)
                .append(String.format(format, "Order", "Name", "Damage", "Value"))
                .append(blockDelimiter)
                .append(boldDelimiter);

        if (!initiative.isEmpty()){
            String collect = IntStream.range(0, initiative.size())
                    .mapToObj(i -> {
                        InitiativeItem initiativeItem = initiative.get(i);
                        return String.format(format,
                                INITIATIVE_STARTING_INDEX + i, initiativeItem.getName(), initiativeItem.getDamageTaken(), initiativeItem.getInitiativeValue());
                    })
                    .collect(Collectors.joining("\n"));
            message.append(blockDelimiter)
                    .append(collect)
                    .append(blockDelimiter);
        }

        return message.toString();
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

    public int getMaxIndex() {
        // L'ordine di iniziativa parte da 1!
        return getInitiative().size();
    }


}

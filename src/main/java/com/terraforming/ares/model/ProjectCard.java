package com.terraforming.ares.model;

import com.terraforming.ares.model.parameters.ParameterColor;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.LongPredicate;

/**
 * Created by oleksii.nikitin
 * Creation date 29.04.2022
 */
public interface ProjectCard extends GenericCard {
    CardColor getColor();

    default List<Tag> getTagRequirements() {
        return Collections.emptyList();
    }

    default List<ParameterColor> getTemperatureRequirement() {
        return Collections.emptyList();
    }

    default List<ParameterColor> getOxygenRequirement() {
        return Collections.emptyList();
    }

    default LongPredicate getOceanRequirement() {
        return currentNumberOfOceans -> (currentNumberOfOceans >= 0 && currentNumberOfOceans <= 9);
    }

    boolean isActiveCard();

    default int getWinningPoints() {
        return 0;
    }

    int getPrice();

    default Set<SpecialEffect> getSpecialEffects() {
        return Collections.emptySet();
    }
}
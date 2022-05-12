package com.terraforming.ares.dto;

import com.terraforming.ares.model.*;
import com.terraforming.ares.model.income.Gain;
import com.terraforming.ares.model.parameters.OceanRequirement;
import com.terraforming.ares.model.parameters.ParameterColor;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleksii.nikitin
 * Creation date 05.05.2022
 */
@Value
public class ProjectCardDto {
    int id;
    String name;
    int price;
    int winPoints;
    List<Tag> tags;
    String description;
    List<Gain> incomes;
    CardColor cardColor;
    List<SpecialEffect> specialEffects;
    CardAction cardAction;
    List<Tag> tagReq;
    List<ParameterColor> tempReq;
    List<ParameterColor> oxygenReq;
    OceanRequirement oceanRequirement;
    List<Gain> bonuses;

    public static ProjectCardDto from(ProjectCard card) {
        return new ProjectCardDto(
                card.getId(),
                card.getCardMetadata().getName(),
                card.getPrice(),
                card.getWinningPoints(),
                card.getTags(),
                card.getCardMetadata().getDescription(),
                card.getCardMetadata().getIncomes(),
                card.getColor(),
                card.getSpecialEffects().isEmpty() ? List.of() : new ArrayList<>(card.getSpecialEffects()),
                card.getCardMetadata().getCardAction(),
                card.getTagRequirements(),
                card.getTemperatureRequirement(),
                card.getOxygenRequirement(),
                card.getOceanRequirement(),
                card.getCardMetadata().getBonuses()
        );
    }
}
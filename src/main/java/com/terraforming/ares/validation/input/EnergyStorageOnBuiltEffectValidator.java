package com.terraforming.ares.validation.input;

import com.terraforming.ares.cards.green.EnergyStorage;
import com.terraforming.ares.model.Player;
import com.terraforming.ares.model.ProjectCard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by oleksii.nikitin
 * Creation date 07.05.2022
 */
@Component
@RequiredArgsConstructor
public class EnergyStorageOnBuiltEffectValidator implements OnBuiltEffectValidator<EnergyStorage> {

    @Override
    public Class<EnergyStorage> getType() {
        return EnergyStorage.class;
    }

    @Override
    public String validate(ProjectCard card, Player player, Map<Integer, List<Integer>> input) {
        if (player.getTerraformingRating() < 7) {
            return "You need to have at least 7 TR";
        }

        return null;
    }
}

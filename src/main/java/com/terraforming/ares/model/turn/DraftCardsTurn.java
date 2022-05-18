package com.terraforming.ares.model.turn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by oleksii.nikitin
 * Creation date 05.05.2022
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DraftCardsTurn implements Turn {
    String playerUuid;

    @Override
    public TurnType getType() {
        return TurnType.DRAFT_CARDS;
    }

}

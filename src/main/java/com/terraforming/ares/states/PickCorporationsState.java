package com.terraforming.ares.states;


import com.terraforming.ares.mars.MarsGame;
import com.terraforming.ares.model.Player;
import com.terraforming.ares.model.StateContext;
import com.terraforming.ares.model.StateType;
import com.terraforming.ares.model.turn.TurnType;

import java.util.Collections;
import java.util.List;

/**
 * Created by oleksii.nikitin
 * Creation date 25.04.2022
 */
public class PickCorporationsState extends AbstractState {

    public PickCorporationsState(MarsGame marsGame) {
        super(marsGame);
    }

    @Override
    public List<TurnType> getPossibleTurns(StateContext stateContext) {
        Player player = marsGame.getPlayerByUuid(stateContext.getPlayerUuid());
        if (player.getNextTurn() != null) {
            if (player.getNextTurn().getType() == TurnType.DISCARD_CARDS) {
                return List.of(TurnType.DISCARD_DRAFTED_CARDS);
            } else {
                return List.of();
            }
        } else {
            return Collections.singletonList(TurnType.PICK_CORPORATION);
        }
    }

    @Override
    public void updateState() {
        if (marsGame.getPlayerUuidToPlayer().values().stream().allMatch(
                player -> player.getNextTurn() == null
        )) {
            marsGame.setStateType(StateType.PICK_PHASE);
        }
    }

}

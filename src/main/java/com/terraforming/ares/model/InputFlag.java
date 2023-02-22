package com.terraforming.ares.model;

import lombok.Getter;

/**
 * Created by oleksii.nikitin
 * Creation date 07.05.2022
 */
@Getter
public enum InputFlag {
    SKIP_ACTION(-1),
    DECOMPOSERS_TAKE_MICROBE(1),
    DECOMPOSERS_TAKE_CARD(2),
    EXTEME_COLD_FUNGUS_PICK_PLANT(3),
    EXTREME_COLD_FUNGUS_PUT_MICROBE(4),
    MARS_UNIVERSITY_CARD(5),
    VIRAL_ENHANCERS_TAKE_PLANT(6),
    VIRAL_ENHANCERS_PUT_RESOURCE(7),
    CEOS_FAVORITE_PUT_RESOURCES(8),
    IMPORTED_HYDROGEN_PICK_PLANT(9),
    IMPORTED_HYDROGEN_PUT_RESOURCE(10),
    IMPORTED_NITROGEN_ADD_ANIMALS(11),
    IMPORTED_NITROGEN_ADD_MICROBES(12),
    LARGE_CONVOY_PICK_PLANT(13),
    LARGE_CONVOY_ADD_ANIMAL(14),
    LOCAL_HEAT_TRAPPING_PUT_RESOURCE(15),
    ASTROFARM_PUT_RESOURCE(16),
    EOS_CHASMA_PUT_RESOURCE(17),
    SYNTHETIC_CATASTROPHE_CARD(18),
    PHASE_UPGRADE_CARD(19),
    TAG_INPUT(20),
    BIOMEDICAL_IMPORTS_RAISE_OXYGEN(21),
    BIOMEDICAL_IMPORTS_UPGRADE_PHASE(22),
    CRYOGENIC_SHIPMENT_PUT_RESOURCE(23),
    DISCARD_HEAT(24),
    CARD_CHOICE(25),
    ADD_DISCARD_MICROBE(26),
    AUSTELLAR_CORPORATION_MILESTONE(27)
    ;

    private final int id;

    InputFlag(int id) {
        this.id = id;
    }
}

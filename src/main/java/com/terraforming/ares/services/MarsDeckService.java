package com.terraforming.ares.services;

import com.terraforming.ares.cards.corporations.HelionCorporation;
import com.terraforming.ares.model.CorporationCard;
import com.terraforming.ares.model.Deck;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by oleksii.nikitin
 * Creation date 25.04.2022
 */
@Service
public class MarsDeckService {
    private final Map<Integer, CorporationCard> inmemoryCorporationsStorage = Map.of(
            1, new HelionCorporation()
    );

    public Deck createProjectsDeck() {
        return Deck.builder()
                .cards(new LinkedList<>(
                        IntStream.range(1, 21).boxed().collect(Collectors.toList())
                ))
                .build();
    }

    public Deck createCorporationsDeck() {
        return Deck.builder()
                .cards(new LinkedList<>(
                        IntStream.range(1, 11).boxed().collect(Collectors.toList())
                ))
                .build();
    }

    public CorporationCard getCard(int id) {
        return inmemoryCorporationsStorage.get(id);
    }

}

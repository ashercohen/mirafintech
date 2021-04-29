package com.mirafintech.prototype.model;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;


interface Parent<C> {

    // TODO: make static with type params specified in the method in order to support multiple associations with a single entity
    default <P extends Parent<C>> boolean addToCollection(Collection<C> collection, C element, P parent, String name, Consumer<P> setter) {

        return Optional.ofNullable(element)
                .map(e -> {
                    setter.accept(parent);
                    return collection.add(e);
                })
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s is null", name)));
    }

    default <P extends Parent<C>> boolean removeFromCollection(Collection<C> collection, C element, String name, Consumer<P> setter) {

        return Optional.ofNullable(element)
                .map(e -> {
                    setter.accept(null);
                    return collection.remove(e);
                })
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s is null", name)));
    }
}

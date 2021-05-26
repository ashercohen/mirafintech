package com.mirafintech.prototype.model;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;


public abstract class EntityBase<T extends EntityBase<?>> {

    protected static <P extends EntityBase<P>, E extends EntityBase<E>>
        boolean addToCollection(Collection<E> collection, E element, P parent, String name, Consumer<P> setter) {

        return Optional.ofNullable(element)
                .map(e -> {
                    setter.accept(parent);
                    return collection.add(e);
                })
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s is null", name)));
    }

    protected static <P extends EntityBase<P>, E extends EntityBase<E>>
        boolean removeFromCollection(Collection<E> collection, E element, String name, Consumer<P> setter) {

        return Optional.ofNullable(element)
                .map(e -> {
                    setter.accept(null);
                    return collection.remove(e);
                })
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s is null", name)));
    }
}

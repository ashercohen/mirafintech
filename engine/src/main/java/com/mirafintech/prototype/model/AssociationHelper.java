package com.mirafintech.prototype.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class AssociationHelper {

    public static <P, E> boolean addToCollection(Collection<E> collection, E element, P parent, String name, Consumer<P> setter) {

        return Optional.ofNullable(element)
                .map(e -> {
                    setter.accept(parent);
                    return collection.add(e);
                })
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s is null", name)));
    }

    public static <P, E>
        boolean removeFromCollection(Collection<E> collection, E element, String name, Consumer<P> setter) {

        return Optional.ofNullable(element)
                .map(e -> {
                    setter.accept(null);
                    return collection.remove(e);
                })
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s is null", name)));
    }

    public static <E> List<E> createIfNull(List<E> list) {
        return list == null ? new ArrayList<E>() : list;
    }
}

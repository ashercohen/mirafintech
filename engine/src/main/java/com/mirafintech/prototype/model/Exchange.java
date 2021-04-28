package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "EXCHANGES")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Exchange implements Parent<Tranche> {

    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "exchange", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Tranche> tranches = new ArrayList<>();

    public boolean addTranche(Tranche tranche) {

        return addToCollection(this.tranches, tranche, this, "tranche", tranche::setExchange);

//        return Optional.ofNullable(tranche)
//                .map(t -> {
//                    t.setExchange(this);
//                    return tranches.add(t);
//                })
//                .orElseThrow(() -> new IllegalArgumentException("tranche is null"));
    }

    public boolean removeTrance(Tranche tranche) {
        return removeFromCollection(this.tranches, tranche, "tranche", tranche::setExchange);

//        return Optional.ofNullable(tranche)
//                .map(t -> {
//                    t.setExchange(null);
//                    return tranches.remove(tranche);
//                })
//                .orElseThrow(() -> new IllegalArgumentException("tranche is null"));
    }
}

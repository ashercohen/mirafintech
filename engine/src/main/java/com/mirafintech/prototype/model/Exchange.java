package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.tranche.Tranche;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "EXCHANGE")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Exchange {

    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "exchange", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tranche> tranches = new ArrayList<>();
    // TODO: addTranche() + removeTranche()

    private Exchange(Long id, String name, List<Tranche> tranches) {
        this.id = id;
        this.name = name;
        this.tranches = tranches;
    }
}

package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "DATED_TRANCHE")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatedTranche {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime timestamp;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false, orphanRemoval = false)
    @JoinColumn(name = "tranche_fk")
    private Tranche tranche;

    private DatedTranche(Long id, LocalDateTime timestamp, Tranche tranche) {
        this.id = id;
        this.timestamp = timestamp;
        this.tranche = tranche;
    }

    public DatedTranche(LocalDateTime timestamp, Tranche tranche) {
        this(null, timestamp, tranche);
    }
}

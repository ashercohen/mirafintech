package com.mirafintech.prototype.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SYSTEM_TIME")
@Getter
@Setter
//@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime dateTime;

    private SystemTime(Long id, LocalDateTime dateTime) {
        this.id = id;
        this.dateTime = dateTime;
    }

    public SystemTime(LocalDateTime dateTime) {
        this(null, dateTime);
    }
}

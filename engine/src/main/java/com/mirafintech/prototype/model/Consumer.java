package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.dto.ConsumerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Entity
@Table(name = "CONSUMER")
@Getter
@Setter
//@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Consumer extends EntityBase<Consumer> {

    @Id
    private Long id;

    private Integer limitBalance; // Amount of given credit in NT dollars (includes individual and family/supplementary credit

    private Integer education; // (1=graduate school, 2=university, 3=high school, 4=others, 5=unknown, 6=unknown)

    private Integer sex; // (1=male, 2=female)

    private Integer martialStatus; // (1=married, 2=single, 3=others)

    private Integer age; // int years

    private LocalDateTime addedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "timedcreditscore_fk")
    private List<TimedCreditScore> timedCreditScores = new ArrayList<>();

    @OneToMany(mappedBy = "consumer", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();

    @OneToMany(mappedBy = "consumer", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    private Consumer(Long id,
                     Integer limitBalance,
                     Integer education,
                     Integer sex,
                     Integer martialStatus,
                     Integer age,
                     LocalDateTime addedAt,
                     List<TimedCreditScore> timedCreditScores,
                     List<Loan> loans,
                     List<Payment> payments) {
        this.id = id;
        this.limitBalance = limitBalance;
        this.education = education;
        this.sex = sex;
        this.martialStatus = martialStatus;
        this.age = age;
        this.addedAt = addedAt;
        this.timedCreditScores = timedCreditScores == null ? new ArrayList<>() : timedCreditScores;
        this.loans = loans == null ? new ArrayList<>() : loans;
        this.payments = payments == null ? new ArrayList<>() : payments;
    }

    public Consumer(ConsumerDto dto, TimedCreditScore creditScore, LocalDateTime timestamp) {
        this(dto.getId(),
             dto.getLimitBalance(),
             dto.getEducation(),
             dto.getSex(),
             dto.getMartialStatus(),
             dto.getAge(),
             timestamp,
             new ArrayList<>(List.of(creditScore)),
             null,
             null);
    }

    public TimedCreditScore currentCreditScore() {
        return this.timedCreditScores
                .stream()
                .max(Comparator.comparing(TimedCreditScore::getTimestamp))
                .orElseThrow(() -> new RuntimeException("consumer does not have credit score: id=" + this.id));
    }

    public Optional<TimedCreditScore> creditScoreAt(LocalDateTime localDateTime) {
        return this.timedCreditScores
                .stream()
                .filter(score -> score.getTimestamp().equals(localDateTime))
                .findAny(); // TODO: assuming user has max one score at specified time
    }

    public boolean addLoan(Loan loan) {
        return addToCollection(this.loans, loan, this, "loan", loan::setConsumer);
    }

    public boolean removeLoan(Loan loan) {
        return removeFromCollection(this.loans, loan, "loan", loan::setConsumer);
    }
}

package com.mirafintech.prototype.tests.association;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.EntityBase;
import com.mirafintech.prototype.tests.util.AbstractTest;
import lombok.*;
import org.junit.Test;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Unidirectional_OneToMany extends AbstractTest {

    private long accountId = 12345L;

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[] {
                Account.class,
                Balance.class
        };
    }

    @Override
    protected void afterInit() {
        // insert here item to db
    }

    @Override
    protected void doDestroy() {
        // keep db tables + rows create in this test
    }

    @Test
    public void testBalanceUpdate() {

        doInJPA(em -> {
            em.persist(new Account(accountId));
        });

        doInJPA(em -> {
            Account acc = em.find(Account.class, this.accountId);
            acc.updateBalance(new Balance(LocalDateTime.parse("2021-05-18T12:00:00"), 1000));
            acc.updateBalance(new Balance(LocalDateTime.parse("2021-05-19T12:00:00"), 2000));
            acc.updateBalance(new Balance(LocalDateTime.parse("2021-05-20T12:00:00"), 3000));
//            em.persist(acc); // no required, objects are attached to transaction
        });

        doInJPA(em -> {
            Account account = em.find(Account.class, this.accountId);
            System.out.println("account=" + account);
        });
    }

    @Test
    public void dropTables() {
        super.doDestroy();
    }

    @Entity
    @Table(name = "ACCOUNT")
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Account extends EntityBase<Account> {

        @Id
        private Long id;

        @Setter(value = AccessLevel.PRIVATE)
        @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
        @JoinColumn(name = "balances_id")
        private List<Balance> balanceHistory;

        Account(Long id, List<Balance> balanceHistory) {
            this.id = id;
            this.balanceHistory = balanceHistory == null ? new ArrayList<>() : balanceHistory;
        }

        public Account(Long number) {
            this(number, null);
        }

        public boolean updateBalance(Balance balance) {
            this.balanceHistory.add(balance);

            return false;
        }
    }

    @Entity
    @Table(name = "BALANCE")
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Balance extends EntityBase<Balance> {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private LocalDateTime dateTime;

        private Integer value;

        private Balance(Long id, LocalDateTime dateTime, Integer value) {
            this.id = id;
            this.dateTime = dateTime;
            this.value = value;
        }

        public Balance(LocalDateTime dateTime, Integer value) {
            this.dateTime = dateTime;
            this.value = value;
        }
    }
}
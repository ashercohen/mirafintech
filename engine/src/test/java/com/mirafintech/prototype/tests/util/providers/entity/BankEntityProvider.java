package com.mirafintech.prototype.tests.util.providers.entity;

import com.mirafintech.prototype.tests.util.EntityProvider;

import javax.persistence.Entity;
import javax.persistence.Id;


public class BankEntityProvider implements EntityProvider {
    @Override
    public Class<?>[] entities() {
        return new Class<?>[]{
            Account.class
        };
    }

    @Entity(name = "account")
    public static class Account {

        @Id
        private Long id;

        private Long balance;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getBalance() {
            return balance;
        }

        public void setBalance(Long balance) {
            this.balance = balance;
        }
    }
}

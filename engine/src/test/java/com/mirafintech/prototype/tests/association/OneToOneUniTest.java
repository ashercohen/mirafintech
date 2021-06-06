package com.mirafintech.prototype.tests.association;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.EntityBase;
import com.mirafintech.prototype.tests.util.AbstractTest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.Test;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class OneToOneUniTest extends AbstractTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
                DatedTranche.class,
                Tranche.class,
        };
    }

    @Override
    public void destroy() {
        // comment this line in order not to drop the tables
        super.destroy();
    }

    @Override
    protected void afterInit() {
        doInJPA(entityManager -> {
            LocalDateTime timestamp = LocalDateTime.now();
            Tranche tranche = Tranche.createEmptyTranche(new BigDecimal(1000000), timestamp);
            entityManager.persist(tranche);
            DatedTranche datedTranche = new DatedTranche(timestamp, tranche);
            entityManager.persist(datedTranche);
        });
    }

    @Test
    public void testLifecycle() {
        final Tranche[] pt = new Tranche[1];
        doInJPA(entityManager -> {
            final Tranche persistedTranche =
                    entityManager.createQuery("select t from Tranche t where t.id = :id ", Tranche.class)
                            .setParameter("id", 1L)
                            .getSingleResult();
            assertNotNull(persistedTranche);
            pt[0] = persistedTranche;
        });

        doInJPA(entityManager -> {
            List<DatedTranche> datedTranches =
                    entityManager.createQuery("select dt from DatedTranche dt", DatedTranche.class)
                            .getResultList();
            assertEquals(1, datedTranches.size());
            Long id = datedTranches.get(0).getTranche().getId();
            assertEquals(pt[0].getId(), id);
        });
    }

    @Test
    public void testRemove() {
        doInJPA(entityManager -> {

            // TODO: implement remove test: see BidirectionalOneToManyTest for example
            DatedTranche datedTranche = entityManager.createQuery("select dt from DatedTranche dt", DatedTranche.class).getSingleResult();
            Tranche tranche = entityManager.find(Tranche.class, 1L);
        });
    }

    @Test
    public void testLazyFetch() {
        QueryCountHolder.clear();

        doInJPA(entityManager -> {
            DatedTranche datedTranche = entityManager.createQuery("select dt from DatedTranche dt", DatedTranche.class).getSingleResult();
            /**
             * lazy association => tranche table is not queried
             */
            assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());

            /**
             * tranche entity (= association target) is not accessed - table not queried
             */
            Tranche tranche = datedTranche.getTranche();
            assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());

            /**
             * once the association target is accessed - tranche table is queried
             */
            Tranche.Status trancheStatus = tranche.getStatus();
            assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
        });
    }

    @Test
    public void testOrphanRemoval() {
    }

    @Entity(name = "DatedTranche")
    @Table(name = "datedtranche")
    @Getter
    @Setter
    //@ToString
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DatedTranche {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private LocalDateTime timestamp;

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

    @Entity(name = "Tranche")
    @Table(name = "tranche")
    @Getter
    @Setter
    //@ToString
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Tranche extends EntityBase<Tranche> {

        enum Status {
            ACTIVE, NOT_ACTIVE
        }

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private BigDecimal initialValue;

        private LocalDateTime creationDate;

        private BigDecimal currentBalance;

        @Enumerated(EnumType.STRING)
        private Status status;

        private Tranche(Long id,
                        BigDecimal initialValue,
                        LocalDateTime creationDate,
                        BigDecimal currentBalance,
                        Status status) {
            this.id = id;
            this.initialValue = initialValue;
            this.creationDate = creationDate;
            this.currentBalance = currentBalance;
            this.status = status;
        }

        /**
         * factory method
         */
        public static Tranche createEmptyTranche(BigDecimal initialValue, LocalDateTime timestamp) {

            return new Tranche(
                    null,
                    initialValue,
                    timestamp,
                    BigDecimal.ZERO,
                    Tranche.Status.ACTIVE);
        }

        public BigDecimal currentBalance() {
            return this.currentBalance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tranche tranche = (Tranche) o;
            return id.equals(tranche.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}

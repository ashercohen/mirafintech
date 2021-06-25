package com.mirafintech.prototype.tests.association;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.tests.util.AbstractTest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.Test;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OneToManyUniTest extends AbstractTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
                Consumer.class,
                DatedCreditScore.class
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
            Consumer consumer = new Consumer(1L, 1000);
            LocalDateTime now = LocalDateTime.now();
            consumer.addTimedCreditScore(new DatedCreditScore(50, now));
            consumer.addTimedCreditScore(new DatedCreditScore(75, now.plusDays(1)));
            consumer.addTimedCreditScore(new DatedCreditScore(100, now.plusDays(2)));
            entityManager.persist(consumer);
        });
    }

    @Test
    public void testLifecycle() {

        doInJPA(entityManager -> {
            Consumer consumer = entityManager.createQuery("select c from Consumer c where c.id = :id", Consumer.class)
                    .setParameter("id", 1L)
                    .getSingleResult();
            assertNotNull(consumer);
            assertEquals(3, consumer.getDatedCreditScores().size());
        });
    }

    @Test
    public void testAdd() {

        doInJPA(entityManager -> {
            Consumer consumer = entityManager.find(Consumer.class, 1L);
            consumer.addTimedCreditScore(new DatedCreditScore(99, LocalDateTime.now().plusDays(3)));
            /**
             * not required to persist the consumer after adding a new TCS
             */
//            entityManager.persist(consumer);
            Consumer consumer1 = entityManager.find(Consumer.class, 1L);
            assertEquals(4, consumer1.getDatedCreditScores().size());
        });
    }

    @Test
    public void testLazyFetch() {
        QueryCountHolder.clear();

        doInJPA(entityManager -> {

            Consumer consumer = entityManager.createQuery("select c from Consumer c where c.id = :id", Consumer.class)
                    .setParameter("id", 1L)
                    .getSingleResult();

            /**
             * lazy association => TimedCreditScore table is not queried
             */
            assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());

            /**
             * List<TimedCreditScore> (= association target) is not accessed - table not queried
             */
            List<DatedCreditScore> datedCreditScoreList = consumer.getDatedCreditScores();
            assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());

            /**
             * once the association target is accessed - TCS table is queried
             */
            DatedCreditScore datedCreditScore = datedCreditScoreList.get(0);
            assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
        });
    }

    @Test
    public void testOrphanRemoval() {

        doInJPA(entityManager -> {
            Consumer consumer = entityManager.find(Consumer.class, 1L);
            entityManager.remove(consumer);
            List<DatedCreditScore> datedCreditScoreList =
                    entityManager.createQuery("select t from DatedCreditScore t", DatedCreditScore.class)
                            .getResultList();

            assertEquals(0, datedCreditScoreList.size());
        });
    }

    @Entity(name = "Consumer")
    @Table(name = "CONSUMER")
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Consumer {

        @Id
        private Long id;

        private Integer limitBalance; // Amount of given credit in NT dollars (includes individual and family/supplementary credit

        private LocalDateTime addedAt;

        @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "consumer_fk")
        private List<DatedCreditScore> datedCreditScores = new ArrayList<>();

        private Consumer(Long id, Integer limitBalance, LocalDateTime addedAt, List<DatedCreditScore> datedCreditScores) {
            this.id = id;
            this.limitBalance = limitBalance;
            this.addedAt = addedAt;
            this.datedCreditScores = datedCreditScores == null ? new ArrayList<>() : datedCreditScores;
        }

        public Consumer(Long id, Integer limitBalance) {
            this(id, limitBalance, LocalDateTime.now(), null);
        }

        public boolean addTimedCreditScore(DatedCreditScore datedCreditScore) {
            return Optional.ofNullable(datedCreditScore)
                    .map(score -> this.datedCreditScores.add(score))
                    .orElseThrow(() -> new IllegalArgumentException("timedCreditScore is null"));
        }
    }

    @Entity(name = "DatedCreditScore")
    @Table(name = "DATED_CREDIT_SCORE")
    @Getter
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DatedCreditScore {

        @JsonIgnore
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private int value;
        private LocalDateTime timestamp;

        private DatedCreditScore(Long id, int value, LocalDateTime timestamp) {
            this.id = id;
            this.value = value;
            this.timestamp = timestamp;
        }

        public DatedCreditScore(int value, LocalDateTime timestamp) {
            this(null, value, timestamp);
        }
    }
}

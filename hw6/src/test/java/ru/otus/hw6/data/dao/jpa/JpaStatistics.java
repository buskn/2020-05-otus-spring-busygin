package ru.otus.hw6.data.dao.jpa;

import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaStatistics {
    private boolean enabled;
    private long lastStatementCount;
    private long lastConnectCount;

    @Autowired
    private final TestEntityManager em;

    private Statistics statistics;

    public void startSession() {
        statistics = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        enabled = true;
    }

    public void briefAndEndSession() {
        if (enabled) {
            System.out.println("===================================");
            System.out.println("STATISTICS:");
            System.out.println("prepared count = " +
                    (statistics.getPrepareStatementCount() - lastStatementCount));
            System.out.println("connection count = " +
                    (statistics.getConnectCount() - lastConnectCount));
            System.out.println("===================================");
            lastStatementCount = statistics.getPrepareStatementCount();
            lastConnectCount = statistics.getConnectCount();
            enabled = false;
        }
    }

}

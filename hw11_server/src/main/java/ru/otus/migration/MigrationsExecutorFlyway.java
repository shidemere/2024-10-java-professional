package ru.otus.migration;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

@Slf4j
public class MigrationsExecutorFlyway {

    private final Flyway flyway;

    public MigrationsExecutorFlyway(DataSource dataSource) {
        flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
    }

    public void executeMigrations() {
        log.info("db migration started...");
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}

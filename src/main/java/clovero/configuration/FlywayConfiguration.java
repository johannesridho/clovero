package clovero.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfiguration {

    @Autowired
    private DataSource dataSource;

    @Value("${auto-clean:false}")
    private boolean autoClean;

    @Bean
    public Flyway flyway() {
        final Flyway flyway = new Flyway();

        flyway.setDataSource(dataSource);
        if(autoClean){
            flyway.clean();
        }
        flyway.migrate();
        
        return flyway;
    }
}

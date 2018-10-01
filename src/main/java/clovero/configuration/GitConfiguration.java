package clovero.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:git.properties")
public class GitConfiguration {

    @Autowired
    Environment env;

    @Bean
    public String lastCommitId() {
        return env.getProperty("git.commit.id.abbrev");
    }
}

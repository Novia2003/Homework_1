package ru.tbank.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tbank.model.Location;
import ru.tbank.model.Category;
import ru.tbank.repository.CustomRepository;

@Configuration
public class RepositoryConfiguration {

    @Bean
    public CustomRepository<Category> categoryRepository() {
        return new CustomRepository<>();
    }

    @Bean
    public CustomRepository<Location> locationRepository() {
        return new CustomRepository<>();
    }
}

package org.home.mma.store;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan("org.home.mma.store.dao")
@EntityScan("org.home.mma.store.entities")
@EnableJpaRepositories("org.home.mma.store.repositories")
public class EnableMmaStore {
    
}

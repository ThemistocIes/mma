package org.home.mma.worker.config;

import org.home.mma.store.EnableMmaStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Import({EnableMmaStore.class})
@Configuration
@EnableScheduling
public class ImportConfig {

}

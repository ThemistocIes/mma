package org.home.mma.client.config;

import org.home.mma.store.EnableMmaStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({EnableMmaStore.class})
@Configuration
public class ImportConfig {

}

package com.semanticpie.loafloader.config;

import org.ostis.api.context.DefaultScContext;
import org.ostis.scmemory.model.ScMemory;
import org.ostis.scmemory.websocketmemory.memory.SyncOstisScMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class JManticConfiguration {
    @Value("${prosloechka.sc-machine.url}")
    private String scMachineURL;
    @Bean
    public DefaultScContext contextBean() throws Exception {
        ScMemory memory = new SyncOstisScMemory(new URI(scMachineURL));
        memory.open();
        return new DefaultScContext(memory);
    }
}

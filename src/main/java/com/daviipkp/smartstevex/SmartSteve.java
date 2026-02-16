package com.daviipkp.smartstevex;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.smartstevex.prompt.Prompt;
import com.daviipkp.smartstevex.services.LLMService;
import com.daviipkp.smartstevex.services.SpringContext;
import lombok.Getter;
import org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Field;
import java.util.List;

@SpringBootApplication(exclude = {OpenAiAutoConfiguration.class})
@EnableScheduling
public class SmartSteve {

    @Getter
    private static List<Class<? extends Command>> commandList;

    @Primary
    @Bean
    public EmbeddingModel customEmbeddingModel(
            RestClient.Builder restClientBuilder,
            WebClient.Builder webClientBuilder) {

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_JSON,
                MediaType.valueOf(Configuration.EMBEDDING_EVENT_STREAM_TYPE)
        ));

        restClientBuilder.messageConverters(converters -> converters.add(0, converter));

        OpenAiApi openAiApi = new OpenAiApi(
                Configuration.EMBEDDING_URL,
                Configuration.EMBEDDING_API_KEY,
                restClientBuilder,
                webClientBuilder,
                new DefaultResponseErrorHandler()
        );

        return new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .withModel(Configuration.EMBEDDING_MODEL)
                        .build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE
        );
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(SmartSteve.class)
                .headless(false)
                .run(args);
        java.security.Security.setProperty("networkaddress.cache.ttl", "-1");

        if(Configuration.DO_WARM_UP) {
            LLMService.warmUp();
        }

        boolean crash = false;
        for(Field f : Configuration.class.getDeclaredFields()) {
            if(f == null) {
                crash = true;
                System.out.println(">>> Configuration field " + f.getName() + " has not been set. Please set it before starting the program.");
            }
        }
        if(crash == true) {
            throw new RuntimeException("All variables on Configuration file must be set");
        }


        if(Configuration.CLEAR_MEMO_ON_STARTUP) {
            try {
                JdbcTemplate jdbcTemplate = SpringContext.getBean(JdbcTemplate.class);
                jdbcTemplate.execute("TRUNCATE TABLE vector_store");
                jdbcTemplate.execute("TRUNCATE TABLE chat_message");
                SteveCommandLib.systemPrint("Memory cleared! Change CLEAR_MEMO_ON_STARTUP to false if you don't want this to happen.");
            } catch (Exception e) {
                SteveCommandLib.systemPrint("error clearing memory: " + e.getMessage());
            }
        }

        if(Configuration.USE_DEFAULT_COMMANDS) {
            commandList = Utils.getRegisteredCommands("com.daviipkp.smartstevex.implementations.commands", Configuration.USER_COMMAND_PACKAGE);
        }else{
            commandList = Utils.getRegisteredCommands(Configuration.USER_COMMAND_PACKAGE);
        }

        SteveCommandLib.debug(true);


        for (String arg : args) {
            if (arg.equals("--FirstBoot")) {
                SpringContext.getBean(LLMService.class).finalCallModel(Prompt.getStartupPrompt());
            }
        }

    }

    @Scheduled(fixedDelay = 50)
    public void meuTickComDelay() throws InterruptedException {
        SteveCommandLib.tick(50);
    }

}

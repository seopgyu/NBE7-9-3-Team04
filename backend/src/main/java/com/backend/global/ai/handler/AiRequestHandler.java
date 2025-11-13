package com.backend.global.ai.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AiRequestHandler {

    @Value("${openai.url}")
    private String apiUrl;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    private final RestClient restClient;

    private final OpenAiChatModel openAiChatModel;

    public <T> T execute(Function<ChatClient, T> mapper) {
        ChatClient chatClient = ChatClient.create(openAiChatModel);
        return mapper.apply(chatClient);
    }

    public <T> String connectionAi(T request){
        return restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .body(request)
                .retrieve()
                .body(String.class);
    }
}

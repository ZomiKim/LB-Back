package com.study.lastlayer.externapi.chatbot;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ChatbotService {
	private final RestTemplate restTemplate =  new RestTemplate();
	private final String url = "http://localhost:8080/api/v1/chatbot";
	
	public ChatResponseDto getAnswer(String question) {
		ChatRequestDto request = new ChatRequestDto();
		request.setQuestion(question);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<ChatRequestDto> entity =  new HttpEntity<>(request, headers);
		
		ResponseEntity<ChatResponseDto> response = restTemplate.postForEntity(url, entity, ChatResponseDto.class);
		return response.getBody();
		
	}

}

package br.com.walter.contaclara.ai;

import java.util.UUID;

public record VoiceAssistantResponse(
        String transcript,
        String answer,
        UUID draftId,
        String speechAudioBase64,
        String speechContentType
) {}

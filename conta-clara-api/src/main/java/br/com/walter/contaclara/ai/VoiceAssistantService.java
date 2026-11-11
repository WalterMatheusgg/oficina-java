package br.com.walter.contaclara.ai;

import br.com.walter.contaclara.application.CreateTransactionDraftUseCase;
import br.com.walter.contaclara.application.GetMonthlySummaryUseCase;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

@Service
public class VoiceAssistantService {
    private static final long MAX_AUDIO_BYTES = 15L * 1024 * 1024;
    private static final String[] SUPPORTED_EXTENSIONS = {".mp3", ".m4a", ".wav", ".webm"};

    private final TranscriptionModel transcriptionModel;
    private final TextToSpeechModel textToSpeechModel;
    private final ChatClient.Builder chatClientBuilder;
    private final CreateTransactionDraftUseCase createDraft;
    private final GetMonthlySummaryUseCase getSummary;
    private final String systemPrompt;

    public VoiceAssistantService(TranscriptionModel transcriptionModel,
                                 TextToSpeechModel textToSpeechModel,
                                 ChatClient.Builder chatClientBuilder,
                                 CreateTransactionDraftUseCase createDraft,
                                 GetMonthlySummaryUseCase getSummary,
                                 @Value("classpath:prompts/finance-assistant.st") Resource promptResource) throws IOException {
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;
        this.chatClientBuilder = chatClientBuilder;
        this.createDraft = createDraft;
        this.getSummary = getSummary;
        this.systemPrompt = new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public VoiceAssistantResponse process(MultipartFile audio, boolean includeSpeech) {
        validateAudio(audio);

        try {
            String transcript = transcriptionModel.transcribe(audio.getResource());
            if (transcript == null || transcript.isBlank()) {
                throw new IllegalArgumentException("Não foi possível identificar fala no áudio.");
            }
            if (transcript.length() > 1500) {
                throw new IllegalArgumentException("O comando transcrito é longo demais.");
            }

            FinanceAssistantTools tools = new FinanceAssistantTools(createDraft, getSummary);
            String answer = chatClientBuilder.build()
                    .prompt()
                    .system(systemPrompt)
                    .user(transcript)
                    .tools(tools)
                    .call()
                    .content();

            if (answer == null || answer.isBlank()) {
                throw new IllegalStateException("O assistente não retornou uma resposta.");
            }

            if (tools.createdDraftId() != null) {
                answer = answer + " Para registrar a despesa, confirme o rascunho com o código "
                        + tools.createdDraftId() + ".";
            }

            String audioBase64 = null;
            String audioType = null;
            if (includeSpeech) {
                audioBase64 = Base64.getEncoder().encodeToString(textToSpeechModel.call(answer));
                audioType = "audio/mpeg";
            }

            return new VoiceAssistantResponse(transcript, answer, tools.createdDraftId(), audioBase64, audioType);
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new AssistantProviderException("Não foi possível processar o áudio agora.", exception);
        }
    }

    private void validateAudio(MultipartFile audio) {
        if (audio == null || audio.isEmpty()) {
            throw new IllegalArgumentException("Envie um arquivo de áudio.");
        }
        if (audio.getSize() > MAX_AUDIO_BYTES) {
            throw new IllegalArgumentException("O áudio deve ter no máximo 15 MB.");
        }

        String filename = audio.getOriginalFilename();
        String lowerFilename = filename == null ? "" : filename.toLowerCase(Locale.ROOT);
        boolean supportedExtension = false;
        for (String extension : SUPPORTED_EXTENSIONS) {
            supportedExtension |= lowerFilename.endsWith(extension);
        }
        if (!supportedExtension) {
            throw new IllegalArgumentException("Formato não suportado. Envie MP3, M4A, WAV ou WebM.");
        }
    }

    public static class AssistantProviderException extends RuntimeException {
        public AssistantProviderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

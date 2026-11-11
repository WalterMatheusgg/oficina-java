package br.com.walter.contaclara.api;

import br.com.walter.contaclara.ai.VoiceAssistantResponse;
import br.com.walter.contaclara.ai.VoiceAssistantService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/assistant")
public class VoiceAssistantController {
    private final VoiceAssistantService assistant;

    public VoiceAssistantController(VoiceAssistantService assistant) {
        this.assistant = assistant;
    }

    @PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoiceAssistantResponse processVoice(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "true") boolean includeSpeech) {
        return assistant.process(file, includeSpeech);
    }
}

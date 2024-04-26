package net.taxiMap.taxiplatform.kafka;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class SseEmitters {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(SseEmitters.class);

    SseEmitter add(SseEmitter emitter) {

        this.emitters.add(emitter);

        LOGGER.info(String.format("[[ New Emitter  ]] %s", emitter));
        LOGGER.info(String.format("[[ Size Emitter ]] %s", emitters.size()));

        emitter.onCompletion(() -> {
            LOGGER.info("[[ onCompletion of emitter ]] ");
            this.emitters.remove(emitter);    // 만료되면 리스트에서 삭제
        });

        emitter.onTimeout(() -> {
            LOGGER.info("[[ onTimeout of emitter    ]] ");
            emitter.complete();
        });

        return emitter;
    }
}
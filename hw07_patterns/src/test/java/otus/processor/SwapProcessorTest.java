package otus.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.otus.model.Message;
import ru.otus.processor.SwapProcessor;

class SwapProcessorTest {

    @Test
    void swapProcessorTest() {
        // given
        var swapProcessor = new SwapProcessor();

        var id = 100L;

        var message =
                new Message.Builder(id).field11("field11").field12("field12").build();

        // when
        var newMessage = swapProcessor.process(message);

        // then
        assertEquals(message.getField11(), newMessage.getField12());
        assertEquals(message.getField12(), newMessage.getField11());
    }
}

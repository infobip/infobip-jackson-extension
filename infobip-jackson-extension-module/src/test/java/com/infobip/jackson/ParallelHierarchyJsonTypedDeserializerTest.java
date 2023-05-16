package com.infobip.jackson;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.var;
import org.junit.jupiter.api.Test;

@AllArgsConstructor
class ParallelHierarchyJsonTypedDeserializerTest extends TestBase {

    @Test
    void shouldDeserializeInboundSmsMessageAsMessageFromJson() throws JsonProcessingException {
        // given
        String json ="{'direction':'INBOUND','channel':'SMS'}";

        // when
        var actual = objectMapper.readValue(json, Message.class);

        // then
        then(actual).isEqualTo(new InboundSmsMessage());
    }

    @Test
    void shouldDeserializeInboundSmsMessageAsMessageFromSerializedInboundSmsMessage() throws JsonProcessingException {
        // given
        String json =objectMapper.writeValueAsString(new InboundSmsMessage());

        // when
        var actual = objectMapper.readValue(json, Message.class);

        // then
        then(actual).isEqualTo(new InboundSmsMessage());
    }

    @Test
    void shouldDeserializeInboundSmsMessageAsInboundMessageFromJson() throws JsonProcessingException {
        // given
        String json ="{'direction':'INBOUND','channel':'SMS'}";

        // when
        var actual = objectMapper.readValue(json, InboundMessage.class);

        // then
        then(actual).isEqualTo(new InboundSmsMessage());
    }

    @Test
    void shouldDeserializeInboundSmsMessageAsInboundMessageFromInboundSmsMessage() throws JsonProcessingException {
        // given
        String json =objectMapper.writeValueAsString(new InboundSmsMessage());

        // when
        var actual = objectMapper.readValue(json, InboundMessage.class);

        // then
        then(actual).isEqualTo(new InboundSmsMessage());
    }

    @Test
    void shouldDeserializeListOfMessages() throws JsonProcessingException {
        // given
        String json =objectMapper.writeValueAsString(Arrays.asList(new InboundSmsMessage()));

        // when
        var actual = objectMapper.readValue(json, new TypeReference<List<Message>>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new InboundSmsMessage()));
    }

    @Test
    void shouldDeserializeListOfInboundMessages() throws JsonProcessingException {
        // given
        String json =objectMapper.writeValueAsString(Arrays.asList(new InboundSmsMessage()));

        // when
        var actual = objectMapper.readValue(json, new TypeReference<List<InboundMessage>>() {
        });

        // then
        then(actual).isEqualTo(Arrays.asList(new InboundSmsMessage()));
    }

    @Test
    void shouldDeserializeInboundSmsMessageAsInboundSmsMessageFromJson() throws JsonProcessingException {
        // given
        String json ="{'direction':'INBOUND','channel':'SMS'}";

        // when
        var actual = objectMapper.readValue(json, InboundSmsMessage.class);

        // then
        then(actual).isEqualTo(new InboundSmsMessage());
    }

    @JsonTypeResolveWith(MessageJsonTypeResolver.class)
    interface Message {
        Direction getDirection();

        Channel getChannel();
    }

    static class MessageJsonTypeResolver extends SimpleJsonTypeResolver<Direction> {

        public MessageJsonTypeResolver() {
            super(Direction.class, "direction");
        }
    }

    @Getter
    @AllArgsConstructor
    enum Direction implements TypeProvider<Message> {
        INBOUND(InboundMessage.class),
        OUTBOUND(OutboundMessage.class);

        private final Class<? extends Message> type;
    }

    @Getter
    @AllArgsConstructor
    enum Channel {
        SMS(InboundSmsMessage.class, OutboundSmsMessage.class);

        private final Class<? extends InboundMessage> inboundMessageType;
        private final Class<? extends OutboundMessage> outboundMessageType;
    }

    @JsonTypeResolveWith(InboundMessageJsonTypeResolver.class)
    interface InboundMessage extends Message {
        @Override
        default Direction getDirection() {
            return Direction.INBOUND;
        }
    }

    static class InboundMessageJsonTypeResolver extends CompositeJsonTypeResolver<Channel> {

        public InboundMessageJsonTypeResolver() {
            super(Channel.class, "channel", Channel::getInboundMessageType);
        }
    }

    @JsonTypeResolveWith(OutboundMessageJsonTypeResolver.class)
    interface OutboundMessage extends Message {
        @Override
        default Direction getDirection() {
            return Direction.OUTBOUND;
        }
    }

    static class OutboundMessageJsonTypeResolver extends CompositeJsonTypeResolver<Channel> {

        public OutboundMessageJsonTypeResolver() {
            super(Channel.class, "channel", Channel::getOutboundMessageType);
        }
    }

    @Value
    static class InboundSmsMessage implements InboundMessage {
        @Override
        public Channel getChannel() {
            return Channel.SMS;
        }

        @Override
        public Direction getDirection() {
            return Direction.INBOUND;
        }
    }

    static class OutboundSmsMessage implements OutboundMessage {
        @Override
        public Channel getChannel() {
            return Channel.SMS;
        }

        @Override
        public Direction getDirection() {
            return Direction.OUTBOUND;
        }
    }
}
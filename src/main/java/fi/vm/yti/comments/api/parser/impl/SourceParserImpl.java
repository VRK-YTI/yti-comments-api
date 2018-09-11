package fi.vm.yti.comments.api.parser.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.yti.comments.api.dto.SourceDTO;
import fi.vm.yti.comments.api.exception.JsonParsingException;
import fi.vm.yti.comments.api.parser.SourceParser;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_406;

@Component
public class SourceParserImpl implements AbstractBaseParser, SourceParser {

    private static final Logger LOG = LoggerFactory.getLogger(SourceParserImpl.class);

    public SourceDTO parseSourceFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final SourceDTO source;
        try {
            source = mapper.readValue(jsonPayload, SourceDTO.class);
        } catch (final IOException e) {
            LOG.error("Source parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return source;
    }

    public Set<SourceDTO> parseSourcesFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final Set<SourceDTO> sources;
        try {
            sources = mapper.readValue(jsonPayload, new TypeReference<Set<SourceDTO>>() {
            });
        } catch (final IOException e) {
            LOG.error("Sources parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return sources;
    }
}

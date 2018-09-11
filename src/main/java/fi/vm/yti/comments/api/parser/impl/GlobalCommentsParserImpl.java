package fi.vm.yti.comments.api.parser.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.yti.comments.api.dto.GlobalCommentsDTO;
import fi.vm.yti.comments.api.exception.JsonParsingException;
import fi.vm.yti.comments.api.parser.GlobalCommentsParser;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_406;

@Component
public class GlobalCommentsParserImpl implements AbstractBaseParser, GlobalCommentsParser {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalCommentsParserImpl.class);

    public GlobalCommentsDTO parseGlobalCommentsFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final GlobalCommentsDTO globalComments;
        try {
            globalComments = mapper.readValue(jsonPayload, GlobalCommentsDTO.class);
        } catch (final IOException e) {
            LOG.error("GlobalComments parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return globalComments;
    }

    public Set<GlobalCommentsDTO> parseGlobalCommentsSetFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final Set<GlobalCommentsDTO> globalComments;
        try {
            globalComments = mapper.readValue(jsonPayload, new TypeReference<Set<GlobalCommentsDTO>>() {
            });
        } catch (final IOException e) {
            LOG.error("GlobalComments parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return globalComments;
    }
}

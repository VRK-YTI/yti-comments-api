package fi.vm.yti.comments.api.parser.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.exception.JsonParsingException;
import fi.vm.yti.comments.api.parser.CommentThreadParser;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_406;

@Component
public class CommentThreadParserImpl implements AbstractBaseParser, CommentThreadParser {

    private static final Logger LOG = LoggerFactory.getLogger(CommentThreadParserImpl.class);

    public CommentThreadDTO parseCommentThreadFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final CommentThreadDTO commentThread;
        try {
            commentThread = mapper.readValue(jsonPayload, CommentThreadDTO.class);
        } catch (final IOException e) {
            LOG.error("CommentThread parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return commentThread;
    }

    public Set<CommentThreadDTO> parseCommentThreadsFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final Set<CommentThreadDTO> commentThreads;
        try {
            commentThreads = mapper.readValue(jsonPayload, new TypeReference<Set<CommentThreadDTO>>() {
            });
        } catch (final IOException e) {
            LOG.error("CommentThreads parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return commentThreads;
    }
}

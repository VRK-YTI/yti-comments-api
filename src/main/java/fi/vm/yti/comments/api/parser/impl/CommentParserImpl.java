package fi.vm.yti.comments.api.parser.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.exception.JsonParsingException;
import fi.vm.yti.comments.api.parser.CommentParser;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_406;

@Component
public class CommentParserImpl implements AbstractBaseParser, CommentParser {

    private static final Logger LOG = LoggerFactory.getLogger(CommentParserImpl.class);

    public CommentDTO parseCommentFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final CommentDTO comment;
        try {
            comment = mapper.readValue(jsonPayload, CommentDTO.class);
        } catch (final IOException e) {
            LOG.error("Comment parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return comment;
    }

    public Set<CommentDTO> parseCommentsFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final Set<CommentDTO> comments;
        try {
            comments = mapper.readValue(jsonPayload, new TypeReference<Set<CommentDTO>>() {
            });
        } catch (final IOException e) {
            LOG.error("Comments parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return comments;
    }
}

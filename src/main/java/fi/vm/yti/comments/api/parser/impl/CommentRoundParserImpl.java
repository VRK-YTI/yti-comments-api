package fi.vm.yti.comments.api.parser.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.exception.JsonParsingException;
import fi.vm.yti.comments.api.parser.CommentRoundParser;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_406;

@Component
public class CommentRoundParserImpl implements AbstractBaseParser, CommentRoundParser {

    private static final Logger LOG = LoggerFactory.getLogger(CommentRoundParserImpl.class);

    public CommentRoundDTO parseCommentRoundFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final CommentRoundDTO commentRound;
        try {
            commentRound = mapper.readValue(jsonPayload, CommentRoundDTO.class);
        } catch (final IOException e) {
            LOG.error("CommentRound parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return commentRound;
    }

    public Set<CommentRoundDTO> parseCommentRoundsFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final Set<CommentRoundDTO> commentRounds;
        try {
            commentRounds = mapper.readValue(jsonPayload, new TypeReference<Set<CommentRoundDTO>>() {
            });
        } catch (final IOException e) {
            LOG.error("CommentRounds parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return commentRounds;
    }
}

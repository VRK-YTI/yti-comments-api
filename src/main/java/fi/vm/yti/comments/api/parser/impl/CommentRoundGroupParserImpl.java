package fi.vm.yti.comments.api.parser.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.yti.comments.api.dto.CommentRoundGroupDTO;
import fi.vm.yti.comments.api.exception.JsonParsingException;
import fi.vm.yti.comments.api.parser.CommentRoundGroupParser;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_406;

@Component
public class CommentRoundGroupParserImpl implements AbstractBaseParser, CommentRoundGroupParser {

    private static final Logger LOG = LoggerFactory.getLogger(CommentRoundGroupParserImpl.class);

    public CommentRoundGroupDTO parseCommentRoundGroupFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final CommentRoundGroupDTO commentRoundGroup;
        try {
            commentRoundGroup = mapper.readValue(jsonPayload, CommentRoundGroupDTO.class);
        } catch (final IOException e) {
            LOG.error("CommentRoundGroup parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return commentRoundGroup;
    }

    public Set<CommentRoundGroupDTO> parseCommentRoundGroupsFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final Set<CommentRoundGroupDTO> commentRoundGroups;
        try {
            commentRoundGroups = mapper.readValue(jsonPayload, new TypeReference<Set<CommentRoundGroupDTO>>() {
            });
        } catch (final IOException e) {
            LOG.error("CommentRoundGroup parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return commentRoundGroups;
    }
}

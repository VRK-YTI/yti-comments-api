package fi.vm.yti.comments.api.parser.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.JsonParsingException;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.parser.CommentRoundParser;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_406;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_END_BEFORE_START_DATE;

@Component
public class CommentRoundParserImpl implements AbstractBaseParser, CommentRoundParser {

    private static final Logger LOG = LoggerFactory.getLogger(CommentRoundParserImpl.class);

    public CommentRoundDTO parseCommentRoundFromJson(final String jsonPayload) {
        final ObjectMapper mapper = createObjectMapper();
        final CommentRoundDTO commentRound;
        try {
            commentRound = mapper.readValue(jsonPayload, CommentRoundDTO.class);
            validateDatesForCommentRound(commentRound);
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
            commentRounds.forEach(this::validateDatesForCommentRound);
        } catch (final IOException e) {
            LOG.error("CommentRounds parsing failed from JSON!", e);
            throw new JsonParsingException(ERR_MSG_USER_406);
        }
        return commentRounds;
    }

    private void validateDatesForCommentRound(final CommentRoundDTO commentRound) {
        if (!validateDates(commentRound.getStartDate(), commentRound.getEndDate())) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_END_BEFORE_START_DATE));
        }
    }

    private boolean validateDates(final LocalDate startDate,
                                  final LocalDate endDate) {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}

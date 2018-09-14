package fi.vm.yti.comments.api.dao.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dao.SourceDao;
import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.dto.SourceDTO;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.Source;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.jpa.CommentRoundRepository;

@Component
public class CommentRoundDaoImpl implements CommentRoundDao {

    private final CommentRoundRepository commentRoundRepository;
    private final SourceDao sourceDao;

    @Inject
    public CommentRoundDaoImpl(final CommentRoundRepository commentRoundRepository,
                               final DtoMapper dtoMapper,
                               final SourceDao sourceDao) {
        this.commentRoundRepository = commentRoundRepository;
        this.sourceDao = sourceDao;
    }

    public Set<CommentRound> findAll() {
        return commentRoundRepository.findAll();
    }

    public CommentRound findById(final UUID commentRoundId) {
        return commentRoundRepository.findById(commentRoundId);
    }

    public CommentRound addOrUpdateCommentRoundFromDto(final CommentRoundDTO fromCommentRound) {
        final CommentRound commentRound = createOrUpdateCommentRound(fromCommentRound);
        commentRoundRepository.save(commentRound);
        return commentRound;
    }

    public Set<CommentRound> addOrUpdateCommentRoundsFromDtos(final Set<CommentRoundDTO> fromCommentRounds) {
        final Set<CommentRound> commentRounds = new HashSet<>();
        for (final CommentRoundDTO fromCommentRound : fromCommentRounds) {
            commentRounds.add(createOrUpdateCommentRound(fromCommentRound));
        }
        commentRoundRepository.saveAll(commentRounds);
        return commentRounds;
    }

    private CommentRound createOrUpdateCommentRound(final CommentRoundDTO fromCommentRound) {
        final CommentRound existingCommentRound;
        if (fromCommentRound.getId() != null) {
            existingCommentRound = commentRoundRepository.findById(fromCommentRound.getId());
        } else {
            existingCommentRound = null;
        }
        final CommentRound commentRound;
        if (existingCommentRound != null) {
            commentRound = updateCommentRound(existingCommentRound, fromCommentRound);
        } else {
            commentRound = createCommentRound(fromCommentRound);
        }
        return commentRound;

    }

    private CommentRound createCommentRound(final CommentRoundDTO fromCommentRound) {
        final CommentRound commentRound = new CommentRound();
        commentRound.setId(UUID.randomUUID());
        commentRound.setUserId(fromCommentRound.getUserId());
        commentRound.setDescription(fromCommentRound.getDescription());
        commentRound.setLabel(fromCommentRound.getLabel());
        commentRound.setStatus(fromCommentRound.getStatus());
        commentRound.setOpenComments(fromCommentRound.getOpenComments());
        commentRound.setFixedComments(fromCommentRound.getFixedComments());
        commentRound.setStartDate(fromCommentRound.getStartDate());
        commentRound.setEndDate(fromCommentRound.getEndDate());
        final LocalDateTime timeStamp = LocalDateTime.now();
        commentRound.setCreated(timeStamp);
        commentRound.setModified(timeStamp);
        resolveSource(commentRound, fromCommentRound);
        return commentRound;
    }

    private CommentRound updateCommentRound(final CommentRound existingCommentRound,
                                            final CommentRoundDTO fromCommentRound) {
        existingCommentRound.setUserId(fromCommentRound.getUserId());
        existingCommentRound.setDescription(fromCommentRound.getDescription());
        existingCommentRound.setLabel(fromCommentRound.getLabel());
        existingCommentRound.setStatus(fromCommentRound.getStatus());
        existingCommentRound.setOpenComments(fromCommentRound.getOpenComments());
        existingCommentRound.setFixedComments(fromCommentRound.getFixedComments());
        existingCommentRound.setStartDate(fromCommentRound.getStartDate());
        existingCommentRound.setEndDate(fromCommentRound.getEndDate());
        existingCommentRound.setModified(LocalDateTime.now());
        resolveSource(existingCommentRound, fromCommentRound);
        return existingCommentRound;
    }

    private void resolveSource(final CommentRound commentRound,
                               final CommentRoundDTO fromCommentRound) {
        final SourceDTO sourceDto = fromCommentRound.getSource();
        if (sourceDto != null && sourceDto.getId() != null) {
            final Source source = sourceDao.findById(sourceDto.getId());
            if (source != null) {
                commentRound.setSource(source);
            } else {
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid source in DTO data."));
            }
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid source in DTO data."));
        }
    }
}

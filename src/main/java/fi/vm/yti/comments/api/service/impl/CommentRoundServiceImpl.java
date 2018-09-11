package fi.vm.yti.comments.api.service.impl;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.service.CommentRoundService;

@Component
public class CommentRoundServiceImpl implements CommentRoundService {

    private final CommentRoundDao commentRoundDao;
    private final DtoMapper dtoMapper;

    public CommentRoundServiceImpl(final CommentRoundDao commentRoundDao,
                             final DtoMapper dtoMapper) {
        this.commentRoundDao = commentRoundDao;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public Set<CommentRoundDTO> findAll() {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.findAll());
    }

    @Transactional
    public CommentRoundDTO findById(final UUID commentRoundId) {
        return dtoMapper.mapDeepCommentRound(commentRoundDao.findById(commentRoundId));
    }

    @Transactional
    public CommentRoundDTO addOrUpdateCommentRoundFromDto(final CommentRoundDTO fromCommentRound) {
        return dtoMapper.mapDeepCommentRound(commentRoundDao.addOrUpdateCommentRoundFromDto(fromCommentRound));
    }

    @Transactional
    public Set<CommentRoundDTO> addOrUpdateCommentRoundsFromDtos(final Set<CommentRoundDTO> fromCommentRounds) {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.addOrUpdateCommentRoundsFromDtos(fromCommentRounds));
    }
}

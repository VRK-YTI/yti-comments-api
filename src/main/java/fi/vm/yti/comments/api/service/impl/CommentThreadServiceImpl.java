package fi.vm.yti.comments.api.service.impl;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dao.CommentThreadDao;
import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.service.CommentThreadService;

@Component
public class CommentThreadServiceImpl implements CommentThreadService {

    private final DtoMapper dtoMapper;
    private final CommentThreadDao commentThreadDao;
    private final CommentRoundDao commentRoundDao;

    public CommentThreadServiceImpl(final DtoMapper dtoMapper,
                                    final CommentThreadDao commentThreadDao,
                                    final CommentRoundDao commentRoundDao) {
        this.dtoMapper = dtoMapper;
        this.commentThreadDao = commentThreadDao;
        this.commentRoundDao = commentRoundDao;
    }

    @Transactional
    public Set<CommentThreadDTO> findAll() {
        return dtoMapper.mapDeepCommentThreads(commentThreadDao.findAll());
    }

    @Transactional
    public CommentThreadDTO findById(final UUID commentThreadId) {
        return dtoMapper.mapDeepCommentThread(commentThreadDao.findById(commentThreadId));
    }

    @Transactional
    public Set<CommentThreadDTO> findByCommentRoundId(final UUID commentRoundId) {
        return dtoMapper.mapDeepCommentThreads(commentThreadDao.findByCommentRoundId(commentRoundId));
    }

    @Transactional
    public CommentThreadDTO addOrUpdateCommentThreadFromDto(final UUID commentRoundId,
                                                            final CommentThreadDTO fromCommentThread) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (commentRound != null) {
            return dtoMapper.mapDeepCommentThread(commentThreadDao.addOrUpdateCommentThreadFromDto(commentRound, fromCommentThread));
        } else {
            throw new NotFoundException();
        }
    }

    @Transactional
    public Set<CommentThreadDTO> addOrUpdateCommentThreadsFromDtos(final UUID commentRoundId,
                                                                   final Set<CommentThreadDTO> fromCommentThreads) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (commentRound != null) {
            return dtoMapper.mapDeepCommentThreads(commentThreadDao.addOrUpdateCommentThreadsFromDtos(commentRound, fromCommentThreads));
        } else {
            throw new NotFoundException();
        }
    }

    @Transactional
    public void deleteCommentThread(final CommentThread commentThread) {
        commentThreadDao.deleteCommentThread(commentThread);
    }
}

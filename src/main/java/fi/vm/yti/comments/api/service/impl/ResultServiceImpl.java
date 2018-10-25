package fi.vm.yti.comments.api.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentDao;
import fi.vm.yti.comments.api.dao.CommentThreadDao;
import fi.vm.yti.comments.api.dto.CommentThreadResultDTO;
import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.service.ResultService;

@Component
public class ResultServiceImpl implements ResultService {

    private final CommentThreadDao commentThreadDao;
    private final CommentDao commentDao;

    @Inject
    public ResultServiceImpl(@Lazy final CommentThreadDao commentThreadDao,
                             @Lazy final CommentDao commentDao) {
        this.commentThreadDao = commentThreadDao;
        this.commentDao = commentDao;
    }

    @Transactional
    public Set<CommentThreadResultDTO> getResultsForCommentThread(final UUID commentThreadId) {
        final Map<String, Integer> commentThreadResultsMap = new HashMap<>();
        final CommentThread commentThread = commentThreadDao.findById(commentThreadId);
        if (commentThread != null) {
            final Set<Comment> comments = commentDao.findByCommentThreadId(commentThreadId);
            comments.forEach(comment -> {
                final String proposedStatus = comment.getProposedStatus();
                if (proposedStatus != null && !proposedStatus.isEmpty() && !"NOSTATUS".equalsIgnoreCase(proposedStatus)) {
                    if (commentThreadResultsMap.get(proposedStatus) != null) {
                        commentThreadResultsMap.put(proposedStatus, commentThreadResultsMap.get(proposedStatus) + 1);
                    } else {
                        commentThreadResultsMap.put(proposedStatus, 1);
                    }
                }
            });
        }
        final int totalCount = commentThreadResultsMap.values().stream().mapToInt(i -> i).sum();
        final Set<CommentThreadResultDTO> commentThreadResults = new HashSet<>();
        commentThreadResultsMap.forEach((status, count) -> {
            final CommentThreadResultDTO commentThreadResult = new CommentThreadResultDTO();
            commentThreadResult.setStatus(status);
            commentThreadResult.setCount(count);
            commentThreadResult.setPercentage(count / totalCount * 100);
            commentThreadResults.add(commentThreadResult);
        });
        return commentThreadResults;
    }
}

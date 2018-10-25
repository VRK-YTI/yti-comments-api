package fi.vm.yti.comments.api.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.entity.CommentRound;

@Component
public class CommentRoundScheduler {

    private static final String STATUS_INPROGRESS = "INPROGRESS";
    private static final String STATUS_ENDED = "ENDED";

    private CommentRoundDao commentRoundDao;

    @Inject
    public CommentRoundScheduler(final CommentRoundDao commentRoundDao) {
        this.commentRoundDao = commentRoundDao;
    }

    @Scheduled(cron = "1 0 * * * *")
    private void updateCommentRoundStatuses() {
        updateStatuses();
    }

    @Transactional
    public void updateStatuses() {
        final Set<CommentRound> commentRounds = commentRoundDao.findByStatusAndEndDateBefore(STATUS_INPROGRESS, LocalDate.now());
        commentRounds.forEach(commentRound -> {
            commentRound.setStatus(STATUS_ENDED);
            commentRound.setModified(LocalDateTime.now());
        });
        commentRoundDao.saveAll(commentRounds);
    }
}

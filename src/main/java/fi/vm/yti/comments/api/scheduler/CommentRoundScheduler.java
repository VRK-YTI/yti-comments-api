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
import static fi.vm.yti.comments.api.constants.ApiConstants.*;

@Component
public class CommentRoundScheduler {

    private final CommentRoundDao commentRoundDao;

    @Inject
    public CommentRoundScheduler(final CommentRoundDao commentRoundDao) {
        this.commentRoundDao = commentRoundDao;
    }

    @Scheduled(cron = "1 0 * * * *", zone = "Europe/Helsinki")
    public void updateCommentRoundStatuses() {
        updateStatuses();
    }

    @Transactional
    public void updateStatuses() {
        updateEnding();
        updateStarting();
    }

    private void updateEnding() {
        final Set<CommentRound> commentRounds = commentRoundDao.findByStatusAndEndDateBefore(STATUS_INPROGRESS, LocalDate.now());
        commentRounds.forEach(commentRound -> {
            commentRound.setStatus(STATUS_ENDED);
            commentRound.setModified(LocalDateTime.now());
        });
        commentRoundDao.saveAll(commentRounds);
    }

    private void updateStarting() {
        startRounds(STATUS_INCOMPLETE);
        startRounds(STATUS_AWAIT);
    }

    private void startRounds(final String status) {
        final Set<CommentRound> commentRounds = commentRoundDao.findByStatusAndStartDateAfter(status, LocalDate.now());
        commentRounds.forEach(commentRound -> {
            commentRound.setStatus(STATUS_INPROGRESS);
            commentRound.setModified(LocalDateTime.now());
        });
        commentRoundDao.saveAll(commentRounds);
    }
}

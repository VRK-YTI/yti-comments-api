package fi.vm.yti.comments.api.scheduler;

import java.time.Clock;
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
        changeRoundStatuses(STATUS_INPROGRESS, STATUS_ENDED);
        changeRoundStatuses(STATUS_AWAIT, STATUS_INPROGRESS);
        changeRoundStatuses(STATUS_INCOMPLETE, STATUS_INPROGRESS);
    }

    private void changeRoundStatuses(final String currentStatus,
                                     final String endStatus) {
        final Set<CommentRound> commentRounds;
        if (endStatus.equalsIgnoreCase(STATUS_ENDED)) {
            commentRounds = commentRoundDao.findByStatusAndEndDateBefore(currentStatus, LocalDate.now());
        } else {
            commentRounds = commentRoundDao.findByStatusAndStartDateAfter(currentStatus, LocalDate.now());
        }
        commentRounds.forEach(commentRound -> {
            commentRound.setStatus(endStatus);
            commentRound.setModified(LocalDateTime.now(Clock.systemUTC()));
        });
        commentRoundDao.saveAll(commentRounds);
    }
}

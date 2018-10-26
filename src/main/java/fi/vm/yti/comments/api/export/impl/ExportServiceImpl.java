package fi.vm.yti.comments.api.export.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.export.ExportService;
import fi.vm.yti.comments.api.service.UserService;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;

@Component
public class ExportServiceImpl implements ExportService {

    private static final String DATEFORMAT = "yyyy-MM-dd";
    private static final String DATEFORMAT_WITH_SECONDS = "yyyy-MM-dd HH:mm:ss";

    private final UserService userService;

    public ExportServiceImpl(final UserService userService) {
        this.userService = userService;
    }

    public Workbook exportCommentRoundToExcel(final CommentRound commentRound) {
        final Workbook workbook = new XSSFWorkbook();
        addCommentRoundSheet(workbook, commentRound);
        addCommentThreadsSheet(workbook, commentRound.getCommentThreads());
        addCommentsSheet(workbook, commentRound.getCommentThreads());
        return workbook;
    }

    private void addCommentRoundSheet(final Workbook workbook,
                                      final CommentRound commentRound) {
        final Sheet sheet = workbook.createSheet(EXCEL_SHEET_COMMENTROUND);
        final Row rowhead = sheet.createRow((short) 0);
        int j = 0;
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_ID);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_USER_ID);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_USER);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_LABEL);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_DESCRIPTION);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_STARTDATE);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_ENDDATE);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_CREATED);
        rowhead.createCell(j).setCellValue(CONTENT_HEADER_MODIFIED);
        final Row row = sheet.createRow(1);
        int k = 0;
        row.createCell(k++).setCellValue(checkEmptyValue(commentRound.getId().toString()));
        row.createCell(k++).setCellValue(checkEmptyValue(commentRound.getUserId().toString()));
        row.createCell(k++).setCellValue(checkEmptyValue(userService.getUserById(commentRound.getUserId()).getDisplayNameWithEmail()));
        row.createCell(k++).setCellValue(checkEmptyValue(commentRound.getLabel()));
        row.createCell(k++).setCellValue(checkEmptyValue(commentRound.getDescription()));
        row.createCell(k++).setCellValue(commentRound.getStartDate() != null ? formatDateWithISO8601(commentRound.getStartDate()) : "");
        row.createCell(k++).setCellValue(commentRound.getEndDate() != null ? formatDateWithISO8601(commentRound.getEndDate()) : "");
        row.createCell(k++).setCellValue(commentRound.getCreated() != null ? formatDateWithSeconds(commentRound.getCreated()) : "");
        row.createCell(k).setCellValue(commentRound.getModified() != null ? formatDateWithSeconds(commentRound.getModified()) : "");
        autoSizeColumns(sheet, j);
    }

    private void addCommentThreadsSheet(final Workbook workbook,
                                        final Set<CommentThread> commentThreads) {
        final Sheet sheet = workbook.createSheet(EXCEL_SHEET_COMMENTTHREADS);
        final Row rowhead = sheet.createRow((short) 0);
        int j = 0;
        final Set<String> labelLanguages = resolveCommentThreadLabelLanguages(commentThreads);
        final Set<String> descriptionLanguages = resolveCommentThreadDescriptionLanguages(commentThreads);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_ID);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_USER_ID);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_USER);
        for (final String language : labelLanguages) {
            rowhead.createCell(j++).setCellValue(CONTENT_HEADER_LABEL_PREFIX + language.toUpperCase());
        }
        for (final String language : descriptionLanguages) {
            rowhead.createCell(j++).setCellValue(CONTENT_HEADER_DESCRIPTION_PREFIX + language.toUpperCase());
        }
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_PROPOSEDSTATUS);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_PROPOSEDTEXT);
        rowhead.createCell(j).setCellValue(CONTENT_HEADER_CREATED);
        int i = 1;
        for (final CommentThread commentThread : commentThreads) {
            final Row row = sheet.createRow(i++);
            int k = 0;
            row.createCell(k++).setCellValue(checkEmptyValue(commentThread.getId().toString()));
            row.createCell(k++).setCellValue(checkEmptyValue(commentThread.getUserId().toString()));
            row.createCell(k++).setCellValue(checkEmptyValue(userService.getUserById(commentThread.getUserId()).getDisplayNameWithEmail()));
            for (final String language : labelLanguages) {
                row.createCell(k++).setCellValue(commentThread.getDescription().get(language));
            }
            for (final String language : descriptionLanguages) {
                row.createCell(k++).setCellValue(commentThread.getDescription().get(language));
            }
            row.createCell(k++).setCellValue(checkEmptyValue(commentThread.getProposedStatus()));
            row.createCell(k++).setCellValue(checkEmptyValue(commentThread.getProposedText()));
            row.createCell(k).setCellValue(commentThread.getCreated() != null ? formatDateWithSeconds(commentThread.getCreated()) : "");
        }
        autoSizeColumns(sheet, j);
    }

    private void addCommentsSheet(final Workbook workbook,
                                  final Set<CommentThread> commentThreads) {
        final Sheet sheet = workbook.createSheet(EXCEL_SHEET_COMMENTS);
        final Row rowhead = sheet.createRow((short) 0);
        int j = 0;
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_ID);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_COMMENTTHREAD_ID);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_USER_ID);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_USER);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_CONTENT);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_PROPOSEDSTATUS);
        rowhead.createCell(j++).setCellValue(CONTENT_HEADER_PARENTCOMMENT);
        rowhead.createCell(j).setCellValue(CONTENT_HEADER_CREATED);
        int i = 1;
        for (final CommentThread commentThread : commentThreads) {
            for (final Comment comment : commentThread.getComments()) {
                final Row row = sheet.createRow(i++);
                int k = 0;
                row.createCell(k++).setCellValue(checkEmptyValue(comment.getId().toString()));
                row.createCell(k++).setCellValue(checkEmptyValue(commentThread.getId().toString()));
                row.createCell(k++).setCellValue(checkEmptyValue(comment.getUserId().toString()));
                row.createCell(k++).setCellValue(checkEmptyValue(userService.getUserById(comment.getUserId()).getDisplayNameWithEmail()));
                row.createCell(k++).setCellValue(checkEmptyValue(comment.getContent()));
                row.createCell(k++).setCellValue(checkEmptyValue(comment.getProposedStatus()));
                row.createCell(k++).setCellValue(comment.getParentComment() != null ? comment.getParentComment().getId().toString() : "");
                row.createCell(k).setCellValue(comment.getCreated() != null ? formatDateWithSeconds(comment.getCreated()) : "");
            }
        }
        autoSizeColumns(sheet, j);
    }

    private Set<String> resolveCommentThreadLabelLanguages(final Set<CommentThread> commentThreads) {
        final Set<String> languages = new LinkedHashSet<>();
        commentThreads.forEach(commentThread -> languages.addAll(commentThread.getLabel().keySet()));
        return languages;
    }

    private Set<String> resolveCommentThreadDescriptionLanguages(final Set<CommentThread> commentThreads) {
        final Set<String> languages = new LinkedHashSet<>();
        commentThreads.forEach(commentThread -> languages.addAll(commentThread.getDescription().keySet()));
        return languages;
    }

    private String checkEmptyValue(final String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    private String formatDateWithISO8601(final LocalDate date) {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATEFORMAT);
        return date.format(dateFormatter);
    }

    private String formatDateWithSeconds(final LocalDateTime date) {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATEFORMAT_WITH_SECONDS);
        return date.format(dateFormatter);
    }

    private void autoSizeColumns(final Sheet sheet,
                                 final int columnCount) {
        for (int i = 0; i <= columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}

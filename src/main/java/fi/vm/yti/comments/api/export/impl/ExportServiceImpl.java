package fi.vm.yti.comments.api.export.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.export.ExportService;
import fi.vm.yti.comments.api.service.ResultService;
import fi.vm.yti.comments.api.service.UserService;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;

@Component
public class ExportServiceImpl implements ExportService {

    private static final String DATEFORMAT = "yyyy-MM-dd";
    private static final String DATEFORMAT_WITH_SECONDS = "yyyy-MM-dd HH:mm:ss";

    private final UserService userService;
    private final ResultService resultService;

    public ExportServiceImpl(final UserService userService,
                             final ResultService resultService) {
        this.userService = userService;
        this.resultService = resultService;
    }

    public Workbook exportCommentRoundToExcel(final CommentRound commentRound) {
        final Workbook workbook = new XSSFWorkbook();
        addCommentRoundSheet(workbook, commentRound);
        final Set<CommentThread> commentThreads = commentRound.getCommentThreads();
        addCommentThreadsSheet(workbook, commentThreads);
        for (final CommentThread commentThread : commentThreads) {
            addCommentsSheetForCommentThread(workbook, commentThread);
        }
        return workbook;
    }

    private void addCommentRoundSheet(final Workbook workbook,
                                      final CommentRound commentRound) {
        final Sheet sheet = workbook.createSheet(EXCEL_SHEET_COMMENTROUND);
        final Row rowhead = sheet.createRow((short) 0);
        int j = 0;
        final CellStyle style = createCellStyle(workbook);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_URI);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_SOURCE);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_ID);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_USER_ID);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_USER);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_LABEL);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_DESCRIPTION);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_STARTDATE);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_ENDDATE);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_CREATED);
        addCellToRow(rowhead, style, j, CONTENT_HEADER_MODIFIED);
        final Row row = sheet.createRow(1);
        int k = 0;
        addCellToRow(row, style, k++, checkEmptyValue(commentRound.getSource().getContainerUri()));
        addCellToRow(row, style, k++, checkEmptyValue(commentRound.getSource().getContainerType()));
        addCellToRow(row, style, k++, checkEmptyValue(commentRound.getId().toString()));
        addCellToRow(row, style, k++, checkEmptyValue(commentRound.getUserId().toString()));
        addCellToRow(row, style, k++, checkEmptyValue(userService.getUserById(commentRound.getUserId()).getDisplayNameWithEmail()));
        addCellToRow(row, style, k++, checkEmptyValue(commentRound.getLabel()));
        addCellToRow(row, style, k++, checkEmptyValue(commentRound.getDescription()));
        addCellToRow(row, style, k++, commentRound.getStartDate() != null ? formatDateWithISO8601(commentRound.getStartDate()) : "");
        addCellToRow(row, style, k++, commentRound.getEndDate() != null ? formatDateWithISO8601(commentRound.getEndDate()) : "");
        addCellToRow(row, style, k++, commentRound.getCreated() != null ? formatDateWithSeconds(commentRound.getCreated()) : "");
        addCellToRow(row, style, k, commentRound.getModified() != null ? formatDateWithSeconds(commentRound.getModified()) : "");
        autoSizeColumns(sheet, j);
    }

    private void addCommentThreadsSheet(final Workbook workbook,
                                        final Set<CommentThread> commentThreads) {
        final Sheet sheet = workbook.createSheet(EXCEL_SHEET_COMMENTTHREADS);
        final Row rowhead = sheet.createRow((short) 0);
        int j = 0;
        final Set<String> labelLanguages = resolveCommentThreadLabelLanguages(commentThreads);
        final Set<String> descriptionLanguages = resolveCommentThreadDescriptionLanguages(commentThreads);
        final CellStyle style = createCellStyle(workbook);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_URI);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_RESULT);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_COMMENTS_SHEET);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_ID);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_USER_ID);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_USER);
        for (final String language : labelLanguages) {
            addCellToRow(rowhead, style, j++, CONTENT_HEADER_LABEL_PREFIX + language.toUpperCase());
        }
        for (final String language : descriptionLanguages) {
            addCellToRow(rowhead, style, j++, CONTENT_HEADER_DESCRIPTION_PREFIX + language.toUpperCase());
        }
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_PROPOSEDSTATUS);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_PROPOSEDTEXT);
        addCellToRow(rowhead, style, j, CONTENT_HEADER_CREATED);
        int i = 1;
        for (final CommentThread commentThread : commentThreads) {
            final Row row = sheet.createRow(i++);
            int k = 0;
            addCellToRow(row, style, k++, checkEmptyValue(commentThread.getResourceUri()));
            addCellToRow(row, style, k++, checkEmptyValue(resultService.getResultsForCommentThreadAsText(commentThread.getId())));
            addCellToRow(row, style, k++, checkEmptyValue(EXCEL_SHEET_COMMENTS + "_" + commentThread.getId().toString()));
            addCellToRow(row, style, k++, checkEmptyValue(commentThread.getId().toString()));
            addCellToRow(row, style, k++, checkEmptyValue(commentThread.getUserId().toString()));
            addCellToRow(row, style, k++, checkEmptyValue(userService.getUserById(commentThread.getUserId()).getDisplayNameWithEmail()));
            for (final String language : labelLanguages) {
                addCellToRow(row, style, k++, checkEmptyValue(commentThread.getDescription().get(language)));
            }
            for (final String language : descriptionLanguages) {
                addCellToRow(row, style, k++, checkEmptyValue(commentThread.getDescription().get(language)));
            }
            addCellToRow(row, style, k++, checkEmptyValue(commentThread.getProposedStatus()));
            addCellToRow(row, style, k++, checkEmptyValue(commentThread.getProposedText()));
            addCellToRow(row, style, k, checkEmptyValue(commentThread.getCreated() != null ? formatDateWithSeconds(commentThread.getCreated()) : ""));
        }
        autoSizeColumns(sheet, j);
    }

    private void addCommentsSheetForCommentThread(final Workbook workbook,
                                                  final CommentThread commentThread) {
        final Sheet sheet = workbook.createSheet(EXCEL_SHEET_COMMENTS + "_" + commentThread.getId().toString());
        final Row rowhead = sheet.createRow((short) 0);
        int j = 0;
        final CellStyle style = createCellStyle(workbook);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_ID);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_COMMENTTHREAD_ID);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_USER_ID);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_USER);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_CONTENT);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_PROPOSEDSTATUS);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_ENDSTATUS);
        addCellToRow(rowhead, style, j++, CONTENT_HEADER_PARENTCOMMENT);
        addCellToRow(rowhead, style, j, CONTENT_HEADER_CREATED);
        int i = 1;
        for (final Comment comment : commentThread.getComments()) {
            final Row row = sheet.createRow(i++);
            int k = 0;
            addCellToRow(row, style, k++, checkEmptyValue(comment.getId().toString()));
            addCellToRow(row, style, k++, checkEmptyValue(commentThread.getId().toString()));
            addCellToRow(row, style, k++, checkEmptyValue(comment.getUserId().toString()));
            addCellToRow(row, style, k++, checkEmptyValue(userService.getUserById(comment.getUserId()).getDisplayNameWithEmail()));
            addCellToRow(row, style, k++, checkEmptyValue(comment.getContent()));
            addCellToRow(row, style, k++, checkEmptyValue(comment.getProposedStatus()));
            addCellToRow(row, style, k++, checkEmptyValue(comment.getEndStatus()));
            addCellToRow(row, style, k++, comment.getParentComment() != null ? comment.getParentComment().getId().toString() : "");
            addCellToRow(row, style, k, comment.getCreated() != null ? formatDateWithSeconds(comment.getCreated()) : "");
        }
        autoSizeColumns(sheet, j);
    }

    private CellStyle createCellStyle(final Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        return style;
    }

    private void addCellToRow(final Row row,
                              final CellStyle style,
                              final int index,
                              final String value) {
        final Cell cell = row.createCell(index);
        cell.setCellStyle(style);
        cell.setCellValue(value);
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

package fi.vm.yti.comments.api.export;

import java.util.UUID;

import org.apache.poi.ss.usermodel.Workbook;

import fi.vm.yti.comments.api.entity.CommentRound;

public interface ExportService {

    Workbook exportCommentRoundToExcel(final CommentRound commentRound);
}

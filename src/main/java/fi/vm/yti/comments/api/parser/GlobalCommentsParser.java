package fi.vm.yti.comments.api.parser;

import java.util.Set;

import fi.vm.yti.comments.api.dto.GlobalCommentsDTO;

public interface GlobalCommentsParser {

    GlobalCommentsDTO parseGlobalCommentsFromJson(final String jsonPayload);

    Set<GlobalCommentsDTO> parseGlobalCommentsSetFromJson(final String jsonPayload);
}

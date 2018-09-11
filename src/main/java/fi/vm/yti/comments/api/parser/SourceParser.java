package fi.vm.yti.comments.api.parser;

import java.util.Set;

import fi.vm.yti.comments.api.dto.SourceDTO;

public interface SourceParser {

    SourceDTO parseSourceFromJson(final String jsonPayload);

    Set<SourceDTO> parseSourcesFromJson(final String jsonPayload);
}

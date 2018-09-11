package fi.vm.yti.comments.api.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.api.UrlGenerator;
import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentRoundGroup;
import fi.vm.yti.comments.api.entity.GlobalComments;
import fi.vm.yti.comments.api.entity.Source;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;

@Component
public class DtoMapper {

    private final UrlGenerator urlGenerator;

    public DtoMapper(final UrlGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
    }

    public Set<CommentDTO> mapComments(final Set<Comment> comments,
                                       boolean deep) {
        final Set<CommentDTO> commentDtos = new HashSet<>();
        for (final Comment comment : comments) {
            commentDtos.add(mapComment(comment, deep));
        }
        return commentDtos;
    }

    public Set<CommentDTO> mapDeepComments(final Set<Comment> comments) {
        final Set<CommentDTO> commentDtos = new HashSet<>();
        for (final Comment comment : comments) {
            commentDtos.add(mapDeepComment(comment));
        }
        return commentDtos;
    }

    public CommentDTO mapDeepComment(final Comment comment) {
        return mapComment(comment, true);
    }

    public CommentDTO mapComment(final Comment comment,
                                 final boolean deep) {
        if (comment == null) {
            return null;
        }
        final CommentDTO commentDto = new CommentDTO();
        final UUID id = comment.getId();
        commentDto.setId(id);
        commentDto.setUrl(urlGenerator.createResourceUrl(API_PATH_COMMENTS, id.toString()));
        commentDto.setCreated(comment.getCreated());
        commentDto.setModified(comment.getModified());
        commentDto.setContent(comment.getContent());
        commentDto.setResourceUri(comment.getResourceUri());
        commentDto.setUserId(comment.getUserId());
        commentDto.setProposedStatus(comment.getProposedStatus());
        if (deep) {
            commentDto.setCommentRound(mapCommentRound(comment.getCommentRound(), false));
            commentDto.setGlobalComments(mapGlobalComments(comment.getGlobalComments(), false));
            commentDto.setRelatedComment(mapComment(comment.getRelatedComment(), false));
        }
        return commentDto;
    }

    public Set<CommentRoundDTO> mapDeepCommentRounds(final Set<CommentRound> commentRounds) {
        final Set<CommentRoundDTO> commentRoundDtos = new HashSet<>();
        for (final CommentRound commentRound : commentRounds) {
            commentRoundDtos.add(mapDeepCommentRound(commentRound));
        }
        return commentRoundDtos;
    }

    public CommentRoundDTO mapDeepCommentRound(final CommentRound commentRound) {
        return mapCommentRound(commentRound, true);
    }

    public CommentRoundDTO mapCommentRound(final CommentRound commentRound,
                                           final boolean deep) {
        if (commentRound == null) {
            return null;
        }
        final CommentRoundDTO commentRoundDto = new CommentRoundDTO();
        final UUID id = commentRound.getId();
        commentRoundDto.setId(id);
        commentRoundDto.setUrl(urlGenerator.createResourceUrl(API_PATH_COMMENTROUNDS, id.toString()));
        commentRoundDto.setCreated(commentRound.getCreated());
        commentRoundDto.setModified(commentRound.getModified());
        commentRoundDto.setStartDate(commentRound.getStartDate());
        commentRoundDto.setEndDate(commentRound.getEndDate());
        commentRoundDto.setDescription(commentRound.getDescription());
        commentRoundDto.setLabel(commentRound.getLabel());
        commentRoundDto.setUserId(commentRound.getUserId());
        if (deep) {
            commentRoundDto.setSource(mapSource(commentRound.getSource(), false));
            commentRoundDto.setComments(mapComments(commentRound.getComments(), false));

        }
        return commentRoundDto;
    }

    public Set<GlobalCommentsDTO> mapDeepGlobalComments(final Set<GlobalComments> globalCommentsSet) {
        final Set<GlobalCommentsDTO> globalGommentsDtos = new HashSet<>();
        for (final GlobalComments globalComments : globalCommentsSet) {
            globalGommentsDtos.add(mapDeepGlobalComments(globalComments));
        }
        return globalGommentsDtos;
    }

    public GlobalCommentsDTO mapDeepGlobalComments(final GlobalComments globalComments) {
        return mapGlobalComments(globalComments, true);
    }

    public GlobalCommentsDTO mapGlobalComments(final GlobalComments globalComments,
                                               final boolean deep) {
        if (globalComments == null) {
            return null;
        }
        final GlobalCommentsDTO globalCommentsDto = new GlobalCommentsDTO();
        final UUID id = globalComments.getId();
        globalCommentsDto.setId(id);
        globalCommentsDto.setUrl(urlGenerator.createResourceUrl(API_PATH_GLOBALCOMMENTS, id.toString()));
        globalCommentsDto.setCreated(globalComments.getCreated());
        if (deep) {
            globalCommentsDto.setSource(mapSource(globalComments.getSource(), false));
            if (globalComments.getComments() != null) {
                globalCommentsDto.setComments(mapComments(globalComments.getComments(), false));
            }
        }
        return globalCommentsDto;
    }

    public Set<SourceDTO> mapDeepSources(final Set<Source> sources) {
        final Set<SourceDTO> sourceDtos = new HashSet<>();
        for (final Source source : sources) {
            sourceDtos.add(mapDeepSource(source));
        }
        return sourceDtos;
    }

    public SourceDTO mapDeepSource(final Source source) {
        return mapSource(source, true);
    }

    public SourceDTO mapSource(final Source source,
                               final boolean deep) {
        if (source == null) {
            return null;
        }
        final SourceDTO sourceDto = new SourceDTO();
        final UUID id = source.getId();
        sourceDto.setId(id);
        sourceDto.setUrl(urlGenerator.createResourceUrl(API_PATH_SOURCES, id.toString()));
        sourceDto.setContainerType(source.getContainerType());
        sourceDto.setContainerUri(source.getContainerUri());
        if (deep) {
            sourceDto.setGlobalComments(mapGlobalComments(source.getGlobalComments(), false));
        }
        return sourceDto;
    }

    public Set<CommentRoundGroupDTO> mapDeepCommentRoundGroups(final Set<CommentRoundGroup> commentRoundGroups) {
        final Set<CommentRoundGroupDTO> commentRoundGroupDtos = new HashSet<>();
        for (final CommentRoundGroup commentRoundGroup : commentRoundGroups) {
            commentRoundGroupDtos.add(mapDeepCommentRoundGroup(commentRoundGroup));
        }
        return commentRoundGroupDtos;
    }

    public CommentRoundGroupDTO mapDeepCommentRoundGroup(final CommentRoundGroup commentRoundGroup) {
        return mapCommentRoundGroup(commentRoundGroup, true);
    }

    public CommentRoundGroupDTO mapCommentRoundGroup(final CommentRoundGroup commentRoundGroup,
                                                     final boolean deep) {
        final CommentRoundGroupDTO commentRoundGroupDto = new CommentRoundGroupDTO();
        final UUID id = commentRoundGroup.getId();
        commentRoundGroupDto.setId(id);
        commentRoundGroupDto.setUrl(urlGenerator.createResourceUrl(API_PATH_COMMENTROUNDGROUPS, id.toString()));
        if (deep) {
            commentRoundGroupDto.setCommentRound(mapCommentRound(commentRoundGroup.getCommentRound(), false));
        }
        return commentRoundGroupDto;
    }
}

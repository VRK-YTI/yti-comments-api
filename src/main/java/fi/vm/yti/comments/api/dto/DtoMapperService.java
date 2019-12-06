package fi.vm.yti.comments.api.dto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.api.UrlGenerator;
import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.entity.Organization;
import fi.vm.yti.comments.api.entity.Source;
import fi.vm.yti.comments.api.service.ResultService;
import fi.vm.yti.comments.api.service.UserService;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;
import static fi.vm.yti.comments.api.utils.DateUtils.convertLocalDateTimeToDate;

@Component
public class DtoMapperService {

    private final UserService userService;
    private final UrlGenerator urlGenerator;
    private final ResultService resultService;

    @Inject
    public DtoMapperService(final UserService userService,
                            final UrlGenerator urlGenerator,
                            final ResultService resultService) {
        this.userService = userService;
        this.urlGenerator = urlGenerator;
        this.resultService = resultService;
    }

    @Transactional
    public Set<CommentDTO> mapComments(final Set<Comment> comments,
                                       boolean deep) {
        final Set<CommentDTO> commentDtos = new HashSet<>();
        if (comments != null && !comments.isEmpty()) {
            for (final Comment comment : comments) {
                commentDtos.add(mapComment(comment, deep, false));
            }
        }
        return commentDtos;
    }

    @Transactional
    public Set<CommentDTO> mapDeepComments(final Set<Comment> comments) {
        return mapComments(comments, true);
    }

    @Transactional
    public CommentDTO mapDeepCommentWithCommentRound(final Comment comment) {
        return mapComment(comment, true, true);
    }

    @Transactional
    public CommentDTO mapDeepComment(final Comment comment) {
        return mapComment(comment, true, false);
    }

    @Transactional
    public CommentDTO mapComment(final Comment comment,
                                 final boolean deep,
                                 final boolean mapDeepCommentThread) {
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
        commentDto.setUser(userService.getUserById(comment.getUserId()));
        commentDto.setProposedStatus(comment.getProposedStatus());
        commentDto.setEndStatus(comment.getEndStatus());
        commentDto.setUri(comment.getUri());
        commentDto.setSequenceId(comment.getSequenceId());
        if (deep) {
            commentDto.setParentComment(mapComment(comment.getParentComment(), false, false));
        }
        if (deep || mapDeepCommentThread) {
            commentDto.setCommentThread(mapCommentThread(comment.getCommentThread(), true));
        }
        return commentDto;
    }

    @Transactional
    public Set<CommentRoundDTO> mapDeepCommentRounds(final Set<CommentRound> commentRounds) {
        return mapCommentRounds(commentRounds, true);
    }

    @Transactional
    public Set<CommentRoundDTO> mapCommentRounds(final Set<CommentRound> commentRounds,
                                                 final boolean deep) {
        final Set<CommentRoundDTO> commentRoundDtos = new HashSet<>();
        if (commentRounds != null && !commentRounds.isEmpty()) {
            for (final CommentRound commentRound : commentRounds) {
                commentRoundDtos.add(mapCommentRound(commentRound, deep, false, false));
            }
        }
        return commentRoundDtos;
    }

    @Transactional
    public CommentRoundDTO mapDeepCommentRound(final CommentRound commentRound) {
        return mapCommentRound(commentRound, true, true, true);
    }

    @Transactional
    public CommentRoundDTO mapCommentRound(final CommentRound commentRound,
                                           final boolean deep,
                                           final boolean mapSource,
                                           final boolean mapOrganization) {
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
        commentRoundDto.setOpenThreads(commentRound.getOpenThreads());
        commentRoundDto.setFixedThreads(commentRound.getFixedThreads());
        commentRoundDto.setSourceLocalName(commentRound.getSourceLocalName());
        commentRoundDto.setSourceLabel(copyStringMap(commentRound.getSourceLabel()));
        commentRoundDto.setLabel(commentRound.getLabel());
        commentRoundDto.setUser(userService.getUserById(commentRound.getUserId()));
        commentRoundDto.setStatus(commentRound.getStatus());
        commentRoundDto.setUri(commentRound.getUri());
        commentRoundDto.setSequenceId(commentRound.getSequenceId());
        if (deep) {
            commentRoundDto.setCommentThreads(mapCommentThreads(commentRound.getCommentThreads(), false));
        }
        if (deep || mapSource) {
            commentRoundDto.setSource(mapSource(commentRound.getSource()));
        }
        if (deep || mapOrganization) {
            commentRoundDto.setOrganizations(mapOrganizations(commentRound.getOrganizations(), false));
        }
        return commentRoundDto;
    }

    @Transactional
    public Set<CommentThreadDTO> mapCommentThreads(final Set<CommentThread> commentThreads,
                                                   final boolean deep) {
        final Set<CommentThreadDTO> commentThreadDtos = new HashSet<>();
        if (commentThreads != null && !commentThreads.isEmpty()) {
            for (final CommentThread commentThread : commentThreads) {
                commentThreadDtos.add(mapCommentThread(commentThread, deep));
            }
        }
        return commentThreadDtos;
    }

    @Transactional
    public Set<CommentThreadDTO> mapDeepCommentThreads(final Set<CommentThread> commentThreads) {
        return mapCommentThreads(commentThreads, true);
    }

    @Transactional
    public CommentThreadDTO mapDeepCommentThread(final CommentThread commentThread) {
        return mapCommentThread(commentThread, true);
    }

    @Transactional
    public CommentThreadDTO mapCommentThread(final CommentThread commentThread,
                                             final boolean deep) {
        if (commentThread == null) {
            return null;
        }
        final CommentThreadDTO commentThreadDto = new CommentThreadDTO();
        final UUID id = commentThread.getId();
        commentThreadDto.setId(id);
        commentThreadDto.setUrl(urlGenerator.createResourceUrl(API_PATH_THREADS, id.toString()));
        commentThreadDto.setResourceUri(commentThread.getResourceUri());
        commentThreadDto.setCurrentStatus(commentThread.getCurrentStatus());
        commentThreadDto.setProposedStatus(commentThread.getProposedStatus());
        commentThreadDto.setProposedText(commentThread.getProposedText());
        commentThreadDto.setCreated(commentThread.getCreated());
        commentThreadDto.setLabel(copyStringMap(commentThread.getLabel()));
        commentThreadDto.setDescription(copyStringMap(commentThread.getDescription()));
        commentThreadDto.setLocalName(commentThread.getLocalName());
        commentThreadDto.setUser(userService.getUserById(commentThread.getUserId()));
        commentThreadDto.setResults(resultService.getResultsForCommentThread(commentThread.getId()));
        commentThreadDto.setUri(commentThread.getUri());
        commentThreadDto.setSequenceId(commentThread.getSequenceId());
        if (commentThread.getComments() != null) {
            commentThreadDto.setCommentCount(commentThread.getComments().size());
        }
        if (deep) {
            commentThreadDto.setCommentRound(mapCommentRound(commentThread.getCommentRound(), false, true, true));
            commentThreadDto.setComments(mapComments(commentThread.getComments(), false));
        }
        return commentThreadDto;
    }

    @Transactional
    public Set<SourceDTO> mapSources(final Set<Source> sources) {
        final Set<SourceDTO> sourceDtos = new HashSet<>();
        if (sources != null && !sources.isEmpty()) {
            for (final Source source : sources) {
                sourceDtos.add(mapSource(source));
            }
        }
        return sourceDtos;
    }

    @Transactional
    public SourceDTO mapSource(final Source source) {
        if (source == null) {
            return null;
        }
        final SourceDTO sourceDto = new SourceDTO();
        final UUID id = source.getId();
        sourceDto.setId(id);
        sourceDto.setUrl(urlGenerator.createResourceUrl(API_PATH_SOURCES, id.toString()));
        sourceDto.setContainerType(source.getContainerType());
        sourceDto.setContainerUri(source.getContainerUri());
        return sourceDto;
    }

    @Transactional
    public OrganizationDTO mapOrganization(final Organization organization,
                                           final boolean deep) {
        final OrganizationDTO organizationDto = new OrganizationDTO();
        organizationDto.setId(organization.getId());
        organizationDto.setRemoved(organization.getRemoved());
        organizationDto.setUrl(organization.getUrl());
        organizationDto.setDescription(copyStringMap(organization.getDescription()));
        organizationDto.setPrefLabel(copyStringMap(organization.getPrefLabel()));
        if (deep && organization.getCommentRounds() != null) {
            organizationDto.setCommentRounds(mapCommentRounds(organization.getCommentRounds(), false));
        }
        return organizationDto;
    }

    @Transactional
    public Set<OrganizationDTO> mapOrganizations(final Set<Organization> organizations,
                                                 final boolean deep) {
        final Set<OrganizationDTO> organizationDtos = new HashSet<>();
        if (organizations != null && !organizations.isEmpty()) {
            organizations.forEach(organization -> organizationDtos.add(mapOrganization(organization, deep)));
        }
        return organizationDtos;
    }

    @Transactional
    public Set<ResourceDTO> mapCommentRoundsToResources(final Set<CommentRound> commentRounds) {
        final Set<ResourceDTO> resourceDtos = new HashSet<>();
        commentRounds.forEach(resource -> resourceDtos.add(mapCommentRoundToResource(resource)));
        return resourceDtos;
    }

    @Transactional
    public ResourceDTO mapCommentRoundToResource(final CommentRound commentRound) {
        final ResourceDTO resourceDto = new ResourceDTO();
        resourceDto.setPrefLabel(createUndLocalizable(commentRound.getLabel()));
        resourceDto.setDescription(createUndLocalizable(commentRound.getDescription()));
        resourceDto.setUri(commentRound.getUri());
        resourceDto.setLocalName(commentRound.getSequenceId().toString());
        resourceDto.setStatus(commentRound.getStatus());
        resourceDto.setModified(convertLocalDateTimeToDate(commentRound.getModified()));
        resourceDto.setStatusModified(convertLocalDateTimeToDate(commentRound.getStatusModified()));
        resourceDto.setContentModified(convertLocalDateTimeToDate(commentRound.getContentModified()));
        resourceDto.setType("commentround");
        return resourceDto;
    }

    @Transactional
    public Set<ResourceDTO> mapCommentThreadsToResources(final Set<CommentThread> commentThreads) {
        final Set<ResourceDTO> resourceDtos = new HashSet<>();
        commentThreads.forEach(resource -> resourceDtos.add(mapCommentThreadToResource(resource)));
        return resourceDtos;
    }

    @Transactional
    public ResourceDTO mapCommentThreadToResource(final CommentThread commentThread) {
        final ResourceDTO resourceDto = new ResourceDTO();
        resourceDto.setPrefLabel(copyStringMap(commentThread.getLabel()));
        resourceDto.setDescription(copyStringMap(commentThread.getDescription()));
        resourceDto.setUri(commentThread.getUri());
        resourceDto.setLocalName(commentThread.getSequenceId().toString());
        resourceDto.setModified(convertLocalDateTimeToDate(commentThread.getCreated()));
        resourceDto.setContentModified(convertLocalDateTimeToDate(commentThread.getCommentsModified()));
        resourceDto.setType("commentthread");
        return resourceDto;
    }

    private Map<String, String> createUndLocalizable(final String text) {
        if (text != null && !text.isEmpty()) {
            final Map<String, String> localizable = new HashMap<>();
            localizable.put("und", text);
            return localizable;
        } else {
            return null;
        }
    }

    private Map copyStringMap(final Map<String, String> map) {
        if (map != null && map.size() > 0) {
            return new HashMap<>(map);
        }
        return null;
    }
}

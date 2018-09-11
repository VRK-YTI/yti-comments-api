package fi.vm.yti.comments.api.dao.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dao.CommentRoundGroupDao;
import fi.vm.yti.comments.api.dto.CommentRoundGroupDTO;
import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentRoundGroup;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.jpa.CommentRoundGroupRepository;

@Component
public class CommentRoundGroupDaoImpl implements CommentRoundGroupDao {

    private final CommentRoundGroupRepository commentRoundGroupRepository;
    private final CommentRoundDao commentRoundDao;

    @Inject
    public CommentRoundGroupDaoImpl(final CommentRoundGroupRepository commentRoundGroupRepository,
                                    final DtoMapper dtoMapper,
                                    final CommentRoundDao sourceDao) {
        this.commentRoundGroupRepository = commentRoundGroupRepository;
        this.commentRoundDao = sourceDao;
    }

    public Set<CommentRoundGroup> findAll() {
        return commentRoundGroupRepository.findAll();
    }

    public CommentRoundGroup findById(final UUID commentRoundGroupId) {
        return commentRoundGroupRepository.findById(commentRoundGroupId);
    }

    public CommentRoundGroup addOrUpdateCommentRoundGroupFromDto(final CommentRoundGroupDTO fromCommentRoundGroup) {
        final CommentRoundGroup commentRoundGroup = createOrUpdateCommentRoundGroup(fromCommentRoundGroup);
        commentRoundGroupRepository.save(commentRoundGroup);
        return commentRoundGroup;
    }

    public Set<CommentRoundGroup> addOrUpdateCommentRoundGroupsFromDtos(final Set<CommentRoundGroupDTO> fromCommentRoundGroups) {
        final Set<CommentRoundGroup> commentRoundGroups = new HashSet<>();
        for (final CommentRoundGroupDTO fromCommentRoundGroup : fromCommentRoundGroups) {
            commentRoundGroups.add(createOrUpdateCommentRoundGroup(fromCommentRoundGroup));
        }
        commentRoundGroupRepository.saveAll(commentRoundGroups);
        return commentRoundGroups;
    }

    private CommentRoundGroup createOrUpdateCommentRoundGroup(final CommentRoundGroupDTO fromCommentRoundGroup) {
        final CommentRoundGroup existingCommentRoundGroup;
        if (fromCommentRoundGroup.getId() != null) {
            existingCommentRoundGroup = commentRoundGroupRepository.findById(fromCommentRoundGroup.getId());
        } else {
            existingCommentRoundGroup = null;
        }
        final CommentRoundGroup commentRoundGroup;
        if (existingCommentRoundGroup != null) {
            commentRoundGroup = updateCommentRoundGroup(existingCommentRoundGroup, fromCommentRoundGroup);
        } else {
            commentRoundGroup = createCommentRoundGroup(fromCommentRoundGroup);
        }
        return commentRoundGroup;

    }

    private CommentRoundGroup createCommentRoundGroup(final CommentRoundGroupDTO fromCommentRoundGroup) {
        final CommentRoundGroup commentRoundGroup = new CommentRoundGroup();
        commentRoundGroup.setId(UUID.randomUUID());
        resolveCommentRound(commentRoundGroup, fromCommentRoundGroup);
        return commentRoundGroup;
    }

    private CommentRoundGroup updateCommentRoundGroup(final CommentRoundGroup existingCommentRoundGroup,
                                                      final CommentRoundGroupDTO fromCommentRoundGroup) {
        resolveCommentRound(existingCommentRoundGroup, fromCommentRoundGroup);
        return existingCommentRoundGroup;
    }

    private void resolveCommentRound(final CommentRoundGroup commentRoundGroup,
                                     final CommentRoundGroupDTO fromCommentRoundGroup) {
        if (fromCommentRoundGroup.getCommentRound() != null && fromCommentRoundGroup.getCommentRound().getId() != null) {
            final CommentRound commentRound = commentRoundDao.findById(fromCommentRoundGroup.getCommentRound().getId());
            if (commentRound != null) {
                commentRoundGroup.setCommentRound(commentRound);
            }
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid commentRound referenced in DTO data."));
        }
    }
}

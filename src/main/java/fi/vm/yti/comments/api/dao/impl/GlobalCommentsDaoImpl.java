package fi.vm.yti.comments.api.dao.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.GlobalCommentsDao;
import fi.vm.yti.comments.api.dao.SourceDao;
import fi.vm.yti.comments.api.dto.GlobalCommentsDTO;
import fi.vm.yti.comments.api.entity.GlobalComments;
import fi.vm.yti.comments.api.entity.Source;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.jpa.GlobalCommentsRepository;

@Component
public class GlobalCommentsDaoImpl implements GlobalCommentsDao {

    private final GlobalCommentsRepository globalCommentsRepository;
    private final SourceDao sourceDao;

    @Inject
    public GlobalCommentsDaoImpl(final GlobalCommentsRepository globalCommentsRepository,
                                 final SourceDao sourceDao) {
        this.globalCommentsRepository = globalCommentsRepository;
        this.sourceDao = sourceDao;
    }

    public Set<GlobalComments> findAll() {
        return globalCommentsRepository.findAll();
    }

    public GlobalComments findById(final UUID globalCommentsId) {
        return globalCommentsRepository.findById(globalCommentsId);
    }

    public GlobalComments addOrUpdateGlobalCommentsFromDto(final GlobalCommentsDTO fromGlobalComments) {
        final GlobalComments globalComments = createOrUpdateGlobalComments(fromGlobalComments);
        globalCommentsRepository.save(globalComments);
        return globalComments;
    }

    public Set<GlobalComments> addOrUpdateGlobalCommentsFromDtos(final Set<GlobalCommentsDTO> fromGlobalComments) {
        final Set<GlobalComments> globalComments = new HashSet<>();
        for (final GlobalCommentsDTO fromGlobalCommentsDto : fromGlobalComments) {
            globalComments.add(createOrUpdateGlobalComments(fromGlobalCommentsDto));
        }
        globalCommentsRepository.saveAll(globalComments);
        return globalComments;
    }

    private GlobalComments createOrUpdateGlobalComments(final GlobalCommentsDTO fromGlobalComments) {
        final GlobalComments existingGlobalComments;
        if (fromGlobalComments.getId() != null) {
            existingGlobalComments = globalCommentsRepository.findById(fromGlobalComments.getId());
        } else {
            existingGlobalComments = null;
        }
        final GlobalComments globalComments;
        if (existingGlobalComments != null) {
            globalComments = updateGlobalComments(existingGlobalComments, fromGlobalComments);
        } else {
            globalComments = createGlobalComments(fromGlobalComments);
        }
        return globalComments;
    }

    private GlobalComments createGlobalComments(final GlobalCommentsDTO fromGlobalComments) {
        final GlobalComments globalComments = new GlobalComments();
        globalComments.setId(UUID.randomUUID());
        globalComments.setCreated(LocalDateTime.now());
        resolveSource(globalComments, fromGlobalComments);
        return globalComments;
    }

    private GlobalComments updateGlobalComments(final GlobalComments existingGlobalComments,
                                                final GlobalCommentsDTO fromGlobalComments) {
        resolveSource(existingGlobalComments, fromGlobalComments);
        return existingGlobalComments;
    }

    private void resolveSource(final GlobalComments globalComments,
                               final GlobalCommentsDTO fromGlobalComments) {
        if (fromGlobalComments.getSource() != null && fromGlobalComments.getSource().getId() != null) {
            final Source source = sourceDao.findById(fromGlobalComments.getSource().getId());
            if (source != null) {
                globalComments.setSource(source);
            }
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid globalComments referenced in DTO data."));
        }
    }
}

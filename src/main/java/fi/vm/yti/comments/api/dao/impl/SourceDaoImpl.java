package fi.vm.yti.comments.api.dao.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.SourceDao;
import fi.vm.yti.comments.api.dto.SourceDTO;
import fi.vm.yti.comments.api.entity.Source;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.jpa.SourceRepository;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_INVALID_SOURCE_DATA;

@Component
public class SourceDaoImpl implements SourceDao {

    private final SourceRepository sourceRepository;

    @Inject
    public SourceDaoImpl(final SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    public Set<Source> findAll() {
        return sourceRepository.findAll();
    }

    public Source findById(final UUID sourceId) {
        return sourceRepository.findById(sourceId);
    }

    @Transactional
    public Source addOrUpdateSourceFromDto(final SourceDTO fromSource) {
        final Source source = createOrUpdateSource(fromSource);
        sourceRepository.save(source);
        return source;
    }

    @Transactional
    public Set<Source> addOrUpdateSourcesFromDtos(final Set<SourceDTO> fromSources) {
        final Set<Source> sources = new HashSet<>();
        for (final SourceDTO fromSource : fromSources) {
            sources.add(createOrUpdateSource(fromSource));
        }
        sourceRepository.saveAll(sources);
        return sources;
    }

    @Transactional
    public Source getOrCreateByDto(final SourceDTO fromSource) {
        if (fromSource.getContainerUri() != null) {
            final Source existingSource = sourceRepository.findByContainerUri(fromSource.getContainerUri());
            if (existingSource != null) {
                return existingSource;
            } else {
                return createSource(fromSource);
            }
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_INVALID_SOURCE_DATA));
        }
    }

    private Source createOrUpdateSource(final SourceDTO fromSource) {
        final Source existingSource;
        if (fromSource.getId() != null) {
            existingSource = sourceRepository.findById(fromSource.getId());
        } else {
            existingSource = null;
        }
        final Source source;
        if (existingSource != null) {
            source = updateSource(existingSource, fromSource);
        } else {
            source = createSource(fromSource);
        }
        return source;

    }

    private Source createSource(final SourceDTO fromSource) {
        final Source source = new Source();
        source.setId(UUID.randomUUID());
        source.setContainerType(fromSource.getContainerType());
        source.setContainerUri(fromSource.getContainerUri());
        return source;
    }

    private Source updateSource(final Source existingSource,
                                final SourceDTO fromSource) {
        existingSource.setContainerType(fromSource.getContainerType());
        existingSource.setContainerUri(fromSource.getContainerUri());
        return existingSource;
    }
}

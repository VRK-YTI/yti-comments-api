package fi.vm.yti.comments.api.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.poi.ss.usermodel.Workbook;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.EndpointConfigBase;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterModifier;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;

public interface AbstractBaseResource {

    default SimpleFilterProvider createSimpleFilterProviderWithSingleFilter(final String baseFilter,
                                                                            final String expand) {
        final List<String> baseFilters = new ArrayList<>();
        baseFilters.add(baseFilter);
        return createSimpleFilterProvider(baseFilters, expand);
    }

    default SimpleFilterProvider createSimpleFilterProvider(final List<String> baseFilters,
                                                            final String expand) {
        final SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(FILTER_NAME_COMMENT, SimpleBeanPropertyFilter.filterOutAllExcept(FIELD_NAME_ID));
        filterProvider.addFilter(FILTER_NAME_SOURCE, SimpleBeanPropertyFilter.filterOutAllExcept(FIELD_NAME_ID));
        filterProvider.addFilter(FILTER_NAME_COMMENTROUND, SimpleBeanPropertyFilter.filterOutAllExcept(FIELD_NAME_ID));
        filterProvider.addFilter(FILTER_NAME_COMMENTTHREAD, SimpleBeanPropertyFilter.filterOutAllExcept(FIELD_NAME_ID));
        filterProvider.addFilter(FILTER_NAME_ORGANIZATION, SimpleBeanPropertyFilter.filterOutAllExcept(FIELD_NAME_ID));
        filterProvider.addFilter(FILTER_NAME_COMMENTROUNDORGANIZATION, SimpleBeanPropertyFilter.filterOutAllExcept(FIELD_NAME_ID));
        filterProvider.setFailOnUnknownId(false);
        if (baseFilters != null) {
            for (final String baseFilter : baseFilters) {
                filterProvider.removeFilter(baseFilter);
            }
        }
        if (expand != null && !expand.isEmpty()) {
            final String[] filterOptions = expand.split(",");
            for (final String filter : filterOptions) {
                filterProvider.removeFilter(filter);
            }
        }
        return filterProvider;
    }

    default Response streamExcelOutput(final Workbook workbook,
                                       final String filename) {
        final StreamingOutput stream = output -> {
            try {
                workbook.write(output);
            } catch (final Exception e) {
                throw new YtiCommentsException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Excel output generation failed!"));
            }
        };
        return Response.ok(stream).header(HEADER_CONTENT_DISPOSITION, "attachment; filename = " + filename).build();
    }

    class FilterModifier extends ObjectWriterModifier {

        private final FilterProvider provider;

        FilterModifier(final FilterProvider provider) {
            this.provider = provider;
        }

        @Override
        public ObjectWriter modify(final EndpointConfigBase<?> endpoint,
                                   final MultivaluedMap<String, Object> responseHeaders,
                                   final Object valueToWrite,
                                   final ObjectWriter w,
                                   final JsonGenerator g) {
            return w.with(provider);
        }
    }
}

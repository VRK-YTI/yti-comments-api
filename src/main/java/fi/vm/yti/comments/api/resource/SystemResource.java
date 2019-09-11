package fi.vm.yti.comments.api.resource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dto.SystemMetaCountDTO;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.jpa.CommentRepository;
import fi.vm.yti.comments.api.jpa.CommentRoundRepository;
import fi.vm.yti.comments.api.jpa.CommentThreadRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;

@Component
@Path("/v1/system")
@Api(value = "system")
@Produces({ "text/plain;charset=utf-8", MediaType.APPLICATION_JSON + ";charset=UTF-8" })
public class SystemResource implements AbstractBaseResource {

    private static final Logger LOG = LoggerFactory.getLogger(SystemResource.class);

    private final CommentRoundRepository commentRoundRepository;
    private final CommentThreadRepository commentThreadRepository;
    private final CommentRepository commentRepository;

    public SystemResource(final CommentRoundRepository commentRoundRepository,
                          final CommentThreadRepository commentThreadRepository,
                          final CommentRepository commentRepository) {
        this.commentRoundRepository = commentRoundRepository;
        this.commentThreadRepository = commentThreadRepository;
        this.commentRepository = commentRepository;
    }

    @GET
    @Path("counts")
    @ApiOperation(value = "Get entity count meta information from the system", response = String.class)
    @ApiResponse(code = 200, message = "Returns the meta information of entity counts from the system in given format (json / text).")
    @Produces({ "text/plain;charset=utf-8", MediaType.APPLICATION_JSON + ";charset=UTF-8" })
    public Response getSystemCountMetaInformation(@ApiParam(value = "Date after which resources have been modified.") @QueryParam("modifiedAfter") final String modifiedAfter,
                                                  @ApiParam(value = "Date after which resources have been created.") @QueryParam("createdAfter") final String createdAfter,
                                                  @ApiParam(value = "Format of output. Supports json and text, defaults to json.") @QueryParam("format") @DefaultValue("json") final String format) {
        final SystemMetaCountDTO countMeta;
        if (modifiedAfter != null) {
            final Date modifiedAfterDate = parseDateFromString(modifiedAfter);
            countMeta = new SystemMetaCountDTO(commentRoundRepository.modifiedAfterCount(modifiedAfterDate), null, null);
            return createCountMetaResponse(countMeta, format);
        } else if (createdAfter != null) {
            final Date createdAfterDate = parseDateFromString(createdAfter);
            countMeta = new SystemMetaCountDTO(commentRoundRepository.createdAfterCount(createdAfterDate), commentThreadRepository.createdAfterCount(createdAfterDate), commentRepository.createdAfterCount(createdAfterDate));
            return createCountMetaResponse(countMeta, format);
        } else {
            countMeta = new SystemMetaCountDTO(commentRoundRepository.count(), commentThreadRepository.count(), commentRepository.count());
            return createCountMetaResponse(countMeta, format);
        }
    }

    private Date parseDateFromString(final String dateString) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(dateString);
        } catch (final ParseException e) {
            LOG.error("Error parsing date from string in meta api: " + dateString);
        }
        return null;
    }

    private Response createCountMetaResponse(final SystemMetaCountDTO countMeta,
                                             final String format) {
        if (format.startsWith("json") || format.startsWith(MediaType.APPLICATION_JSON)) {
            return Response.ok(countMeta).build();
        } else if (format.startsWith("text")) {
            return Response.ok(createResponseStringMessage(countMeta)).build();
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Format not supported for the system meta counts API: " + format));
        }
    }

    private String createResponseStringMessage(final SystemMetaCountDTO systemMetaCount) {
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("CommentRounds: " + systemMetaCount.getCommentRoundCount());
        stringBuffer.append("\n");
        if (systemMetaCount.getCommentThreadCount() != null) {
            stringBuffer.append("CommentThreads: " + systemMetaCount.getCommentThreadCount());
            stringBuffer.append("\n");
        }
        if (systemMetaCount.getCommentCount() != null) {
            stringBuffer.append("Comments: " + systemMetaCount.getCommentCount());
        }
        return stringBuffer.toString();
    }
}

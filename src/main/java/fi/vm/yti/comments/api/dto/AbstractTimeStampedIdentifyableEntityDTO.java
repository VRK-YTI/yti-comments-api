package fi.vm.yti.comments.api.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModelProperty;

class AbstractTimeStampedIdentifyableEntityDTO extends AbstractIdentifyableEntityDTO {

    private LocalDateTime created;
    private LocalDateTime modified;

    @ApiModelProperty(dataType = "dateTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    @ApiModelProperty(dataType = "dateTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(final LocalDateTime modified) {
        this.modified = modified;
    }
}

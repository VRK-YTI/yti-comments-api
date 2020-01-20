package fi.vm.yti.comments.api.dto;

import java.io.Serializable;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonFilter("user")
@XmlRootElement
@XmlType(propOrder = { "id", "email", "firstName", "lastName", "tokenRole", "containerUri" })
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "User", description = "User DTO for a single Groupmanagement User.")
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String tokenRole;
    private String containerUri;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    public String getTokenRole() {
        return tokenRole;
    }

    public void setTokenRole(final String tokenRole) {
        this.tokenRole = tokenRole;
    }

    public String getContainerUri() {
        return containerUri;
    }

    public void setContainerUri(final String containerUri) {
        this.containerUri = containerUri;
    }
}

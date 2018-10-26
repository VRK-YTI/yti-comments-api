package fi.vm.yti.comments.api.dto;

import java.io.Serializable;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;

@JsonFilter("user")
@XmlRootElement
@XmlType(propOrder = { "id", "email", "firstName", "lastName" })
@JsonIgnoreProperties({ "displayName" })
@ApiModel(value = "User", description = "User DTO for a single groupmanagement user.")
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;

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

    public String getDisplayNameWithEmail() {
        return firstName + " " + lastName + " (" + email + ")";
    }
}

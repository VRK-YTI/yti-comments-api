package fi.vm.yti.comments.api.resource;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.groupmanagement.GroupManagementUser;
import fi.vm.yti.comments.api.groupmanagement.ImpersonateUserService;
import io.swagger.annotations.Api;

@Component
@Path("/fakeableUsers")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "fakeableUsers")
public class ImpersonateUserResource implements AbstractBaseResource {

    private final ImpersonateUserService impersonateUserService;

    @Inject
    public ImpersonateUserResource(ImpersonateUserService impersonateUserService) {
        super();
        this.impersonateUserService = impersonateUserService;
    }

    @GET
    public List<GroupManagementUser> isLoginFakeable() {
        return impersonateUserService.getUsers();
    }
}
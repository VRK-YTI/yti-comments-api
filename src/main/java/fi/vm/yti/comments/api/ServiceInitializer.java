package fi.vm.yti.comments.api;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.configuration.VersionInformation;
import fi.vm.yti.comments.api.groupmanagement.OrganizationUpdater;
import fi.vm.yti.comments.api.service.UserService;
import fi.vm.yti.comments.api.service.impl.UserServiceImpl;

@Component
public class ServiceInitializer implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceInitializer.class);
    private final VersionInformation versionInformation;
    private final OrganizationUpdater organizationUpdater;
    private final UserService userService;

    @Inject
    public ServiceInitializer(final VersionInformation versionInformation,
                              final OrganizationUpdater organizationUpdater,
                              final UserServiceImpl userService) {
        this.versionInformation = versionInformation;
        this.organizationUpdater = organizationUpdater;
        this.userService = userService;
    }

    @Override
    public void run(final ApplicationArguments args) {
        initialize();
    }

    public void initialize() {
        printLogo();
        LOG.info("*** Updating organizations. ***");
        organizationUpdater.updateOrganizations();
        LOG.info("*** Updating users. ***");
        userService.updateUsers();
    }

    public void printLogo() {
        LOG.info("");
        LOG.info("          __  .__                                              __          ");
        LOG.info(" ___.__._/  |_|__|   ____  ____   _____   _____   ____   _____/  |_  ______");
        LOG.info("<   |  |\\   __\\  | _/ ___\\/  _ \\ /     \\ /     \\_/ __ \\ /    \\   __\\/  ___/");
        LOG.info(" \\___  | |  | |  | \\  \\__(  <_> )  Y Y  \\  Y Y  \\  ___/|   |  \\  |  \\___ \\ ");
        LOG.info(" / ____| |__| |__|  \\___  >____/|__|_|  /__|_|  /\\___  >___|  /__| /____  >");
        LOG.info(" \\/                     \\/            \\/      \\/     \\/     \\/          \\/ ");
        LOG.info("              .__ ");
        LOG.info("_____  ______ |__|");
        LOG.info("\\__  \\ \\____ \\|  |");
        LOG.info(" / __ \\|  |_> >  |");
        LOG.info("(____  /   __/|__|");
        LOG.info("     \\/|__|       ");
        LOG.info("");
        LOG.info(getVersionString());
        LOG.info("");
    }

    private String getVersionString() {
        return String.format("                --- Version %s starting up. --- ", versionInformation.getVersion());
    }
}

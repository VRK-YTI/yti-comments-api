package fi.vm.yti.comments.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fi.vm.yti.comments.api.configuration.CommentsApiConfiguration;
import fi.vm.yti.comments.api.configuration.VersionInformation;
import fi.vm.yti.comments.api.groupmanagement.OrganizationUpdater;
import fi.vm.yti.comments.api.service.UserService;
import fi.vm.yti.comments.api.service.impl.UserServiceImpl;
import fi.vm.yti.comments.api.utils.FileUtils;

@Component
public class ServiceInitializer implements ApplicationRunner {

    public static final String LOCAL_SWAGGER_DATA_DIR = "/data/yti/yti-comments-api/swagger/";

    private static final Logger LOG = LoggerFactory.getLogger(ServiceInitializer.class);
    private final CommentsApiConfiguration commentsApiProperties;
    private final VersionInformation versionInformation;
    private final OrganizationUpdater organizationUpdater;
    private final UserService userService;

    @Inject
    public ServiceInitializer(final VersionInformation versionInformation,
                              final CommentsApiConfiguration publicApiServiceProperties,
                              final OrganizationUpdater organizationUpdater,
                              final UserServiceImpl userService) {
        this.versionInformation = versionInformation;
        this.commentsApiProperties = publicApiServiceProperties;
        this.organizationUpdater = organizationUpdater;
        this.userService = userService;
    }

    @Override
    public void run(final ApplicationArguments args) {
        initialize();
    }

    public void initialize() {
        printLogo();
        printTimeZone();
        updateSwaggerHost();
        LOG.info("*** Updating organizations. ***");
        organizationUpdater.updateOrganizations();
        LOG.info("*** Updating users. ***");
        userService.updateUsers();
    }

    private void printTimeZone() {
        // TODO: Remove this debug used for testing purposes.
        LOG.info("Timezone information for Europe/Helsinki: " + TimeZone.getTimeZone("Europe/Helsinki"));
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

    @SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
    private void updateSwaggerHost() {
        final ObjectMapper mapper = new ObjectMapper();
        try (final InputStream inputStream = FileUtils.loadFileFromClassPath("/swagger/swagger.json")) {
            final ObjectNode jsonObject = (ObjectNode) mapper.readTree(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String hostname = commentsApiProperties.getCommentsHostName();
            jsonObject.put("host", hostname);
            final String scheme = commentsApiProperties.getScheme();
            final List<String> schemes = new ArrayList<>();
            schemes.add(scheme);
            final ArrayNode schemeArray = mapper.valueToTree(schemes);
            jsonObject.putArray("schemes").addAll(schemeArray);
            final File file = new File(LOCAL_SWAGGER_DATA_DIR + "swagger.json");
            Files.createDirectories(Paths.get(file.getParentFile().getPath()));
            final String fileLocation = file.toString();
            final String swaggerLogInfo = String.format("Storing modified swagger.json description with hostname: %s to: %s", hostname, fileLocation);
            LOG.info(swaggerLogInfo);
            try (FileOutputStream fos = new FileOutputStream(fileLocation, false)) {
                mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                fos.write(mapper.writeValueAsString(jsonObject).getBytes(StandardCharsets.UTF_8));
            }
        } catch (final IOException e) {
            LOG.error("Swagger JSON parsing failed: ", e);
        }
    }

    private String getVersionString() {
        return String.format("                --- Version %s starting up. --- ", versionInformation.getVersion());
    }
}

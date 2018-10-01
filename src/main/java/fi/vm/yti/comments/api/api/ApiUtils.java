package fi.vm.yti.comments.api.api;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.configuration.CodelistProperties;
import fi.vm.yti.comments.api.configuration.CommentsApiConfiguration;
import fi.vm.yti.comments.api.configuration.DatamodelProperties;
import fi.vm.yti.comments.api.configuration.GroupManagementProperties;
import fi.vm.yti.comments.api.configuration.TerminologyProperties;

@Component
public class ApiUtils {

    private final CommentsApiConfiguration commentsApiConfiguration;
    private final GroupManagementProperties groupManagementProperties;
    private final TerminologyProperties terminologyProperties;
    private final DatamodelProperties dataModelProperties;
    private final CodelistProperties codelistProperties;

    @Inject
    public ApiUtils(final CommentsApiConfiguration commentsApiConfiguration,
                    final GroupManagementProperties groupManagementProperties,
                    final TerminologyProperties terminologyProperties,
                    final DatamodelProperties dataModelProperties,
                    final CodelistProperties codelistProperties) {
        this.commentsApiConfiguration = commentsApiConfiguration;
        this.groupManagementProperties = groupManagementProperties;
        this.terminologyProperties = terminologyProperties;
        this.dataModelProperties = dataModelProperties;
        this.codelistProperties = codelistProperties;
    }

    public String getEnv() {
        return commentsApiConfiguration.getEnv();
    }

    public String getGroupmanagementPublicUrl() {
        return groupManagementProperties.getPublicUrl();
    }

    public String getTerminologyPublicUrl() {
        return terminologyProperties.getPublicUrl();
    }

    public String getDataModelPublicUrl() {
        return dataModelProperties.getPublicUrl();
    }

    public String getCodelistPublicUrl() {
        return codelistProperties.getPublicUrl();
    }
}
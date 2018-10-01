package fi.vm.yti.comments.api.constants;

public abstract class ApiConstants {

    private ApiConstants() {
    }

    public static final String API_VERSION = "v1";
    public static final String API_CONTEXT_PATH = "comments-api";
    public static final String API_BASE_PATH = "api";

    public static final String API_PATH_COMMENTS = "comments";
    public static final String API_PATH_SOURCES = "sources";
    public static final String API_PATH_THREADS = "threads";
    public static final String API_PATH_COMMENTROUNDS = "commentrounds";

    public static final String CODELIST_API_CONTEXT_PATH = "codelist-api";
    public static final String CODELIST_API_PATH = "api";
    public static final String CODELIST_API_VERSION = "v1";

    public static final String API_INTEGRATION = "integration";
    public static final String API_CONTAINERS = "containers";
    public static final String API_RESOURCES = "resources";

    public static final String GROUPMANAGEMENT_API_CONTEXT_PATH = "public-api";
    public static final String GROUPMANAGEMENT_API_USERS = "users";
    public static final String GROUPMANAGEMENT_API_REQUEST = "request";
    public static final String GROUPMANAGEMENT_API_REQUESTS = "requests";
    public static final String GROUPMANAGEMENT_API_ORGANIZATIONS = "organizations";

    public static final String FILTER_NAME_COMMENT = "comment";
    public static final String FILTER_NAME_SOURCE = "source";
    public static final String FILTER_NAME_COMMENTROUND = "commentRound";
    public static final String FILTER_NAME_COMMENTTHREAD = "commentThread";
    public static final String FILTER_NAME_ORGANIZATION = "organization";
    public static final String FILTER_NAME_COMMENTROUNDORGANIZATION = "commentRoundGroup";

    public static final String FIELD_NAME_ID = "id";

    public static final String LANGUAGE_CODE_EN = "en";

}

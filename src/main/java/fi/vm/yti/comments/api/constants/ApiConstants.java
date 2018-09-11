package fi.vm.yti.comments.api.constants;

public abstract class ApiConstants {

    private ApiConstants() {
    }

    public static final String API_VERSION = "v1";
    public static final String API_CONTEXT_PATH = "comments-api";
    public static final String API_BASE_PATH = "api";

    public static final String API_PATH_COMMENTS = "comments";
    public static final String API_PATH_SOURCES = "sources";
    public static final String API_PATH_COMMENTROUNDS = "commentrounds";
    public static final String API_PATH_COMMENTROUNDGROUPS = "commentroundgroups";
    public static final String API_PATH_GLOBALCOMMENTS = "globalcomments";

    public static final String FILTER_NAME_COMMENT = "comment";
    public static final String FILTER_NAME_SOURCE = "source";
    public static final String FILTER_NAME_COMMENTROUND = "commentRound";
    public static final String FILTER_NAME_COMMENTROUNDGROUP = "commentRoundGroup";
    public static final String FILTER_NAME_GLOBALCOMMENTS = "globalComments";

    public static final String FIELD_NAME_ID = "id";

}

package fi.vm.yti.comments.api.constants;

public abstract class ApiConstants {

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
    public static final String API_REST = "rest";
    public static final String API_CONTAINERS = "containers";
    public static final String API_RESOURCES = "resources";
    public static final String GROUPMANAGEMENT_API_CONTEXT_PATH = "public-api";
    public static final String GROUPMANAGEMENT_API_PRIVATE_CONTEXT_PATH = "private-api";
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
    public static final String FORMAT_JSON = "json";
    public static final String FORMAT_EXCEL = "excel";
    public static final String STATUS_INCOMPLETE = "INCOMPLETE";
    public static final String STATUS_AWAIT = "AWAIT";
    public static final String STATUS_INPROGRESS = "INPROGRESS";
    public static final String STATUS_ENDED = "ENDED";
    public static final String EXCEL_SHEET_COMMENTROUND = "CommentRound";
    public static final String EXCEL_SHEET_COMMENTTHREADS = "CommentThreads";
    public static final String EXCEL_SHEET_COMMENTS = "Comments";
    public static final String HEADER_CONTENT_DISPOSITION = "content-disposition";
    public static final String CONTENT_HEADER_URI = "URI";
    public static final String CONTENT_HEADER_SOURCE = "SOURCE";
    public static final String CONTENT_HEADER_ID = "ID";
    public static final String CONTENT_HEADER_COMMENTTHREAD_ID = "COMMENTTHREADID";
    public static final String CONTENT_HEADER_USER_ID = "USER_ID";
    public static final String CONTENT_HEADER_USER = "USER";
    public static final String CONTENT_HEADER_PARENTCOMMENT = "PARENTCOMMENT";
    public static final String CONTENT_HEADER_CONTENT = "CONTENT";
    public static final String CONTENT_HEADER_LABEL = "LABEL";
    public static final String CONTENT_HEADER_DESCRIPTION = "DESCRIPTION";
    public static final String CONTENT_HEADER_CREATED = "CREATED";
    public static final String CONTENT_HEADER_MODIFIED = "MODIFIED";
    public static final String CONTENT_HEADER_STARTDATE = "STARTDATE";
    public static final String CONTENT_HEADER_ENDDATE = "ENDDATE";
    public static final String CONTENT_HEADER_LABEL_PREFIX = "LABEL_";
    public static final String CONTENT_HEADER_DESCRIPTION_PREFIX = "DESCRIPTION_";
    public static final String CONTENT_HEADER_PROPOSEDSTATUS = "PROPOSEDSTATUS";
    public static final String CONTENT_HEADER_ENDSTATUS = "ENDSTATUS";
    public static final String CONTENT_HEADER_PROPOSEDTEXT = "PROPOSEDTEXT";
    public static final String CONTENT_HEADER_RESULT = "RESULT";
    public static final String CONTENT_HEADER_COMMENTS_SHEET = "COMMENTS_SHEET";

    private ApiConstants() {
    }
}

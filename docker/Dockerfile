# Pull base image
FROM yti-codelist-docker-java-base:alpine

# Deploy project
ADD target/yti-comments-api.jar ${deploy_dir}/yti-comments-api.jar

# If you want to serve static content (e.g. /yti-codelist-public-resource-service/swagger/index.html)
# from spring boot application that is running inside docker container you shall run into
# e.g. following problem:
#
# WARN  o.e.jetty.servlet.ServletHandler - /yti-codelist-public-resource-service/swagger/index.html
# java.io.FileNotFoundException: class path resource [static/swagger/index.html] cannot be resolved in the file system for resolving its last-modified timestamp
#
# Reason for this behaviour is explained here: https://jira.spring.io/browse/SPR-12862
# Workaround is described here: https://spring.io/guides/gs/spring-boot-docker/
#
# Enable following command to solve problem:
RUN bash -c 'touch ${deploy_dir}/yti-comments-api.jar'

# Expose port
EXPOSE 9701

# Set default command on run
ENTRYPOINT ["/bootstrap.sh", "yti-comments-api.jar"]

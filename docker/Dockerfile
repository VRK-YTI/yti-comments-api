# Pull base image
FROM yti-docker-java-base:alpine

# Deploy project
ADD target/yti-comments-api.jar ${deploy_dir}/yti-comments-api.jar

# Expose port
EXPOSE 9701

# Set default command on run
ENTRYPOINT ["/bootstrap.sh", "yti-comments-api.jar"]

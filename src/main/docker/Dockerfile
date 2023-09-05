FROM fabric8/java-centos-openjdk11-jdk:1.8.0

# Various Centos packages in the original image, including the JDK, have vulnerabilities.
# Running 'yum update' gets us the latest security patches for everything.
USER root
RUN yum update -y && yum clean all
USER jboss

ENV JAVA_APP_DIR=/deployments
EXPOSE 8080 8778 9779
COPY target/*.jar /deployments/

CMD ["/deployments/run-java.sh"]

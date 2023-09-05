# Azure Communication Service Demo on Spring Boot with AKS

---

## 1. Overview

This demo project showcases real-time communication features using Azure Communication Service, built with Spring Boot and deployed on Azure Kubernetes Service (AKS). Features include ***voice/video calls, chat, and SMS***.

## 2. Features

- **Voice Calls:** Utilize Azure Communication Service for high-quality voice calls.
- **Video Streams:** Real-time video streaming at high quality rate.
- **Chats:** Implement secure and scalable chat functionalities.
- **SMS Notifications:** Automated SMS alerts and notifications.

## 3. Prerequisites

- Azure Subscription
- Azure CLI installed and configured
- kubectl installed
- JDK 11 or higher
- Gradle 6.x or higher
- Docker - Download and install for [Mac Docker](https://docs.docker.com/docker-for-mac/install/) or [Windows Docker](https://docs.docker.com/docker-for-windows/install/)

## 4. Setup and Installation

The **<u>[Spring Boot AKS](https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/deploy-spring-boot-java-app-on-kubernetes)</u>** application is containerized using Docker and deployed on Azure Kubernetes Service (AKS) for scalable, high-availability communication services.

### 4.a. Azure and AKS Configuration

4.a.1. Login to Azure CLI

```bash
az login
```

4.a.2. Create AKS Cluster

```bash
az aks create --name MyAKSCluster --resource-group MyResourceGroup
```

### 4.b. Application Deployment

4.b.1. Clone the **repository**

```bash
git clone https://github.com/your-repo/azure-communication-demo.git
```

4.b.2. Navigate to the **project directory**

```bash
cd azure-communication-demo
```

4.b.3. Build the project with **Gradle**

```bash
./gradlew build

```

4.b.4. Build the **Docker** image

```bash
docker build -t azure-communication-demo .
```

4.b.5. Deploy to **Azure Kubernetes Service (AKS)**

```bash
kubectl apply -f deployment.yaml
```

### 4.c. Running the Demo

4.c.1. After deployment, you can access the service via the external IP provided by **AKS**.

```bash
kubectl get svc azure-communication-demo-service
```

4.c.2. Go to **URL** site on your browser.

```md
http://<EXTERNAL_HOST_IP>:<PORT> 
```

## 5. Architecture

### 5.a. SLF4J Mapped Diagnostic Context (MDC)

Please consider using the **SLF4J MDC** (mapped diagnostic context) whenever there is some common information that should be logged with every message.  To add data to the MDC, use:

```java
org.slf4j.MDC.put("sample-key", "sample value");
```

To make the **MDC** values appear in the generated logs, adjust `logback-spring.xml`. Add the following to the format statement for the **JSON** logs:

```json
"sample-key": "%mdc{sample-key}"
```

To see the generated MDC context in a **JSON** log message, enable the `cloud` profile.

### 5.b. Protected Endpoints

The intent is to use **JWT** tokens to authenticate all API calls.  This is implemented with the standard [spring-jwt-library](https://www.springcloud.io/post/2022-02/spring-security-new-jwt/#gsc.tab=0). This library standardizes web security behavior so it's easy to protect any sort of Spring Boot-based API.

### 5.c. Protecting Endpoints with a Pattern - Simple Example

By default, this example code does not protect any endpoints.  This example can change configuration in `application.yaml` to match your needs.

Each URL requires a `type` (either [`ANT`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html), [`MVC`](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/servlet/util/matcher/MvcRequestMatcher.html), or [`REGEX`](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/util/matcher/RegexRequestMatcher.html)), a `pattern` string, and a list of one or more `roles`.  For any URL that matches the pattern, the caller is required to be in at least one of the provided roles.

Role names are taken from the BSG claims in the **JWT** token.  Role names are always in **UPPER CASE** and always start with `ROLE_`.  For example, if the BSG claims include `one` as a role, then the Spring Security context will include `ROLE_ONE`.

For API, the simplest example looks like this:

```yaml
bsg.jwt:
  protectedUrls:
    - type: ANT
      pattern: /engine-rest/**
      roles: [ ROLE_ONE ]
```

### 5.d. Protecting Endpoints with a Pattern - Complicated Example

Here's a slightly more complicated example, where portions of the API are identified in different ways and have different protections:

```yaml
bsg.jwt:
  protectedUrls:
    - type: REGEX
      pattern: .*process-definition.*
      roles: [ ROLE_ONE, ROLE_TWO ]
    - type: ANT
      pattern: /engine-rest/history/process-instance/**
      roles: [ ROLE_THREE ]
```

**NOTE:** that the pattern must match the endpoint configured on the controller itself, disregarding any context path that is set for the application.  Even if you were running this application with context path `/demo` (meaning that the API would be invoked at `/demo/engine-rest/process-definition` or whatever) you still only pattern match on `/engine-rest/**`.

### 5.e Azure Communication Service API Authentication

Protect these **Azure Communication Service API** endpoints depends a lot on the use case and who is expecting to invoke the [Azure Communication Service API Authentication](https://learn.microsoft.com/en-us/rest/api/communication/authentication).  In some cases, this might protect all endpoints with a single role.  In other cases, you may have one role that is allowed to start a workflow and retrieve results, but other roles that are allowed to change system configuration. Think this through carefully.  For instance, if browser end users are going to call the Azure Communication Service API directly, you probably don't want them to be able to do dangerous things like remove or modify rulesets through the API.

### 5.f. Retrieving a Token

5.f.1. To invoke a protected endpoint, a caller first gets an access token from the **authorization service**.

5.f.2. In the **User Interface**, this is taken care of by standard functionality.  The auth library sends the user to the correct login page and then uses the password grant to generate an access token for the logged-in user.  The BSG access token can be found in local storage.

5.f.3. Backend callers need to get their own access token using the **client credentials grant** instead of the **password grant**. This requires a `client_id` and `secret`.

5.f.4. In the dev environment, the simplest way for developers and testers to retrieve an access token is to use the  bsg_devops` client id with the client credentials grant:

```bash
curl --request POST --url 'https://<EXTERNAL_HOST_IP>:<PORT>/auth/oauth/token?grant_type=client_credentials&client_id=bsg_devops&client_secret=devops'
```

You can look at the resulting **JWT** claims using the debugger at [jwt.io](https://jwt.io).

### 5.g. Invoking Protected Endpoints

Once the caller has an **Access Token**, they provide it as a `Bearer` token when making API calls, using the `Authorization` header:

```yaml
Authorization: Bearer eyJraWQiOiI0MDU3ZDZkNS1jMjYzLTRj...Rze-NxpO89BStpkIGSFb1FQ
```

When that API call arrives at the application, Spring uses the functionality in [spring-jwt-library](https://www.springcloud.io/post/2022-02/spring-security-new-jwt/#gsc.tab=0) to extract the `Bearer` token, verify its signature, and extract granted authorities based on the claims in the token.

### 6. Docker Compose for PostgresDB

In the **IntelliJ** run configurations, **Add New Configuration**, and select ***Docker-Compose*** as shown below and then select the docker compose file path as shown in the second screenshot.

- Add New **Docker** Configuration:
***Docker-compose.yaml***

![image](.github/media/screen-1.png)

- Docker-compose PostgresDB:
***postgres***

![image](.github/media/screen-2.png)

## 7. Run Locally

In the **IntelliJ** run configurations, **Add New Configuration**, and select ***Spring Boot Configuration*** and then provide the ***main*** class

## 8. Automation Testing - Behavior-Driven Development (BDD)

Implement the **[Behavior-Driven Development (BDD)](https://en.wikipedia.org/wiki/Behavior-driven_development)** tests using **[Cucumber](https://cucumber.io/docs/guides/api-automation/?lang=java)**.

---

### Contributing

Feel free to fork the repository, make changes, and ***submit pull requests***.

### License

This project is licensed under the ***Apache License 2.0*** - see the **<u>[LICENSE](LICENSE)</u>** file for details.

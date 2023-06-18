# Back End

```
스프링 부트 3.0.1과 코틀린 1.7.22를 사용하여 제작 되었습니다.
rest api와 graphql을 이용합니다.
DockerFile을 통해 젠킨스에서 자동으로 실행 해줍니다.

크롤러는 async와 크론 탭 스케줄러로 구성 되어 있습니다.
Jsoup으로 진행되며 CGV의 리뷰는 단순 Jsoup으로는 데이터를 가지고 올 수 없어 제작에서 제외 되었습니다.(MegaBox와 Lotte Cinema의 리뷰만 가져옵니다.)

rest api의 반환 값은 ResponseEntity로 반환 되며, 에러는 해당하는 코드값, 에러 코드, 에러 명으로 구성되어 있습니다.
dto가 늘어 날수록 구분이 힘들어져 도메인 중심 구조로 이루어져 있으며 각 도메인은
- controller
  - response
  - request
- entity
  - dto
  - entity
- repository
  - support (querydsl 사용)
- exception
- service
  - impl (중복 사용하는 경우)
로 구성되어 있습니다.
```

## build.gradle.kts
```
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.1"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.jetbrains.kotlin.plugin.noarg") version "1.7.22"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
	kotlin("plugin.jpa") version "1.7.22"
	kotlin("kapt") version "1.7.22"
}

noArg {
	annotation("jakarta.persistence.Entity")
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

group = "com.threemovie"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.jsoup:jsoup:1.15.4")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	implementation("org.json:json:20230227")
	implementation("org.ehcache:ehcache:3.10.8")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.4")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("com.github.f4b6a3:ulid-creator:5.2.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	
	kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
}

tasks.withType<Jar> {
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
```

## properties
- application
```
spring.profiles.include=smtp-config, db-config, jwt-config, security-config
spring.graphql.path=/api/graphql
spring.graphql.graphiql.path=/api/graphiql
spring.graphql.graphiql.enabled=true
spring.graphql.schema.locations=classpath:graphql/**/
spring.graphql.schema.file-extensions=.graphqls,.gqls
spring.graphql.schema.introspection.enabled=true
spring.graphql.schema.printer.enabled=true
spring.graphql.websocket.connection-init-timeout=60s
spring.mustache.check-template-location=false
```
  
- db
```
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
spring.datasource.hikari.data-source-properties.rewriteBatchedStatements=true
spring.datasource.hikari.data-source-properties.useConfigs=maxPerformance
spring.jpa.properties.hibernate.jdbc.batch_size=300
spring.datasource.hikari.validationTimeout=300000
spring.datasource.hikari.connection-timeout=58000
spring.datasource.hikari.max-lifetime=580000
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.order_inserts=true
spring.datasource.url=jdbc:mariadb://ip/db이름?serverTimezone=Asia/Seoul&useUnicode=true&characterEncoding=utf8mb4
spring.datasource.username=아이디
spring.datasource.password=비밀번호
spring.jpa.open-in-view=false
spring.data.redis.host=ip
spring.data.redis.port=port
spring.data.redis.password=비밀번호
```
  
- jwt
```
jwt.secret.key=비밀키
jwt.secret.refreshtoken-validity-in-seconds=50400
jwt.secret.access-token-validity-in-seconds=7200
```

- security
```
spring.security.user.name=아이디
spring.security.user.password=비밀번호
```

- smtp (네이버 메일 전송)
```
spring.mail.host=smtp.naver.com
spring.mail.default-encoding=UTF-8
spring.mail.port=465
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.naver.com
spring.mail.username=아이디
spring.mail.password=비밀번호
```
  

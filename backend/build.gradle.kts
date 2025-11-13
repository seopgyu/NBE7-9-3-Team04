import org.gradle.kotlin.dsl.implementation

plugins {
	java
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com"
version = "0.0.1-SNAPSHOT"
description = "backend"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
    compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("com.h2database:h2")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
    implementation ("org.springframework.boot:spring-boot-starter-mail")
    implementation("com.github.napstr:logback-discord-appender:1.0.0")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
	implementation("org.springframework.ai:spring-ai-starter-model-openai")
    implementation("org.springframework.ai:spring-ai-starter-model-vertex-ai-gemini")


    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.3.0")
    implementation("org.springframework.boot:spring-boot-starter-aop")
	//querydsl
	annotationProcessor("io.github.openfeign.querydsl:querydsl-apt:7.1:jpa")
	implementation("io.github.openfeign.querydsl:querydsl-jpa:7.1")

    //Elasticsearch
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

    //redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("com.github.codemonstur:embedded-redis:1.4.3")
}
extra["springAiVersion"] = "1.1.0-M1"

dependencyManagement {
	imports {
		mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

//-----------querydsl-----------//
val querydslDir = file("src/main/generated")

sourceSets {
	main {
		java.srcDir(querydslDir)
	}
}

tasks.withType<JavaCompile> {
	options.generatedSourceOutputDirectory.set(querydslDir)
}

tasks.named("clean") {
	doLast {
		querydslDir.deleteRecursively()
	}
}
//--------------------------------//
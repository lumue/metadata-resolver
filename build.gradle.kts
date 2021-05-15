import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
}

group = "io.github.lumue"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jsoup:jsoup:1.11.3")
    implementation("org.slf4j:slf4j-api:1.7.10")
    implementation("org.apache.httpcomponents:httpclient:4.5.5")
    implementation("ru.gildor.coroutines:kotlin-coroutines-retrofit:0.13.0-eap13")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.6")
    implementation("org.springdoc:springdoc-openapi-ui:1.5.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    docker {

        imageName = "lumue/metadata-resolver"
        isPublish=true
        publishRegistry {
            username = "lumue"
            password = "ddlom85s"
            url = "https://registry.docker-hub.com/v2/"
            email = "mueller.lutz@gmail.com"
        }
    }
}
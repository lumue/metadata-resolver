import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
}

group = "net.lumue"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

configurations {
        all {
            exclude( "org.springframework.boot",  "spring-boot-starter-logging")
            exclude ("ch.qos.logback", "logback-classic")

        }

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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("org.slf4j:slf4j-api:1.7.10")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.apache.httpcomponents.core5:httpcore5:5.2.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("ru.gildor.coroutines:kotlin-coroutines-retrofit:1.1.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.15")
    implementation ("org.seleniumhq.selenium:selenium-java:4.6.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
   useJUnitPlatform()
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    docker {
        imageName.set("lumue/metadata-resolver")
        publish.set(true)
        publishRegistry {
            username.set("lumue")
            password.set("M9w8a+ET9u@+tA%")
            url.set("https://registry.docker-hub.com/v2/")
            email.set("mueller.lutz@gmail.com")
        }
    }
}

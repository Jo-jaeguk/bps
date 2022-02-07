import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}


repositories {
    mavenCentral()
}


dependencies {

    api(kotlin("reflect"))
    api(kotlin("stdlib-jdk8"))
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-data-jdbc")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api ("io.github.microutils:kotlin-logging:1.12.0")
    api ("io.springfox:springfox-swagger-ui:2.9.2")
    api ("io.springfox:springfox-swagger2:2.9.2")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    api("org.springframework.boot:spring-boot-starter-jdbc")
    api("org.springframework.boot:spring-boot-starter-thymeleaf")
    api ("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:2.1.2")
    api("org.modelmapper:modelmapper:2.3.7")
    api ("com.google.code.gson:gson")
    api("org.mapstruct:mapstruct:1.3.0.Final")
    kapt("org.mapstruct:mapstruct-processor:1.3.0.Final")

    api( fileTree("libs/NiceID.jar"))

    api("com.querydsl:querydsl-jpa:4.2.1")
    kapt("com.querydsl:querydsl-apt:4.2.2:jpa")

    api ("org.apache.httpcomponents:httpclient")

    api("com.amazonaws:aws-java-sdk-s3:1.11.238")

    api("org.apache.poi:poi:4.1.2")
    api("org.apache.poi:poi-ooxml:4.1.2")

    api("com.google.firebase:firebase-admin:7.3.0")

    api("io.jsonwebtoken:jjwt:0.9.1")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    api("org.mariadb.jdbc:mariadb-java-client")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath( "org.jetbrains.kotlin:kotlin-noarg:1.3.71")
    }
}


repositories {
    mavenCentral()
}


plugins {
    id("org.springframework.boot") version "2.5.0" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.spring") version "1.5.10"
    kotlin("plugin.jpa") version "1.5.10"
    kotlin("kapt") version "1.5.10"
}


allprojects {
    group = "com.mobilityk"
    version = "0.0.1-SNAPSHOT"
    //java.sourceCompatibility = JavaVersion.VERSION_15
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}


configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}


subprojects {
    repositories {
        mavenCentral()
    }

    apply {
        plugin("kotlin")
        plugin("kotlin-spring")
        plugin("kotlin-jpa")
        plugin("kotlin-kapt")
        plugin("idea")
        plugin("eclipse")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin( "kotlin-allopen")
    }

}
allOpen {
    annotation("javax.persistence.Entity")
}


project(":app-admin") {
    dependencies {
        compileOnly(project(":core"))
    }
}

project(":app-user") {
    dependencies {
        compileOnly(project(":core"))
    }
}


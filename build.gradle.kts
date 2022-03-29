import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.google.cloud.tools.jib") version "3.2.0"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"
}

group = "com.kevvlvl"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {

    mavenCentral()

    maven {
        url = uri("https://repo.spring.io/milestone")
    }
}

val springCloudDependenciesVersion = "2021.0.1"
val springCloudSleuthOtelVersion = "1.1.0-M5"
val grpcHttpVersion = "1.45.0"

dependencies {

    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:$springCloudDependenciesVersion"))
    implementation(platform("org.springframework.cloud:spring-cloud-sleuth-otel-dependencies:$springCloudSleuthOtelVersion"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // exclude brave/zipkin from sleuth. We'll use instead an opentelemetry library as the implementation
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth") {
        exclude(group = "org.springframework.cloud", module = "spring-cloud-sleuth-brave")
    }
    implementation("org.springframework.cloud:spring-cloud-sleuth-otel-autoconfigure")

    // available for integration with logz.io's opentelemetry collector
    //    implementation("io.opentelemetry:opentelemetry-exporter-otlp-trace")
    //    implementation("io.grpc:grpc-okhttp:$grpcHttpVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.flywaydb:flyway-core")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
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

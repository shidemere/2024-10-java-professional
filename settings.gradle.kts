// Настройки проекта

rootProject.name = "2024-10-java-professional"

pluginManagement {
    val dependencyManagement: String by settings
    val springframeworkBoot: String by settings

    plugins {
        id("io.spring.dependency-management") version dependencyManagement
        id("org.springframework.boot") version springframeworkBoot
    }
}
include("HW01_Gradle")

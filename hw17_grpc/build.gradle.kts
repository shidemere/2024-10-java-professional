import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("idea")
    // Плагин для генерации классов. Конфигурируется внизу.
    id("com.google.protobuf")
}

val errorProneAnnotations: String by project
val tomcatAnnotationsApi: String by project

dependencies {
    /*
        Реализация gRPC на Netty, как сказал Сергей "наиболее типовой транспорт для gRPC"
        Ну то, что он на Нетти наверное значит что он ассинхронный.
        То-есть он может быть и на другом?

     */
    implementation("io.grpc:grpc-netty")
    /*
        Интеграция protobuf и grpc.
        Необходима для сериализации. Хотя непонятно зачем надо было в отдельную зависимость выносить
     */
    implementation("io.grpc:grpc-protobuf")
    /*
        Базовые классы для gRPC-заглушек (клиент/сервер)
     */
    implementation("io.grpc:grpc-stub")
    /*
        Для работы с protobuf в java
     */
    implementation("com.google.protobuf:protobuf-java")
    /*
        Библиотека для статического анализа кода на этапе компиляции.
        То есть условно ставишь какую нибудь аннотацию и таким образом вешаешь правило на код.
     */
    implementation("com.google.errorprone:error_prone_annotations:$errorProneAnnotations")
    /*
        Предоставляет аннотации исползьуемые в веб приложениях и сервлетах.
        Может быть использовано независимо от Tomcat
     */
    implementation("org.apache.tomcat:annotations-api:$tomcatAnnotationsApi")

    implementation("ch.qos.logback:logback-classic")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

val protoSrcDir = "$projectDir/build/generated"

/**
 * Указание среде разработки что, необходимо добавить к файлам исходного кода
 * файлы из определенной папки. То есть условно когда мы сгенерируем какие-нибудь классы по .proto
 * эта настройка позволит нам пользоваться ими в коде.
 */
idea {
    module {
        sourceDirs = sourceDirs.plus(file(protoSrcDir))
    }
}
/**
 * Сгенерированные классы протобуфа лежат в нестандратном месте.
 * Поэтому их необходимо вот так вручную добавлять в общий список исходных файлов для градла.
 */
sourceSets {
    main {
        proto {
            srcDir(protoSrcDir)
        }
    }
}

protobuf {
    /**
     * Куда класть сгенерированные классы
     */
    generatedFilesBaseDir = protoSrcDir

    /**
     * Утилита которая используется плагином для компиляции классов из прото файлов
     */
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.4"
    }
    /**
     * Включаем генерацию кода для main
     */
    generateProtoTasks {
        ofSourceSet("main")
    }
}

/**
 * Задача "generateProto" зависит от "processResources".
 * Таким образом генерация .proto файлы в ресурсах будут обработаны до выполнения таски processResources
 * А это таска нужна уже для генерации кода
 */
afterEvaluate {
    tasks {
        getByName("generateProto").dependsOn(processResources)
    }
}


protobuf {
    generatedFilesBaseDir = protoSrcDir

    /**
     * Утилита для создания gRPC сервисов.
     */
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.4"
    }
    /**
     * Добавление плагина grpc
     */
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.56.1"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}
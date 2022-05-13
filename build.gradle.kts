// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion by extra { "1.6.10" }

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io/")
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
    rootProject.childProjects.forEach { project ->
        delete(project.value.buildDir)
    }
}

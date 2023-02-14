val nexusUrl: String by extra
val nexusUsername: String by extra
val nexusPassword: String by extra

buildscript {
    val kotlinVersion by extra { "1.6.10" }
    val nexusUrl by extra { System.getProperty("com.vidyo.nexus.url").orEmpty() }
    val nexusUsername by extra { System.getProperty("com.vidyo.nexus.username").orEmpty() }
    val nexusPassword by extra { System.getProperty("com.vidyo.nexus.password").orEmpty() }

    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }

    repositories {
        if (nexusUrl.isNotEmpty()) {
            maven {
                url = uri(nexusUrl)
                credentials.username = nexusUsername
                credentials.password = nexusPassword
            }
        } else {
            google()
            mavenCentral()
        }
    }
}

allprojects {
    repositories {
        if (nexusUrl.isNotEmpty()) {
            maven {
                url = uri(nexusUrl)
                credentials.username = nexusUsername
                credentials.password = nexusPassword
            }
        } else {
            google()
            mavenCentral()
            maven(url = "https://jitpack.io/")
            maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
    rootProject.childProjects.forEach { project ->
        delete(project.value.buildDir)
    }
}

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.Calendar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    `java-library`
    checkstyle
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("org.checkerframework") version "0.5.9"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
    id("com.github.hierynomus.license") version "0.15.0"
}

group = "com.proximyst"
version = "0.1.0"

repositories {
    // <editor-fold desc="Repositories" defaultstate="collapsed">
    // {{{ Repositories
    maven {
        name = "spigotmc"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }

    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")

        content {
            includeGroup("net.md-5")
        }
    }

    maven {
        name = "papermc-snapshots"
        url = uri("https://papermc.io/repo/repository/maven-snapshots/")

        content {
            includeGroup("com.destroystokyo.paper")
            includeGroup("io.github.waterfallmc")
            includeGroup("io.papermc")
        }
    }

    maven {
        name = "papermc"
        url = uri("https://papermc.io/repo/repository/maven-public/")

        content {
            includeGroup("com.destroystokyo.paper")
            includeGroup("io.github.waterfallmc")
            includeGroup("io.papermc")
        }
    }
    // }}}
    // </editor-fold>

    jcenter()
    mavenCentral()
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.3-R0.1-SNAPSHOT")
    implementation("cloud.commandframework:cloud-paper:1.1.0")
}

tasks {
    compileJava {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = sourceCompatibility
        options.isFork = true
        options.compilerArgs.add("-parameters")
    }

    compileTestJava {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = sourceCompatibility
    }

    withType<ShadowJar> {
        this.archiveClassifier.set(null as String?)
        this.archiveBaseName.set(
            if (project === rootProject) project.name
            else "${rootProject.name}-${project.name}"
        )
        this.destinationDirectory.set(
            rootProject
                .tasks
                .shadowJar.get()
                .destinationDirectory.get()
        )

        fun reloc(vararg pkgs: String) =
            pkgs.forEach { relocate(it, "${project.group}.${project.name}.dependencies.$it") }
        reloc(
            "cloud.commandframework",
            "io.leangen.geantyref"
        )
    }

    named("build") {
        dependsOn(withType<ShadowJar>())
    }
}

bukkit {
    name = "Gib"
    main = "com.proximyst.gib.GibPlugin"
    apiVersion = "1.13"
    author = "Proximyst"

    permissions {
        register("gib.gib") {
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}

checkstyle {
    toolVersion = "8.36.2"
    val configRoot = rootProject.projectDir.resolve(".checkstyle")
    configDirectory.set(configRoot)
    configProperties["basedir"] = configRoot.absolutePath
}

license {
    header = rootProject.file("LICENCE-HEADER")
    ext["year"] = Calendar.getInstance().get(Calendar.YEAR)
    include("**/*.java")

    mapping("java", "DOUBLESLASH_STYLE")
}
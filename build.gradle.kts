import java.io.FileInputStream
import java.security.MessageDigest
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  // alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.secrets) apply false
  alias(libs.plugins.google.services) apply false
}

fun sha256Hex(file: java.io.File): String {
  val digest = MessageDigest.getInstance("SHA-256")
  FileInputStream(file).use { fis ->
    val buffer = ByteArray(65536)
    var read = fis.read(buffer)
    while (read != -1) {
      digest.update(buffer, 0, read)
      read = fis.read(buffer)
    }
  }
  return digest.digest().joinToString("") { "%02x".format(it) }
}

tasks.register("exportAndVerifyDebugApk") {
  dependsOn(":app:assembleDebug")
  doLast {
    val sourceApk = file("app/build/outputs/apk/debug/app-debug.apk")
    check(sourceApk.exists() && sourceApk.length() > 0L) {
      "Source debug APK missing or empty at ${sourceApk.absolutePath}"
    }

    val buildOutputsDir = file(".build-outputs").apply { mkdirs() }
    val apkDownloadDir = file("APK_DOWNLOAD").apply { mkdirs() }

    val targetBuildOutputApk = file(".build-outputs/app-debug.apk")
    val targetDownloadApk = file("APK_DOWNLOAD/app-debug.apk")

    sourceApk.copyTo(targetBuildOutputApk, overwrite = true)
    sourceApk.copyTo(targetDownloadApk, overwrite = true)

    val srcHash = sha256Hex(sourceApk)
    val buildOutputHash = sha256Hex(targetBuildOutputApk)
    val downloadHash = sha256Hex(targetDownloadApk)

    check(srcHash == buildOutputHash && srcHash == downloadHash) {
      "APK hash mismatch! src=$srcHash, buildOutputs=$buildOutputHash, download=$downloadHash"
    }

    // Validate APK structure and AndroidManifest.xml + DEX + signing block
    var hasManifest = false
    var dexCount = 0
    var hasResources = false
    var entryCount = 0
    JarFile(targetDownloadApk, true).use { jar ->
      val entries = jar.entries()
      while (entries.hasMoreElements()) {
        val entry = entries.nextElement()
        entryCount++
        if (entry.name == "AndroidManifest.xml") hasManifest = true
        if (entry.name.startsWith("classes") && entry.name.endsWith(".dex")) dexCount++
        if (entry.name == "resources.arsc") hasResources = true
      }
    }

    // Check APK Signing Block v2/v3 magic ("APK Sig Block 42") in raw bytes
    val hasV2SigBlock = sourceApk.inputStream().use { input ->
      val bytes = input.readBytes()
      val magic = "APK Sig Block 42".toByteArray(Charsets.US_ASCII)
      outer@ for (i in 0..(bytes.size - magic.size)) {
        for (j in magic.indices) {
          if (bytes[i + j] != magic[j]) continue@outer
        }
        return@use true
      }
      false
    }

    check(hasManifest && dexCount > 0 && hasResources) {
      "Invalid APK contents: hasManifest=$hasManifest, dexCount=$dexCount, hasResources=$hasResources"
    }

    // Create downloadable project ZIP containing complete Android project + both APK folders
    val zipFile = file("PriyaLive-Project-With-Debug-APK.zip")
    if (zipFile.exists()) zipFile.delete()

    val excludedRootDirs = setOf(".gradle", "build", ".idea", "captures")
    ZipOutputStream(zipFile.outputStream().buffered()).use { zos ->
      rootDir.walkTopDown()
        .onEnter { dir ->
          if (dir == rootDir) true
          else if (dir.parentFile == rootDir && dir.name in excludedRootDirs) false
          else if (dir.parentFile == file("app") && dir.name == "build") false
          else true
        }
        .filter { it.isFile && it != zipFile && !it.name.endsWith(".zip") }
        .forEach { fileToZip ->
          val relPath = fileToZip.relativeTo(rootDir).path.replace("\\", "/")
          val entry = ZipEntry(relPath)
          entry.time = fileToZip.lastModified()
          zos.putNextEntry(entry)
          fileToZip.inputStream().buffered().use { input ->
            input.copyTo(zos)
          }
          zos.closeEntry()
        }
    }

    println("=== APK & ZIP VERIFICATION REPORT ===")
    println("SOURCE_APK_PATH=${sourceApk.absolutePath}")
    println("BUILD_OUTPUTS_APK_PATH=${targetBuildOutputApk.absolutePath}")
    println("APK_DOWNLOAD_PATH=${targetDownloadApk.absolutePath}")
    println("APK_SIZE_BYTES=${sourceApk.length()}")
    println("APK_SHA256=$srcHash")
    println("APK_ENTRIES=$entryCount, DEX_FILES=$dexCount, HAS_MANIFEST=$hasManifest, HAS_RESOURCES=$hasResources, HAS_APK_SIG_BLOCK_V2=$hasV2SigBlock")
    println("PROJECT_ZIP_PATH=${zipFile.absolutePath}")
    println("PROJECT_ZIP_SIZE_BYTES=${zipFile.length()}")
    println("=====================================")
  }
}




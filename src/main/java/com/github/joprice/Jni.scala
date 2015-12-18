package com.github.joprice

import sbt._
import Keys._
import scala.language.postfixOps
import java.io.File

object Jni {
  object Keys {

    //tasks

    lazy val jni = taskKey[Unit]("Run jni build")

    lazy val jniCompile = taskKey[Unit]("Compiles jni sources using gcc")

    lazy val javah = taskKey[Unit]("Builds jni sources")

    lazy val possibleNativeSources = taskKey[Seq[File]]("Lists files with native annotations. Used to find classes to append to 'nativeSource'")

    // settings

    lazy val nativeCompiler = settingKey[String]("Compiler to use. Defaults to gcc")

    lazy val cppExtensions = settingKey[Seq[String]]("Extensions of source files")

    lazy val jniClasses = settingKey[Seq[String]]("Classes with native methods")

    lazy val headersPath = settingKey[File]("Generated JNI headers")

    lazy val binPath = settingKey[File]("Shared libraries produced by JNI")

    lazy val nativeSources = settingKey[File]("JNI native sources")

    lazy val includes = settingKey[Seq[String]]("Compiler includes settings")

    lazy val jniSourceFiles = settingKey[Seq[File]]("Jni source files")

    lazy val gccFlags = settingKey[Seq[String]]("Flags to be passed to gcc")

    lazy val libraryName = settingKey[String]("Shared library produced by JNI")

    lazy val jreIncludes = settingKey[Seq[String]]("Includes for jni")

    lazy val jdkHome = settingKey[Option[File]]("Used to find jre include files for JNI")

    lazy val cpp11 = settingKey[Boolean]("Whether to pass the cpp11 flag to the compiler")

    lazy val libSuffix = settingKey[String]("Suffix for shared library, e.g., .so, .dylib")
  }

  import Jni.Keys._

  def jreIncludeFolder = {
    System.getProperty("os.name") match {
      case "Linux" => "linux"
      case "Mac OS X" => "darwin"
      case  _ => throw new Exception("Cannot determine os name. Provide a value for `javaInclude`.")
    }
  }

  def withExtensions(directory: File, extensions: Seq[String]): Seq[File] = {
    val extensionsFilter = extensions.map("*." + _).foldLeft(NothingFilter: FileFilter)(_ || _)
    (directory ** extensionsFilter).get
  }

  val settings = Seq(
    nativeCompiler := "gcc",
    jniClasses := Seq.empty,
    jdkHome := {
      val home = new File(System.getProperty("java.home"))
      if (home.exists) Some(home) else None
    },
    jreIncludes := {
      jdkHome.value.fold(Seq.empty[String]) { home =>
        val absHome = home.getAbsolutePath
        // in a typical installation, jdk files are one directory above the location of the jre set in 'java.home'
        Seq(s"include", s"include/$jreIncludeFolder").map(file => s"-I${absHome}/../$file")
      }
    },
    includes := Seq(
      s"-I${headersPath.value}",
      "-I/usr/include",
      "-L/usr/local/include"
    ) ++ jreIncludes.value,
    cpp11 := true,
    // 'dylib' and 'jnilib' work on mac, while linux expects 'so'
    libSuffix := "jnilib",
    gccFlags := Seq(
      "-shared",
      "-fPIC",
      "-O3"
    ) ++ (if (cpp11.value) Seq("-std=c++0x") else Seq.empty)
      ++ includes.value,
    binPath := (target in Compile).value / "native" /  "bin",
    headersPath := (target in Compile).value / "native" / "include",
    nativeSources := (sourceDirectory).value / "main" / "native",
    cppExtensions := Seq("c", "cpp", "cc", "cxx"),
    jniSourceFiles := withExtensions(nativeSources.value, cppExtensions.value),
    jniCompile := Def.task {
      val log = streams.value.log
      val mkBinDir = s"mkdir -p ${binPath.value}"
      log.info(mkBinDir)
      mkBinDir ! log
      val sources = jniSourceFiles.value.mkString(" ")
      val flags = gccFlags.value.mkString(" ")
      val command = s"${nativeCompiler.value} $flags -o ${binPath.value}/lib${libraryName.value}.${libSuffix.value} $sources"
      log.info(command)
      Process(command, binPath.value) ! (log)
    }.dependsOn(javah)
     .tag(Tags.Compile, Tags.CPU)
     .value,
    javah := Def.task {
      val log = streams.value.log
      val classes = (fullClasspath in Compile).value.map(_.data).mkString(File.pathSeparator)
      val javahCommand = s"javah -d ${headersPath.value} -classpath $classes ${jniClasses.value.mkString(" ")}"
      log.info(javahCommand)
      javahCommand ! log
    }.dependsOn(compile in Compile)
     .tag(Tags.Compile, Tags.CPU)
     .value,
    cleanFiles ++= Seq(
      binPath.value,
      headersPath.value
    ),
    compile <<= (compile in Compile, jniCompile).map((result, _) => result),
    // Make shared lib available at runtime. Must be used with forked jvm to work.
    javaOptions ++= Seq(
      s"-Djava.library.path=${binPath.value}"
    ),
    //required in order to have a separate jvm to set java options
    fork in run := true,
    possibleNativeSources := {
      def withExtension(dir: File, extension: String) = (dir ** s"*.$extension").filter(_.isFile).get
      //(javaSource.value ***)
      val JavaRegex = " native .*\\)\\s*;".r
      val nativeJava = withExtension((javaSource in Compile).value, "java").flatMap { file =>
        val source = IO.readLines(file)
        val hasNative = source.exists {
          case JavaRegex(_) => true
          case _ => false
        }
        if (hasNative) Some(file) else None
      }

      val nativeScala = withExtension((scalaSource in Compile).value, "scala")
        .filter(IO.read(_).contains("@native"))

      nativeJava ++ nativeScala
    }
  )
}


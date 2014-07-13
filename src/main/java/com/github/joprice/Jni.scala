package com.github.joprice

import sbt._
import Keys._
import sbtsequential.Plugin._

object Jni {
  object Keys {

    //tasks

    lazy val jni = taskKey[Unit]("Run jni build")

    lazy val jniCompile = taskKey[Unit]("Compiles jni sources using gcc")

    lazy val javah = taskKey[Unit]("Builds jni sources")

    // settings

    lazy val nativeCompiler = settingKey[String]("Compiler to use. Defaults to gcc")

    lazy val cppExtensions = settingKey[Seq[String]]("Extensions of source files")

    lazy val jniClasses = settingKey[Seq[String]]("Classes with native methods")

    lazy val headersPath = settingKey[File]("Generated JNI headers")

    lazy val binPath = settingKey[File]("Shared libraries produced by JNI")

    lazy val nativeSource = settingKey[File]("JNI native sources")

    lazy val includes = settingKey[Seq[String]]("Compiler includes settings")

    lazy val jniSourceFiles = settingKey[Seq[File]]("Jni source files")

    lazy val gccFlags = settingKey[Seq[String]]("Flags to be passed to gcc")

    lazy val libraryName = settingKey[String]("Shared library produced by JNI")

    lazy val jreIncludes = settingKey[Seq[String]]("Includes for jni")

    lazy val jdkHome = settingKey[Option[File]]("Used to find jre include files for JNI")

    lazy val cpp11 = settingKey[Boolean]("Whether to pass the cpp11 flag to the compiler")
  }

  import Jni.Keys._

  def jreIncludeFolder = {
    System.getProperty("os.name") match {
      case "Linux" => "linux"
      case "Mac OS X" => "darwin"
      case  _ => throw new Exception("Cannot determine os name. Provide a value for `javaInclude`.")
    }
  }

  def withExtensions(files: Seq[File], extensions: Seq[String]) = {
    files.filter { file =>
      file.isFile && extensions.exists(file.getName.toLowerCase.endsWith)
    }
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
    gccFlags := Seq(
      "-shared",
      "-fPIC",
      "-O3"
    ) ++ (if (cpp11.value) Seq("-std=c++0x") else Seq.empty)
      ++ includes.value,
    binPath := new File((target in Compile).value / "native",  "bin"),
    headersPath := new File((target in Compile).value / "native", "include"),
    nativeSource := new File((sourceDirectory).value / "main",  "native"),
    cppExtensions := Seq(".c", ".cpp", ".cc", ".cxx"),
    jniSourceFiles := withExtensions((nativeSource.value ** "*").get, cppExtensions.value),
    jniCompile := Def.task {
      val log = streams.value.log
      val mkBinDir = s"mkdir -p ${binPath.value}" 
      log.info(mkBinDir)
      mkBinDir ! log
      val sources = jniSourceFiles.value.mkString(" ")
      val flags = gccFlags.value.mkString(" ")
      //TODO: .so for linux, .dylib for mac
      val command = s"${nativeCompiler.value} $flags -o ${binPath.value}/${libraryName.value}.so $sources"
      log.info(command)
      Process(command, binPath.value) ! (log)
    }.dependsOn(javah)
     .tag(Tags.Compile, Tags.CPU)
     .value,
    javah := Def.task {
      val log = streams.value.log
      val classes = {(classDirectory in Compile).value} 
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
    fork in run := true
  )
}


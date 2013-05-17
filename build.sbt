
name := "skitch-engine"

version := "0.1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.10.0"

libraryDependencies ++= Seq(
  "org.clapper" % "grizzled-slf4j_2.10" % "1.0.1",
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "org.jbox2d" % "jbox2d-library" % "2.2.1.1",
  "org.jbox2d" % "jbox2d-serialization" % "1.0.0",
  "com.googlecode.soundlibs" % "jorbis" % "0.0.17-1",
  "org.urish.openal" % "java-openal" % "1.0.0",
  "org.lwjgl.lwjgl" % "lwjgl" % "2.8.4",
  "org.lwjgl.lwjgl" % "lwjgl_util" % "2.8.4",
  "org.lwjgl.lwjgl" % "lwjgl-platform" % "2.8.4" classifier "natives-osx" classifier "natives-windows" classifier "natives-linux"
//  "net.java.jinput" % "jinput" % "2.0.5",
//  "net.java.jinput" % "jinput-platform" % "2.0.5" classifier "natives-osx" classifier "natives-windows" classifier "natives-linux"
//  "com.googlecode.soundlibs" % "jorbis" % "0.0.17-1"
)

resolvers ++= Seq(
    "Codehaus Snapshots" at "http://nexus.codehaus.org/snapshots/",
    "OSS Sonatype" at "http://oss.sonatype.org/content/repositories/github-releases",
    "Scala Tools 2" at "http://scala-tools.org/repo-releases"
)

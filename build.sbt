
name := "sbttest"

version := "1.0"

scalaVersion := "2.8.1"

organization := "com.yunrang"

/////////////////////////////////////////////////resolvers:

resolvers ++= Seq(
  "oss.sonatype.org" at "http://oss.sonatype.org/content/groups/scala-tools/",
  "yunrang-repo" at "http://dev.yunrang.com/nexus/content/groups/public2/",
  "com.twitter" at "http://maven.twttr.com/"
)

///////////////////////////////dependencies:
libraryDependencies ++= Seq(
  "net.lag" % "configgy" % "2.0.0",
  "com.yunrang" %% "base" % "1.3.30-SNAPSHOT",
  "com.google.code.guice" % "guice" % "2.0.1",
  "aopalliance" % "aopalliance" % "1.0",
  "com.twitter" % "util-logging" % "2.0.0"
)

////-------------------------- plugins --------------

seq(atd.sbtthrift.ThriftPlugin.thriftSettings: _*)



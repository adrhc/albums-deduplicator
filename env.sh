#!/bin/bash

# export JAVA_HOME=$TOOLS/jdk1.8.0_141
# export JAVA_HOME=/usr/lib/jvm/java-8-oracle
# export JAVA_HOME=$HOME/.sdkman/candidates/java/11.0.2-open
# export JAVA_HOME=$HOME/.sdkman/candidates/java/17.0.1-tem
#export JAVA_HOME=$HOME/.sdkman/candidates/java/18.0.1-oracle/
#export JAVA_HOME=$HOME/.sdkman/candidates/java/19.0.2-oracle/
export JAVA_HOME=$HOME/.sdkman/candidates/java/20.0.1-oracle/
export PATH=$JAVA_HOME/bin:$PATH
#export MAVEN_OPTS="$MAVEN_OPTS --enable-preview"

# export M2_HOME=$TOOLS/maven-3.3.9
# export M2_CONF=$M2_HOME/conf/settings-jisr.xml
# export MVN="$M2_HOME/bin/mvn -s \"$M2_CONF\""

if [ -e ./mvnw ]; then
	echo "using ./mvnw"
	MVN="./mvnw"
elif [ -e ../mvnw ]; then
	echo "using ../mvnw"
	MVN="../mvnw"
elif [ -e mvnw ]; then
	echo "using mvnw"
	MVN="mvnw"
elif [ "`which mvn`" != "" ]; then
	echo "using `which mvn`"
	MVN="mvn"
else
    echo "Can't find any of \"mvn\" or \"mvnw\"!"
    exit 1
fi
export MVN="$MVN -e"

echo -e "\nUsing java:"
java -version

# export TARGET=/tmp/target-ro.go.adrhc.albums-webapp

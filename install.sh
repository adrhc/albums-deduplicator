#!/bin/bash
if [ -e "env.sh" ]; then
	source env.sh
elif [ -e mvnw ]; then
	echo "env.sh missing, using ./mvnw"
	MVN="./mvnw"
else
	echo "env.sh missing, using mvn"
	MVN="mvn"
fi

#./clear-redis.sh
$MVN -e -Dmaven.javadoc.skip=true install "$@"

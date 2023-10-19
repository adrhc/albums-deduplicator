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

# ehcache static instance is overridden when using stage and production same time so we
# need forked tests when running stage and production tests in same mvn test command
#
# these require <forkCount> usage:
# ./test.sh -P db-off	-> equivalent to in-memory-only
# ./test.sh -P db-on
#
# these does not require <forkCount> usage:
# ./test.sh -P staging-only
# ./test.sh -P production-only
# echo "./test.sh -P db-off|db-on|staging-only|production-only"
#./clean.stage-db.sh

# The Surefire Plugin can be invoked by calling the test phase of the build lifecycle
$MVN -e test "$@"
echo "see /tmp/target-ro.go.adrhc-albums-webapp/site/*.html"

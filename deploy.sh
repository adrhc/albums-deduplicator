#!/bin/bash
source env.sh
# export EXIFWEB_PORT=8080
# export EXIFWEB_DIR=$HOME/Projects/git.exifweb/webapp/target/exifweb
# echo "cp: $EXIFWEB_DIR/WEB-INF/classes:$EXIFWEB_DIR/WEB-INF/lib/*"
# java -Dfile.encoding=UTF-8 -classpath "$EXIFWEB_DIR/WEB-INF/classes:$EXIFWEB_DIR/WEB-INF/lib/*" image.exifweb.ExifWebApp

deploy() {
	if [ ! -f /tmp/target-ro.go.adrhc-albums-webapp/albums-webapp-0.0.1-SNAPSHOT.war ]; then
		exit 1
	fi
	cp -v /tmp/target-ro.go.adrhc-albums-webapp/albums-webapp-0.0.1-SNAPSHOT.war /fast-disk/java-apps
}

deploy

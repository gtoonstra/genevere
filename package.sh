#!/bin/sh

echo "This builds the maven artefacts and generates an rpm and deb package for easy upload"
echo "to your private repo, or for easy direct installation on your source system"
echo ""
echo "(You need the packages fakeroot and alien installed)"

mvn clean package

(cd target; fakeroot alien -k rpm/genevere/RPMS/noarch/genevere-*)

echo "RPM package is available in target/rpm/genevere/RPMS/noarch/"
echo "DEB package is available in target/"

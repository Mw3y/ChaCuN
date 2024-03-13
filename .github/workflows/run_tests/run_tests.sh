#!/bin/bash

echo "Build in progress..."

javac -version

# ChaCuN/scripts/run_tests
ROOT_DIR=../../..

find "${ROOT_DIR}/src/ch/epfl/chacun" -name "*.java" > src_files.txt
find "${ROOT_DIR}/test/ch/epfl/chacun" -name "*.java" > test_files.txt

rm -rf ./out

# build production class
javac -d out/production/classes @src_files.txt
# build test class
javac -d out/test/classes -classpath out/production/classes:junit-platform-console-standalone-1.10.2.jar @test_files.txt
java -jar junit-platform-console-standalone-1.10.2.jar execute -cp out/production/classes:out/test/classes: --select-package ch.epfl.chacun --reports-dir reports

grep -q "failures=\"0\"" reports/TEST-junit-jupiter.xml || exit 1

echo "Build finished."

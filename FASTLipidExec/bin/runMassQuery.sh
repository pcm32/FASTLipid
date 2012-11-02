#!/bin/sh
PATHSEP=":"
WH_DIR=../target/
CPJ=${CPJ}${PATHSEP}${WH_DIR}fast-lipid-1.7.5-SNAPSHOT-jar-with-dependencies.jar
CPJ=${CPJ}${PATHSEP}${WH_DIR}fast-lipid-1.7.5-SNAPSHOT.jar
#fast-lipid-1.7.5-SNAPSHOT-jar-with-dependencies.jar 

# Ex:
# sh runMassQuery.sh -m 411.2022037 -o /tmp/test -r I -t 6 -h PE

#echo ${CPJ}
java -cp "${CPJ}" uk.ac.ebi.lipidhome.fastlipid.exec.MassSearch $*

rm -rf FASTLipidExec
rm FASTLipidExec.zip
mkdir -p FASTLipidExec/target
cp -r bin FASTLipidExec/
cp -r target/fast-lipid-1.7.5-SNAPSHOT-jar-with-dependencies.jar FASTLipidExec/target/
cp -r target/fast-lipid-1.7.5-SNAPSHOT.jar FASTLipidExec/target/
zip -r FASTLipidExec.zip FASTLipidExec

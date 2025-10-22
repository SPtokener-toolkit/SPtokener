rm -rf index
rm -rf tokens
rm -rf clonepairs.txt
mkdir index
mkdir tokens
sh gradlew run --args=\'"$1 $2"\' --no-daemon -Dorg.gradle.jvmargs="-Xms1024m -Xmx2048m"
rm -rf index
rm -rf tokens
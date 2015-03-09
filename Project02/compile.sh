datafolder="src/main/"
binfolder="bin"

# compile all java files in datafolder.
javac $datafolder/*.java
# create bin and store folders.
rm -rf $binfolder
mkdir -p ${binfolder}/stores
mkdir -p ${binfolder}/main
# move all class files into bin folder.
mv $datafolder/*.class "$binfolder/main"
# copy support scripts from scripts to bin
cp scripts/* bin

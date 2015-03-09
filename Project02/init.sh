#Compile and move all java files.
./compile.sh
#Generate all certificates.
./generate_certs.sh
#Create all journals
python generate_journals.py

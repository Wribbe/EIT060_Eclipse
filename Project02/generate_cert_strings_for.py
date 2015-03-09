import sys

filename = sys.argv[1]
access = sys.argv[2]
with open(filename) as file_handler:
    lines = file_handler.readlines();
    for line in lines:
        try:
            name, _ = line.split(";")
        except:
            name = line.strip()
        print "Create_signed_keystore \"{}\" password {}".format(name,access)

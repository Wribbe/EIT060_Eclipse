import random
import os

def make_journal(name, doctor, nurse, section):

    header_size = 70
    subheader_size = 5
    subheader_padding = 1*' '

    header = header_size*'='
    subheader = subheader_size*'='

    afflictions = ["jelly tumor","cough","fever","metroid infection","tetris back","EIT060 ARP poisoning"]
    statuses = ["KO","mellow","ecstatic","sad","fine","delusional","happy","dancing"]

    try:
        os.makedirs("./journals")
    except OSError:
        pass

    filename = "journals/{}.txt".format(name)
    with open(filename,'w') as file_handler:
        file_handler.write("{},{},{},{}\n".format(name,doctor,nurse,section))
        file_handler.write(header+'\n')
        file_handler.write("{}{}Journal for: {}\n".format(subheader,subheader_padding,name))
        file_handler.write(header+'\n')
        file_handler.write("Age: {}\n".format(random.choice(range(18,100))))
        file_handler.write("Affliction: {}\n".format(random.choice(afflictions)))
        file_handler.write('\n')
        file_handler.write("Current physician: {}\n".format(doctor))
        file_handler.write("Current nurse: {}\n".format(nurse))
        file_handler.write('\n')
        file_handler.write("Division: {}".format(section))
        file_handler.write('\n')
        file_handler.write("Current status: {}\n".format(random.choice(statuses)))
        file_handler.write('\n')
        file_handler.write(header+'\n')

doctors  = open("doctors.txt").readlines()
nurses = open("nurses.txt").readlines()
patients = open("patients.txt").readlines()
sections = ['A','B']

for name, doctor, nurse in zip(patients,doctors,nurses):
    section = random.choice(sections)
    make_journal(name.strip(),doctor.strip(),nurse.strip(), section)

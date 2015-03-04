import random

#names = ["Charlie","Sephirot","Mary","Jones","Ann","Svetlana","Peter"]
surenames = ["Nilsson","Svensson","Mercer","Persson","Zoran","Miller","Hultin"]

#names = ["Daniel","Viktoria","Susann","Molly","Adam","Victor","Condrad"]
names = ["Mario","Emma","Lisa","Kurt","Tommy","Batman","Donny"]


for name in names:
    surename = random.choice(surenames)
    surenames.remove(surename)
    print "{} {}".format(name,surename)

from motorController import Motor
from time import sleep

m1 = Motor(1, 11,13,15)
m2 = Motor(2, 22,16,18)

while (True):
    inp = input()

    if (inp == 'h'):
        break

    for step in inp:
        if (step == 'f'):
            m1.forward(50)
            m2.forward(50)
        elif (step == 'b'):
            m1.reverse(50)
            m2.reverse(50)
        elif (step == 'l'):
            m1.forward(50)
            m2.reverse(50)
        elif (step == 'r'):
            m1.reverse(50)
            m2.forward(50)
    
        sleep(0.5)
    m1.stop()
    m2.stop()
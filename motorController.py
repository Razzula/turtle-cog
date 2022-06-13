# abstraction of pin controls as Motor class objects

import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BOARD)

class Motor:

    def __init__(self, id, e, f, r):

        self.id = id
        self.pins = {'e':e, 'f':f, 'r':r}

        GPIO.setup(self.pins['e'],GPIO.OUT)
        GPIO.setup(self.pins['f'],GPIO.OUT)
        GPIO.setup(self.pins['r'],GPIO.OUT)
        self.PWM = GPIO.PWM(e, 50)  # 50Hz frequency
        self.PWM.start(0)
        GPIO.output(self.pins['e'],GPIO.HIGH)
        GPIO.output(self.pins['f'],GPIO.LOW)
        GPIO.output(self.pins['r'],GPIO.LOW)

        self.speed = 0

    def forward(self, speed=None): # speed - Duty Cycle Percentage from 0 (stop) to 100
        print(str(self.id) + ": Forward")

        if (speed != None):
            self.speed = speed
        self.PWM.ChangeDutyCycle(self.speed)
        GPIO.output(self.pins['f'],GPIO.HIGH)
        GPIO.output(self.pins['r'],GPIO.LOW)

    def reverse(self, speed=None):
        print(str(self.id) + ": Reverse")

        if (speed != None):
            self.speed = speed
        self.PWM.ChangeDutyCycle(self.speed)
        GPIO.output(self.pins['f'],GPIO.LOW)
        GPIO.output(self.pins['r'],GPIO.HIGH)

    def stop(self):
        print(str(self.id) + ": Stop")

        self.PWM.ChangeDutyCycle(0)
        GPIO.output(self.pins['f'],GPIO.LOW)
        GPIO.output(self.pins['r'],GPIO.LOW)

    def setSpeed(self, speed):

        if (speed >= 0 & speed <= 100):
            # self.PWM.ChangeDutyCycle(speed)
            self.speed = speed

# m1 = Motor(1, 11,13,15)
# m2 = Motor(2, 22,16,18)
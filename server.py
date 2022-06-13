# server socket to run on RPi

import socket
from time import sleep
from motorController import Motor

m1 = Motor(1, 11,13,15)
m2 = Motor(2, 22,16,18)

HOST = "" ##socket.gethostname()
PORT = 65432

print(f"Running on {HOST}:{PORT}\n")

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s: #IPv4, TCP
    s.bind((HOST, PORT))
    s.listen()

    while True:
        conn, addr = s.accept() # block execution until connection established

        with conn:
            try:
                conn.sendall(b'1')
                print(f"{addr} connected")
            except:
                break

            while True:
                try:
                    data = conn.recv(1024)
                except:
                    data = b''

                if not data: #b'' signals termination
                    print(f"{addr} disconnected")
                    break

                data = data.decode('utf-8')
                print(data)

                if (data == 'w'):
                    m1.forward()
                    m2.forward()
                elif (data == 'a'):
                    m1.reverse()
                    m2.forward()
                elif (data == 's'):
                    m1.reverse()
                    m2.reverse()
                elif (data == 'd'):
                    m1.forward()
                    m2.reverse()
            
                elif (data[0] == ':'):
                    speed = int(data[1:])
                    m1.setSpeed(speed)
                    m2.setSpeed(speed)

                else:
                    m1.stop()
                    m2.stop()
                
            conn.close()
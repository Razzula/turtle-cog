# server socket to run on RPi

import socket

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
                print(data.decode('utf-8'))
                
            conn.close()
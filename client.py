import socket

HOST = "localhost"
PORT = 65432


with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:  #IPv4, TCP
    try:
        s.connect((HOST, PORT))
        data = s.recv(1024)
    except:
        print('Error: Could not connect')
        exit()

    print(f"Connected to {HOST}:{PORT}")
    while True:
        inp = input().encode('utf-8')
        s.sendall(inp)
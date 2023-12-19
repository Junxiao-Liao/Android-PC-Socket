import socket
import subprocess
import time
import threading

# Define host and port (forwarded from Android port 9000)
HOST = "127.0.0.1"
PORT = 8000

def setup_adb_forward():
    try:
        # Forward PC port 8000 to device port 9000
        subprocess.run(["adb", "forward", "tcp:8000", "tcp:9000"])
        return True
    except Exception as e:
        print(e)
        return False

# Function to send data to server
def send_to_server(socket, message):
    try:
        # Send message as UTF-encoded bytes
        socket.sendall(message.encode("utf-8"))
        print(f"Sent: {message}")
    except Exception as e:
        print(f"Error sending data: {e}")

# Start client
def main():
    try:
        setup_adb_forward()
        # Create socket connection
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
            sock.connect((HOST, PORT))

            # Start background thread to receive data
            receive_thread = threading.Thread(target=receive_data, args=(sock,))
            receive_thread.daemon = True
            receive_thread.start()

            # Get user input and send messages until program exits
            while True:
                message = input("Enter message to send: ")
                send_to_server(sock, message)
                time.sleep(1)  # Delay between sending messages

    except Exception as e:
        print(f"Error: {e}")

def receive_data(socket):
    while True:
        try:
            data = socket.recv(1024).decode("utf-8")
            if not data:
                print("Connection closed")
                break

            lines = data.splitlines()
            print(f"Reiceived: {lines[-1]}")

        except Exception as e:
            print(f"Error receiving data: {e}")
            break

if __name__ == "__main__":
    main()

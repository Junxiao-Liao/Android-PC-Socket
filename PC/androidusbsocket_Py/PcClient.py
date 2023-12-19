import socket
import subprocess
import threading

class PcClient:
   def __init__(self, host="127.0.0.1", port=8000):
       self.host = host
       self.port = port
       self.socket = None
       self.received_message = ""

   def connect(self):
       try:
           subprocess.run(["adb", "forward", "tcp:8000", "tcp:9000"])
           self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
           self.socket.connect((self.host, self.port))
           self.start_receive_thread()
           print("Connected to Android device.")
       except Exception as e:
           print(f"Error connecting: {e}")

   def start_receive_thread(self):
       receive_thread = threading.Thread(target=self.receive_data)
       receive_thread.daemon = True
       receive_thread.start()

   def receive_data(self):
       while True:
           try:
               data = self.socket.recv(1024).decode("utf-8")
               if not data:
                   print("Connection closed")
                   break

               lines = data.splitlines()
               self.received_message = lines[-1] # Store the last line
               print(f"Received: {self.received_message}")

           except Exception as e:
               print(f"Error receiving data: {e}")
               break

   def send(self, message):
       try:
           self.socket.sendall(message.encode("utf-8"))
           print(f"Sent: {message}")
       except Exception as e:
           print(f"Error sending data: {e}")

if __name__ == "__main__":
   client = PcClient()
   client.connect()
   client.start_receive_thread()

   while True:
       send_msg = input("Send: ")
       client.send(send_msg)
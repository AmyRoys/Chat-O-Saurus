# Chat-O-Saurus
Client-Server Chat GPT where the output is checked for credibility before being outputted to the client.

# How to run

- Run Server
- Create an instance of client file

# Main Files (Server.java | Client.java)
Implements 
- Threads 
- Sockets
- Message Queues

Client is able to send a prompt to a server where the prompt is sent to the ChatGPT API and the response is received. This repsonse is then fed into the ClaimBuster API where we can check if the output of ChatGpt needs to be fact-checked. We use threads to handle multiple clients and sockets to allow the clients to connect to the sever. Here we use message queues to output the resposnes of the ChatGPT API and ClaimBuster API to all clients connected to the sever.





# Log FIles (server_log.java | client_log.java)
Implements 
- Threads 
- Sockets 
- Pipes

Client is able to send a prompt to a server where the prompt is sent to the ChatGPT API and the response is received. This repsonse is then fed into the ClaimBuster API where we can check if the output of ChatGpt needs to be fact-checked. We use threads to handle multiple clients and sockets to allow the clients to connect to the sever. Pipes are used so the client thread can communicate with a logger thread where the outputs are written to a text file 'log.txt'. Here we can keep track of what prompts have been asked and can see what the credibility of the outputs are. These files show that the api is working as designed and being outputted to the clients correctly.

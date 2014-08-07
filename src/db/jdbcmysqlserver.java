package db;

import java.net.Socket;
import java.net.ServerSocket;

public class jdbcmysqlserver {
	
	public static void main(String[] args) throws Exception {

        // create socket
        int port = 4444;
        int QueryType = 9;
        ServerSocket serverSocket = new ServerSocket(port);
        System.err.println("Started server on port " + port);

        // repeatedly wait for connections, and process
        while (true) {

            // a "blocking" call which waits until a connection is requested
            Socket clientSocket = serverSocket.accept();
            System.err.println("Accepted connection from client");

            // open up IO streams
            In  in  = new In (clientSocket);
            Out out = new Out(clientSocket);

            jdbcmysql test = new jdbcmysql(out);
            
            // waits for data and reads it in until connection dies
            // readLine() blocks until the server receives a new line from client
            String s;
            while ((s = in.readLine()) != null) {
                System.out.println("Server receive: " + s);
                QueryType = test.CheckQueryType(s);
                if(jdbcmysql.SELECT_TYPE == QueryType)
                	test.SelectTable(s);
                else if (jdbcmysql.DELETE_TYPE == QueryType)
                	test.DeleteRecord(s);
                else if (jdbcmysql.INSERT_TYPE == QueryType)
                	test.InsertRecord(s);
                else if (jdbcmysql.UPDATE_TYPE == QueryType)
                	test.UpdateRecord(s);
                else if (jdbcmysql.SELECTCOUNT_TYPE == QueryType)
                	test.SelectCount(s);
            }
            
            // close IO streams, then socket
            System.err.println("Closing connection with client");
            out.close();
            in.close();
            clientSocket.close();
            serverSocket.close();
        }
    }
	

}

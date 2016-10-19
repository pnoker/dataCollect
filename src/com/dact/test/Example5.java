package com.dact.test;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
class Example5 extends Thread {
  volatile boolean stop = false;
  volatile ServerSocket socket;
  public static void main( String args[] ) throws Exception {
    Example5 thread = new Example5();
   System.out.println( "Starting thread..." );
   thread.start();
   Thread.sleep( 3000 );
   System.out.println( "Asking thread to stop..." );
   thread.stop = true;
   thread.socket.close();
   Thread.sleep( 3000 );
   System.out.println( "Stopping application..." );
   //System.exit( 0 );
  }
  public void run() {
    try {
      socket = new ServerSocket(7856);
    } catch ( IOException e ) {
     System.out.println( "Could not create the socket..." );
      return;
    }
    while ( !stop ) {
     System.out.println( "Waiting for connection..." );
      try {
       Socket sock = socket.accept();
      } catch ( IOException e ) {
      System.out.println( "accept() failed or interrupted..." );
      }
    }
   System.out.println( "Thread exiting under request..." );
  }
}
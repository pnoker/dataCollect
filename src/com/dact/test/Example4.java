package com.dact.test;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Example4 extends Thread {

  public static void main( String args[] ) throws Exception {

    Example4 thread = new Example4();

   System.out.println( "Starting thread..." );

   thread.start();

   Thread.sleep( 3000 );

   System.out.println( "Interrupting thread..." );

   thread.interrupt();

   Thread.sleep( 3000 );

   System.out.println( "Stopping application..." );

   //System.exit( 0 );

  }

  public void run() {

   ServerSocket socket;

    try {

      socket = new ServerSocket(7856);

    } catch ( IOException e ) {

     System.out.println( "Could not create the socket..." );

      return;

    }

    while ( true ) {

     System.out.println( "Waiting for connection..." );

      try {

       Socket sock = socket.accept();

      } catch ( IOException e ) {

      System.out.println( "accept() failed or interrupted..." );

      }

    }

  }

}
package com.dact.test;
class Example1 extends Thread {
            boolean stop=false;
            public static void main( String args[] ) throws Exception {
            Example1 thread = new Example1();
            System.out.println( "Starting thread..." );
            thread.start();
            Thread.sleep( 3000 );
            System.out.println( "Interrupting thread..." );
            thread.interrupt();
            Thread.sleep( 3000 );
            System.out.println("Stopping application..." );
            //System.exit(0);
            }
            public void run() {
            while(!stop){
            System.out.println( "Thread is running..." );
            long time = System.currentTimeMillis();
            while((System.currentTimeMillis()-time < 1000)) {
            }
            }
            System.out.println("Thread exiting under request..." );
            }
            }
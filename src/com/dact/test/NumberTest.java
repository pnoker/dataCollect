package com.dact.test;
public class NumberTest
{
    public static void main(String[] args)
    {
        NumberHolder numberHolder = new NumberHolder();
        
        Thread t1 = new IncreaseThread(numberHolder);
        Thread t2 = new DecreaseThread(numberHolder);
                
        t1.start();
        t2.start();
    }

}
package edu.jsu.mcis.cs310;

import org.json.simple.*;

public class Main {

    public static void main(String[] args) {
        
        Database db = new Database("cs310_p2_user", "P2!user", "localhost");
        
        if (db.isConnected()){
            System.err.println("Connected Successfully!");
        }
        int test = db.register(2, 1, 20015);
        System.err.println(test);
    }
    
}
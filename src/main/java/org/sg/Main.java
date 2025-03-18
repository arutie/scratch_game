package org.sg;

import org.sg.utils.ConvertUtils;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            String configFile = "";
            int betAmount = 100;
            int idx = 0;
            while(idx < args.length) {
                if(args[idx].equalsIgnoreCase("--config")) {
                    configFile = args[idx+1];
                    idx++;
                } else if(args[idx].equalsIgnoreCase("--betting-amount")) {
                    betAmount = ConvertUtils.toInt(args[idx + 1], 0);
                    idx++;
                }
                idx ++;
            }

            if (configFile == null || configFile.length() == 0) {
                configFile = (new File("")).getAbsolutePath() + "/resources/config.json";
            }
//            System.out.println("config file: " + configFile);
//            System.out.println("betAmount: " + betAmount);
            ScratchGame scratchGame = new ScratchGame(configFile);

            scratchGame.start(betAmount);


        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }
}
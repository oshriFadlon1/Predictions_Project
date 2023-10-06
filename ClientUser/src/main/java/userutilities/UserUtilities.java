package userutilities;

import range.Range;

import java.util.Random;

public class UserUtilities {
    public static boolean isInteger(String text){
        try{
            Integer.parseInt(text);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    public static boolean isFloat(String value) {
        try{
            Float.parseFloat(value);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    public static float initializeRandomFloat(float from, float to){
        Random random = new Random();
        float randomNumber = from + random.nextFloat() * (to - from);
        return randomNumber;
    }

    public static String initializeRandomString(){
        int min = 1;
        int max = 50;
        Random random = new Random();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!?,_-(). ";
        int randomStrLength = random.nextInt(max - min + 1) + min;
        String randomInitializedString = "";
        for(int i = 0; i < randomStrLength; i++){
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomInitializedString += randomChar;
        }

        return randomInitializedString;
    }

    public static boolean initializeRandomBoolean() {
        Random random = new Random();
        boolean randomBoolean = random.nextBoolean();
        return randomBoolean;
    }
}

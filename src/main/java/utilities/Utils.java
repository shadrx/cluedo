package utilities;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

/**
 * A collection of utility methods.
 */
public class Utils {
    private static final Random random = new Random();

    /**
     * Returns a random enum value from the given class.
     *
     * @param enumClass the class containing enum constants
     * @return a random enum constant from the given class
     */
    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass){
        int x = random.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
    }

    /**
     * Loads an image from the resources directory using the given path.
     *
     * @param imagePath the path to where the image is located.
     * @return buffered image or null if no image was found
     */
    public static BufferedImage loadImage(String imagePath){
        URL url = Utils.class.getClassLoader().getResource(imagePath);
        if(url == null) return null;

        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

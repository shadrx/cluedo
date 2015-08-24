package utilities;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A collection of utility methods.
 */
public class Utils {
    private static final Random random = new Random();

    private static final Map<String, BufferedImage> _imageCache = new HashMap<>();

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
        BufferedImage image = _imageCache.get(imagePath);

        if (image == null) {

            URL url = ClassLoader.getSystemClassLoader().getResource(imagePath);
            if (url == null) return null;
            try {
                image = ImageIO.read(url);
                _imageCache.put(imagePath, image);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return image;
    }

}

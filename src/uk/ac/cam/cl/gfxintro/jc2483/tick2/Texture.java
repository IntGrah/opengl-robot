package uk.ac.cam.cl.gfxintro.jc2483.tick2;

import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Texture {

  int texId;

  public int getTexId() {
    return texId;
  }

  public int load(String filename) {
    ByteBuffer buffer = null;
    int tWidth = 0;
    int tHeight = 0;

    // Link the PNG decoder to this stream
    BufferedImage image = loadImageFromFile(filename);

    // Get the width and height of the texture
    tWidth = image.getWidth();
    tHeight = image.getHeight();

    // Decode the PNG file in a ByteBuffer
    buffer = ByteBuffer.allocateDirect(4 * image.getWidth() * image.getHeight());
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        int argb = image.getRGB(x, y);
        buffer.put((x + y * image.getWidth()) * 4 + 0, (byte) ((argb >> 16) & 0xFF));
        buffer.put((x + y * image.getWidth()) * 4 + 1, (byte) ((argb >> 8) & 0xFF));
        buffer.put((x + y * image.getWidth()) * 4 + 2, (byte) ((argb >> 0) & 0xFF));
        buffer.put((x + y * image.getWidth()) * 4 + 3, (byte) ((argb >> 24) & 0xFF));
      }
    }
    buffer.flip();
    buffer.limit(buffer.capacity());

    // Create a new texture object in memory and bind it
    texId = GL11.glGenTextures();
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

    // All RGB bytes are aligned to each other and each component is 1 byte
    GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

    // Upload the texture data.
    // Note that the internal texture format is sRGB since the shading is in the linear colour domain.
    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL21.GL_SRGB8, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);


    // Setup what to do when the texture has to be scaled
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0); // Unbind texture from the current context

    return texId;
  }
  
  public int loadCubemap(String[] filenames) {
    // Create a new texture object in memory and bind it
    texId = GL11.glGenTextures();
    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId);
    
    // Load each face of the cube one by one
    for(int i = 0; i<filenames.length; i++) {
    	ByteBuffer buffer;
        int tWidth = 0;
        int tHeight = 0;

        // Link the PNG decoder to this stream
        BufferedImage image = loadImageFromFile(filenames[i]);

        // Get the width and height of the texture
        tWidth = image.getWidth();
        tHeight = image.getHeight();

        // Decode the PNG file in a ByteBuffer
        buffer = ByteBuffer.allocateDirect(4 * image.getWidth() * image.getHeight());
        for (int x = 0; x < image.getWidth(); x++) {
          for (int y = 0; y < image.getHeight(); y++) {
            int argb = image.getRGB(x, y);
            buffer.put((x + y * image.getWidth()) * 4 + 0, (byte) ((argb >> 16) & 0xFF));
            buffer.put((x + y * image.getWidth()) * 4 + 1, (byte) ((argb >> 8) & 0xFF));
            buffer.put((x + y * image.getWidth()) * 4 + 2, (byte) ((argb >> 0) & 0xFF));
            buffer.put((x + y * image.getWidth()) * 4 + 3, (byte) ((argb >> 24) & 0xFF));
          }
        }
        buffer.flip();
        buffer.limit(buffer.capacity());
        // All RGB bytes are aligned to each other and each component is 1 byte
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        // Upload the texture data.
        // Note that the internal texture format is sRGB since the shading is in the linear colour domain.
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL21.GL_SRGB8, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
    }
    
    // Setup what to do when the texture has to be scaled
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);

    GL30.glGenerateMipmap(GL13.GL_TEXTURE_CUBE_MAP);

    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0); // Unbind texture from the current context
    
	return texId;
  }
  
  public void bindCubemap() {
	  GL13.glActiveTexture(GL13.GL_TEXTURE1);
	  GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId );
  }
  
  public void unBindCubemap() {
	  GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0 );
  }

  public void bindTexture() {
	  GL13.glActiveTexture(GL13.GL_TEXTURE0);
	  GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
  }
  
  public void unBindTexture() {
	  GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0 );
  }
  
  private static BufferedImage loadImageFromFile(String path) {
    try {
      return ImageIO.read(new File(path));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}

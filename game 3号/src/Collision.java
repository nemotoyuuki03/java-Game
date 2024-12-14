// Collision.java
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Collision
{
    private List<Rectangle> collidableAreas = new ArrayList<>();

    public void loadCollidableAreas(String csvFilePath, int tileWidth, int tileHeight)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) 
        {
            String line;
            int row = 0;

            while ((line = br.readLine()) != null) 
            {
                String[] values = line.split(",");
                for (int col = 0; col < values.length; col++) 
                {
                    if (Integer.parseInt(values[col]) == 1) 
                    { // 地面
                        Rectangle rect = new Rectangle(col * tileWidth, row * tileHeight, tileWidth, tileHeight);
                        collidableAreas.add(rect);
                    } else if (Integer.parseInt(values[col]) == 2) 
                    { // 他のオブジェクト
                        Rectangle rect = new Rectangle(col * tileWidth, row * tileHeight, tileWidth, tileHeight);
                        collidableAreas.add(rect);
                    }
                }
                row++;
            }
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public List<Rectangle> getCollidableAreas() {
        return collidableAreas;
    }
}

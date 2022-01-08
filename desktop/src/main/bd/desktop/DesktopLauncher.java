package main.bd.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import main.bd.BuzzDungeon;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Buzz Dungeon";
        config.fullscreen = false;
        config.width = 1920;
        config.height = 1080;
        config.addIcon("icon16.png", Files.FileType.Internal);
        config.addIcon("icon32.png", Files.FileType.Internal);
        config.addIcon("icon128.png", Files.FileType.Internal);
        new LwjglApplication(new BuzzDungeon(), config);
    }
}

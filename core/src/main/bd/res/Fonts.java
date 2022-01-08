package main.bd.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Fonts {
    public static BitmapFont font;
    public static BitmapFont font32;
    public static BitmapFont font32bold;
    public static BitmapFont font32boldoutline;
    public static BitmapFont font64title;

    public static void init() {
        font = new BitmapFont(Gdx.files.internal("fonts/16.fnt"));
        font32 = new BitmapFont(Gdx.files.internal("fonts/alagard32.fnt"));
        font32bold = new BitmapFont(Gdx.files.internal("fonts/alagard32bold.fnt"));
        font32boldoutline = new BitmapFont(Gdx.files.internal("fonts/alagard32boldoutline.fnt"));
        font64title = new BitmapFont(Gdx.files.internal("fonts/alagard64title.fnt"));
    }

    public static void dispose() {
        font.dispose();
        font32.dispose();
        font32bold.dispose();
        font32boldoutline.dispose();
        font64title.dispose();
    }
}

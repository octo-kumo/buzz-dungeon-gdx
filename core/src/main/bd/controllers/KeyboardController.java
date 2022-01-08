package main.bd.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class KeyboardController implements InputProcessor {
    public boolean left, right, up, down, mright, mleft;

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) // switch code base on the variable keycode
        {
            case Input.Keys.A:    // if keycode is the same as Keys.LEFT a.k.a 21
                left = true;    // do this
                keyProcessed = true;// we have reacted to a keypress
                break;
            case Input.Keys.D:    // if keycode is the same as Keys.LEFT a.k.a 22
                right = true;    // do this
                keyProcessed = true;// we have reacted to a keypress
                break;
            case Input.Keys.W:        // if keycode is the same as Keys.LEFT a.k.a 19
                up = true;        // do this
                keyProcessed = true;// we have reacted to a keypress
                break;
            case Input.Keys.S:    // if keycode is the same as Keys.LEFT a.k.a 20
                down = true;    // do this
                keyProcessed = true;// we have reacted to a keypress
        }
        return keyProcessed;    //  return our peyProcessed flag
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) // switch code base on the variable keycode
        {
            case Input.Keys.A:    // if keycode is the same as Keys.LEFT a.k.a 21
                left = false;    // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Input.Keys.D:    // if keycode is the same as Keys.LEFT a.k.a 22
                right = false;    // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Input.Keys.W:        // if keycode is the same as Keys.LEFT a.k.a 19
                up = false;        // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Input.Keys.S:    // if keycode is the same as Keys.LEFT a.k.a 20
                down = false;    // do this
                keyProcessed = true;    // we have reacted to a keypress
        }
        return keyProcessed;    //  return our peyProcessed flag
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) return mleft = true;
        else if (button == Input.Buttons.RIGHT) return mright = true;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) return !(mleft = false);
        else if (button == Input.Buttons.RIGHT) return !(mright = false);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
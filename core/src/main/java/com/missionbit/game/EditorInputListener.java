package com.missionbit.game;

import com.badlogic.gdx.Input;

public class EditorInputListener implements Input.TextInputListener {

    private String text;
    @Override
    public void input (String text) {
        this.text = text;
    }

    @Override
    public void canceled () {
    }

    public String getText() {
        return text;
    }

    public void setText(String t){
        text = t;
    }
}

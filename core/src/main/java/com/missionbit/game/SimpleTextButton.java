package com.missionbit.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class SimpleTextButton {
    public static BitmapFont font = new BitmapFont();
    protected String text;
    protected Vector2 position;
    protected Color backgroundColor;
    protected Color foregroundColor;
    protected Color highlightColor;
    protected boolean selected = true;

    public static final int HEIGHT = 60;
    public static final int WIDTH = 300;


    public SimpleTextButton(String text, float x, float y){
        backgroundColor = new Color(0, 0, 0, 1);
        foregroundColor = new Color(1, 1, 1, 1);
        highlightColor = new Color(1, 0, 0, 1);

        selected = false;

        position = new Vector2(x, y);
        this.text = text;

    }

    public void drawBackground( ShapeRenderer renderer){

        if(selected){
            renderer.setColor(highlightColor);
        }
        else {
            renderer.setColor(backgroundColor);
        }
        renderer.rect(position.x, position.y, WIDTH, HEIGHT);

    }

    public void drawText(SpriteBatch b){
        font.draw(b, text, position.x + 10, position.y + 30);
    }

    public boolean handleClick(Vector3 touchPos){
        Rectangle.tmp.x = position.x;
        Rectangle.tmp.y = position.y;
        Rectangle.tmp.width = WIDTH;
        Rectangle.tmp.height = HEIGHT;
        return Rectangle.tmp.contains(touchPos.x, touchPos.y);
    }

}

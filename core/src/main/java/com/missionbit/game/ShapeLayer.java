package com.missionbit.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.BufferedWriter;
import java.util.ArrayList;

public class ShapeLayer {

    protected String title;
    protected SimpleTextButton button;
    protected String shapeType;
    protected ArrayList<Shape2D> shapeItems = new ArrayList<Shape2D>();
    protected ArrayList<Vector2> polyClicks = new ArrayList<Vector2>();

    protected Vector2 lastClick;
    protected Vector3 drawPos = new Vector3();

    public ShapeLayer(String title, String type){
        this.title = title;
        shapeType = type;
        button = new SimpleTextButton(title, 0, 0);

    }

    public void handleClick(Vector3 touchPos){

        if(lastClick == null){
            lastClick = new Vector2(touchPos.x, touchPos.y);
            polyClicks.add(new Vector2(touchPos.x, touchPos.y));
        }
        else{
            if(shapeType.equals("rect")) {
                completeCurrent(touchPos.x, touchPos.y);
            }
            if(shapeType.equals("poly")){
                polyClicks.add(new Vector2(touchPos.x, touchPos.y));

            }
        }
    }

    public void draw(Camera c, ShapeRenderer renderer){
        drawPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        c.unproject(drawPos);

        renderer.setColor(1, 0, 0, 1);
        for(Shape2D shape: shapeItems){
            if(shapeType == "rect") {
                Rectangle r = (Rectangle) shape;
                renderer.rect(r.x, r.y, r.width, r.height);
            }
            else if (shapeType == "poly"){
                Polygon p = (Polygon) shape;
                renderer.polygon(p.getVertices());
            }
        }
        if(shapeType.equals("rect") && lastClick != null){
            renderer.rect(lastClick.x, lastClick.y, drawPos.x - lastClick.x, drawPos.y - lastClick.y);
        }
        else if(shapeType.equals("poly") && !polyClicks.isEmpty()){

            polyClicks.add(new Vector2(drawPos.x, drawPos.y));

            for(int i = 0; i < polyClicks.size() - 1; i ++){
                renderer.line(polyClicks.get(i).x, polyClicks.get(i).y, polyClicks.get(i + 1).x, polyClicks.get(i + 1).y);
            }

            polyClicks.remove(polyClicks.size() - 1);
        }
    }

    public void completeCurrent(float x, float y){
        if(shapeType.equals("rect")  && lastClick != null) {
            float rectX = lastClick.x;
            float rectY = lastClick.y;
            float rectW = x - lastClick.x;
            float rectH = y - lastClick.y;

            if(rectW < 0){
                rectX += rectW;
                rectW *= -1;
            }
            if(rectH < 0){
                rectY += rectH;
                rectH *= -1;
            }
            shapeItems.add(new Rectangle(rectX, rectY, rectW, rectH));
            //shapeItems.add(new Rectangle(lastClick.x, lastClick.y, x - lastClick.x, y - lastClick.y));
            clearCurrent();
        }
        else{
            float[] vertices = new float[polyClicks.size() * 2];
            for(int i = 0, v = 0; i < polyClicks.size(); i++, v+=2){
                Vector2 vtmp = polyClicks.get(i);
                vertices[v] = vtmp.x;
                vertices[v+1] = vtmp.y;
            }
            Polygon p = new Polygon(vertices);
            shapeItems.add(p);
            clearCurrent();
        }
    }

    public void clearCurrent(){
        lastClick = null;
        polyClicks.clear();
    }

    public void printJava(){

        System.out.println("float[][] " + title + " = new float[][]{");
        for(Shape2D s : shapeItems) {
            System.out.print("{");
            if(shapeType.equals("rect")) {
                Rectangle r = (Rectangle)s;
                System.out.print(r.x + ", " + r.y + ", " + r.width + ", " + r.height);
            }
            else if(shapeType.equals("poly")){
                Polygon p = (Polygon)s;
                float[] tmpvert = p.getVertices();
                for(int pv = 0; pv < tmpvert.length; pv++){
                    System.out.print(tmpvert[pv] + "f, ");
                }
            }
            System.out.println("},");
        }
        System.out.print("};\n\n");

    }

    public String getJson(){
        Json json = new Json();
        String j = "";//
        json.writeObjectStart();
        // json.prettyPrint(json.toJson(shapeItems));
        json.writeObjectEnd();

        return json.prettyPrint(json.toString());
    }

}

package com.missionbit.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;


import com.badlogic.gdx.Input;


import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.HashMap;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class CoordinateEditor extends ApplicationAdapter {

    private OrthographicCamera camera;
    private Texture background;
    private SpriteBatch batch;
    private Vector2 cameraStart;
    private Vector2 cameraOffset;
    private ShapeRenderer renderer;
    private HashMap<String, ShapeLayer> items;

    private ShapeLayer selectedLayer;
    private SimpleTextButton selectedButton;
    private SimpleTextButton addRectButton;
    private SimpleTextButton addPolyButton;

    private EditorInputListener inputListener = new EditorInputListener();
    private EditorInputListener polyInputListener = new EditorInputListener();

    public static final int VIEWPORT_WIDTH = 960;
    public static final int VIEWPORT_HEIGHT = 540;

    @Override
    public void create(){
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH / 1.5f, VIEWPORT_HEIGHT / 1.5f);

        cameraStart = new Vector2(Gdx.graphics.getWidth() / 2.0f,Gdx.graphics.getHeight() / 2.0f);
        cameraOffset = new Vector2(0, 0);

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        background = new Texture(Gdx.files.internal("images/bridge.jpg"));

        items = new HashMap<String, ShapeLayer>();

        addRectButton = new SimpleTextButton("Add Rectangles", Gdx.graphics.getWidth() - 300, Gdx.graphics.getHeight() - 50);
        addPolyButton = new SimpleTextButton("Add Polygons", Gdx.graphics.getWidth() - 300, Gdx.graphics.getHeight() - 100);

    }

    @Override
    public void render(){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.x = cameraStart.x + cameraOffset.x;
        camera.position.y = cameraStart.y + cameraOffset.y;

        //Set up our camera
        camera.update();


        if(Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

            camera.unproject(touchPos);

            boolean addFlag = addRectButton.handleClick(touchPos);
            boolean addPolyFlag = addPolyButton.handleClick(touchPos);
            if(addFlag){
                addRectButton.selected = true;
                selectedButton = addRectButton;
                for(ShapeLayer l : items.values()) {
                    l.button.selected = false;
                }

                Gdx.input.getTextInput(inputListener, "Layer name", "Layer name", "Layer name");
            }
            else if(addPolyFlag){
                addPolyButton.selected = true;
                selectedButton = addPolyButton;
                for(ShapeLayer l : items.values()) {
                    l.button.selected = false;
                }

                Gdx.input.getTextInput(polyInputListener, "Layer name", "Layer name", "Layer name");
            }
            else {
                boolean selectFlag = false;
                for (ShapeLayer l : items.values()) {
                    if (l.button.handleClick(touchPos)) {
                        selectedLayer.button.selected = false;

                        selectedButton = l.button;
                        l.button.selected = true;
                        selectedLayer = l;
                        selectFlag = true;
                    }

                }

                if (selectedLayer != null && !selectFlag) {
                    selectedLayer.handleClick(touchPos);
                }
            }

        }
        if(inputListener.getText() != null) {
            String layerTitle = inputListener.getText();

            inputListener.setText(null);
            selectedButton.selected = false;
            addRectButton.selected = false;

            ShapeLayer l = new ShapeLayer(layerTitle, "rect");
            items.put(l.title, l);
            l.button.position.x = addPolyButton.position.x;
            l.button.position.y = addPolyButton.position.y - (items.size() * 60);
            l.button.selected = true;
            selectedButton = l.button;
            selectedLayer = l;

        }
        else if(polyInputListener.getText() != null){
            String layerTitle = polyInputListener.getText();

            polyInputListener.setText(null);
            selectedButton.selected = false;
            addRectButton.selected = false;

            ShapeLayer l = new ShapeLayer(layerTitle, "poly");
            items.put(l.title, l);
            l.button.position.x = addPolyButton.position.x;
            l.button.position.y = addPolyButton.position.y - (items.size() * 60);
            l.button.selected = true;
            selectedButton = l.button;
            selectedLayer = l;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            cameraOffset.y += 5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            cameraOffset.y -= 5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            cameraOffset.x -= 5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            cameraOffset.x += 5;
        }


        if(Gdx.input.isKeyJustPressed(Input.Keys.J)) {

            System.out.println("/** BEGIN JAVA CODE EXPORT**/\n");
            for(ShapeLayer l : items.values()){
                l.printJava();
            }
            System.out.println("/** END JAVA CODE EXPORT**/\n");
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.K)) {

            for(ShapeLayer l : items.values()){
                System.out.println(l.getJson());
            }

        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && selectedLayer != null) {
            selectedLayer.clearCurrent();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && selectedLayer != null){
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

            camera.unproject(touchPos);
            selectedLayer.completeCurrent(touchPos.x, touchPos.y);
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background,0, 0, background.getWidth(), background.getHeight());

        batch.end();

        renderer.setProjectionMatrix(camera.combined);
        if(selectedLayer != null){
            renderer.begin(ShapeRenderer.ShapeType.Line);
            selectedLayer.draw(camera, renderer);
            renderer.end();
        }

        camera.setToOrtho(false, VIEWPORT_WIDTH , VIEWPORT_HEIGHT );

        camera.position.x = cameraStart.x;
        camera.position.y = cameraStart.y;
        camera.update();
        renderer.setProjectionMatrix(camera.combined);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        batch.setProjectionMatrix(camera.combined);
        addRectButton.drawBackground(renderer);
        addPolyButton.drawBackground(renderer);
        for(ShapeLayer l: items.values()){
            l.button.drawBackground(renderer);
        }
        renderer.end();





        batch.begin();
        addRectButton.drawText(batch);
        addPolyButton.drawText(batch);
        for(ShapeLayer l: items.values()){
            l.button.drawText(batch);
        }

        batch.end();
    }

    @Override
    public void dispose(){

    }

    @Override
    public void resize(int width, int height){
        Gdx.graphics.setWindowedMode(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }
}
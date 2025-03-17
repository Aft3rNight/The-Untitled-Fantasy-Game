package io.github.TheUntitledFantasyGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
    private final Menu game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private boolean transitionCompleted = false;
    private float transitionTimer = 0f;
    private static final float TRANSITION_TIMEOUT = 2.0f;

    public GameScreen(Menu game) {
        this.game = game;
        batch = game.getBatch();
        font = game.getFont();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE && transitionCompleted && game.getTransition().isActive()) {
                    System.out.println("ESC pressed - returning to menu");
                    game.returnToMenu();
                    return true;
                }
                return false;
            }
        });

        transitionCompleted = false;
        transitionTimer = 0f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, "The game screen is empty for now, press ESC to return to the menu.", 100, 150);
        batch.end();

        // Проверка завершения анимации без потока
        if (!transitionCompleted) {
            if (game.getTransition().isActive()) {
                transitionCompleted = true;
                System.out.println("Transition completed, ESC now active");
            } else {
                transitionTimer += delta;
                if (transitionTimer >= TRANSITION_TIMEOUT) {
                    transitionCompleted = true;
                    System.out.println("Transition timeout, ESC now active");
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        System.out.println("Resized 3.0");
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null); // Сбрасываем обработчик ввода
    }

    @Override
    public void dispose() {}
}


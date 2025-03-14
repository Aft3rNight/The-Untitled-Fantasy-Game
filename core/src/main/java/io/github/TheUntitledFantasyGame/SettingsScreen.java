package io.github.TheUntitledFantasyGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsScreen implements Screen {
    private final Main game;
    private Stage stage;

    public SettingsScreen(Main game) {
        this.game = game;

        // Создаем новый Stage для этого экрана
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Создаем таблицу для организации UI
        Table table = new Table();
        table.setFillParent(true);

        // Создаем метку с текстом
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.getFont();
        Label settingsLabel = new Label("Настройки (Нажмите ESC для возврата)", labelStyle);

        // Добавляем метку в таблицу
        table.add(settingsLabel).expand().center();

        // Добавляем таблицу на stage
        stage.addActor(table);
    }

    @Override
    public void show() {
        // Метод вызывается при отображении экрана
    }

    @Override
    public void render(float delta) {
        // Обработка нажатия ESC для возврата в главное меню
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.returnToMenu();
            return;
        }

        // Очистка экрана
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновление и отрисовка stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Обновляем размер viewport при изменении размера окна
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Метод вызывается при приостановке игры
    }

    @Override
    public void resume() {
        // Метод вызывается при возобновлении игры
    }

    @Override
    public void hide() {
        // Метод вызывается когда экран перестает быть активным
    }

    @Override
    public void dispose() {
        // Освобождаем ресурсы
        stage.dispose();
    }
}


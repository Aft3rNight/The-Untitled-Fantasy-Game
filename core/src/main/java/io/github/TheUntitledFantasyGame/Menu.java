package io.github.TheUntitledFantasyGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Menu extends Game {

    private Skin skin;
    private TextButton play_button, settings_button, quit_button;
    private Stage stage;
    private SpriteBatch batch;
    private FreeTypeFontGenerator generator;
    private ScreenTransition transition;
    private Screen nextScreen;
    private Music backgroundMusic;
    private BitmapFont font;
    private Texture backgroundTexture;
    private Texture logoTexture;
    private GameSettings gameSettings;

    @Override
    public void create() {

        // Загружаем и устанавливаем кастомный курсор

        try {
            // Загружаем изображение курсора
            Pixmap originalCursor = new Pixmap(Gdx.files.internal("sprites/custom_cursor.png")); // AFTERNIGHT COMMIT TEXTURES (Afternight: No.)
            int scaleFactor = 4;
            int newWidth = originalCursor.getWidth() * scaleFactor;
            int newHeight = originalCursor.getHeight() * scaleFactor;

            // Создаем новый Pixmap для масштабированного курсора
            Pixmap scaledCursor = new Pixmap(newWidth, newHeight, Pixmap.Format.RGBA8888);

            // Явно указываем прозрачность
            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    // Получаем цвет соответствующего пикселя из оригинального изображения
                    int srcX = x / scaleFactor;
                    int srcY = y / scaleFactor;
                    int color = originalCursor.getPixel(srcX, srcY);

                    // Устанавливаем тот же цвет в увеличенном изображении
                    scaledCursor.drawPixel(x, y, color);
                }
            }
            // Устанавливаем курсор с явным указанием горячей точки (0,0 - левый верхний угол)
            Gdx.graphics.setCursor(Gdx.graphics.newCursor(scaledCursor, 0, 0));

            // Выводим отладочную информацию
            System.out.println("Установлен кастомный курсор размером " + newWidth + "x" + newHeight);

            // Освобождаем ресурсы
            originalCursor.dispose();
            scaledCursor.dispose();
        } catch (Exception e) {
            System.err.println("Ошибка при установке курсора: " + e.getMessage());
            e.printStackTrace();
        }

        gameSettings = new GameSettings(this);
        gameSettings.loadSettings();

        batch = new SpriteBatch();
        transition = new ScreenTransition(1.0f);

        // Load background and logo
        backgroundTexture = new Texture(Gdx.files.internal("sprites/Background.png"));
        logoTexture = new Texture(Gdx.files.internal("sprites/Logo.UTLL.png"));

        // Load and configure background music
        loadBackgroundMusic();

        // Initialize skin and fonts
        skin = new Skin();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 20;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        font = generator.generateFont(parameter);
        skin.add("default-font", font);

        loadButtonTextures();

        play_button = createButtonWithStates("play_button", "play_buttonH", "play_buttonP");
        settings_button = createButtonWithStates("settings_button", "settings_buttonH", "settings_buttonP");
        quit_button = createButtonWithStates("quit_button", "quit_buttonH", "quit_buttonP");

        play_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGameScreenTransition();
            }
        });

        settings_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startSettingsScreenTransition();
            }
        });

        quit_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        gameSettings.applySettings(); // Apply settings immediately
        resetStage();
    }

    private void loadButtonTextures() {
        skin.add("play_button", new Texture("sprites/play_button.png"));
        skin.add("settings_button", new Texture("sprites/settings_button.png"));
        skin.add("quit_button", new Texture("sprites/quit_button.png"));

        skin.add("play_buttonH", new Texture("sprites/play_buttonH.png")); // AFTERNIGHT COMMIT TEXTURES
        skin.add("settings_buttonH", new Texture("sprites/settings_buttonH.png")); // AFTERNIGHT COMMIT TEXTURES
        skin.add("quit_buttonH", new Texture("sprites/quit_buttonH.png")); // AFTERNIGHT COMMIT TEXTURES

        skin.add("play_buttonP", new Texture("sprites/play_buttonP.png")); // AFTERNIGHT COMMIT TEXTURES
        skin.add("settings_buttonP", new Texture("sprites/settings_buttonP.png")); // AFTERNIGHT COMMIT TEXTURES
        skin.add("quit_buttonP", new Texture("sprites/quit_buttonP.png")); // AFTERNIGHT COMMIT TEXTURES
    }

    public void resetStage() {
        if (stage != null) {
            stage.dispose();
        }

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Image background = new Image(backgroundTexture);
        background.setSize(gameSettings.getResolutionWidth(), gameSettings.getResolutionHeight());
        System.out.println("Ssaasas" + gameSettings.getResolutionWidth() + gameSettings.getResolutionHeight());
        stage.addActor(background);

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Image logo = new Image(logoTexture);
        float logoWidth = Math.min(660, Gdx.graphics.getWidth() * 0.6f);
        float logoHeight = logoWidth * logoTexture.getHeight() / logoTexture.getWidth();

        Table logoTable = new Table();
        logoTable.add(logo).width(logoWidth).height(logoHeight);

        Table buttonTable = new Table();
        buttonTable.add(play_button).padBottom(20).width(600).height(150).row();
        buttonTable.add(settings_button).padBottom(20).width(600).height(150).row();
        buttonTable.add(quit_button).width(600).height(150);

        mainTable.add(logoTable).expandX().padBottom(0).row();
        mainTable.add(buttonTable).expand().align(Align.center).padBottom(100);

        stage.addActor(mainTable);
    }

    private void loadBackgroundMusic() {
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/test_sound.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f);
            backgroundMusic.play();
        } catch (Exception e) {
            System.err.println("Failed to load background music: " + e.getMessage());
        }
    }

    public void startGameScreenTransition() {
        disableMenuButtons();
        nextScreen = new GameScreen(this);
        transition.start(ScreenTransition.TransitionType.FADE_OUT, () -> {
            setScreen(nextScreen);
            transition.start(ScreenTransition.TransitionType.FADE_IN, null);
        });
    }

    public void startSettingsScreenTransition() {
        disableMenuButtons();
        nextScreen = new SettingsScreen(this);
        transition.start(ScreenTransition.TransitionType.FADE_OUT, () -> {
            setScreen(nextScreen);
            transition.start(ScreenTransition.TransitionType.FADE_IN, null);
        });
    }

    private void disableMenuButtons() {
        play_button.setDisabled(true);
        settings_button.setDisabled(true);
        quit_button.setDisabled(true);
    }

    private void enableMenuButtons() {
        play_button.setDisabled(false);
        settings_button.setDisabled(false);
        quit_button.setDisabled(false);
    }

    public void returnToMenu() {
        transition.start(ScreenTransition.TransitionType.FADE_OUT, () -> {
            setScreen(null);
            resetStage();
            enableMenuButtons();
            transition.start(ScreenTransition.TransitionType.FADE_IN, null);
        });
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (getScreen() != null) {
            super.render();
        } else {
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
        }

        transition.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        // Проверяем, разрешено ли изменение размера
        boolean resizingEnabled = false;
        if (!resizingEnabled) {
            // Если изменение размера не разрешено, возвращаем прежний размер
            stage.getViewport().update(width, height, true);
            return;
        }

        // Обновляем размер viewport при изменении размера окна
        if (getScreen() != null) {
            getScreen().resize(width, height);
        } else {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        // Освобождаем ресурсы
        if (getScreen() != null) {
            getScreen().dispose();
        }

        // Освобождаем ресурсы аудио
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }

        // Освобождаем текстуры
        backgroundTexture.dispose();
        logoTexture.dispose();

        stage.dispose();
        skin.dispose();
        batch.dispose();
        generator.dispose();
        font.dispose();
        transition.dispose();
    }

    // Методы для управления музыкой в настройках
    public void setMusicVolume(float volume) {
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(volume);
        }
    }

    public void toggleMusic(boolean enabled) {
        if (backgroundMusic != null) {
            if (enabled) {
                backgroundMusic.play();
            } else {
                backgroundMusic.pause();
            }
        }
    }


    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    public ScreenTransition getTransition() {
        return transition;
    }

    // Обновленный метод создания кнопки с поддержкой разных состояний
    private TextButton createButtonWithStates(String normalTexture, String hoverTexture, String pressedTexture) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("default-font");

        // Устанавливаем разные текстуры для разных состояний кнопки
        style.up = skin.getDrawable(normalTexture);    // Обычное состояние
        style.over = skin.getDrawable(hoverTexture);   // При наведении
        style.down = skin.getDrawable(pressedTexture); // При нажатии

        return new TextButton("", style);
    }
}
package io.github.TheUntitledFantasyGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Main extends Game {

    private Skin skin;
    private TextButton play_button, settings_button, quit_button;
    private Stage stage;
    private SpriteBatch batch;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontParameter parameter;
    private ScreenTransition transition;
    private Screen nextScreen;
    private Music backgroundMusic; // Добавляем переменную для фоновой музыки

    @Override
    public void create() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        batch = new SpriteBatch();
        transition = new ScreenTransition(1.0f); // 1 секунда на переход

        // Загрузка и настройка фоновой музыки
        loadBackgroundMusic();

        // Настройка skin
        skin = new Skin();

        // Загрузка шрифта с поддержкой кириллицы
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        parameter = new FreeTypeFontParameter();
        parameter.size = 20;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        BitmapFont font = generator.generateFont(parameter);
        skin.add("default-font", font);

        // Добавляем текстуры кнопок
        skin.add("play_button_baton", new Texture("sprites/play_button.png"));
        skin.add("settings_button_baton", new Texture("sprites/settings_button.png"));
        skin.add("quit_button_baton", new Texture("sprites/quit_button.png"));

        // Создание кнопок (о велики плэ бутон батон)
        play_button = createButton("play_button_baton");
        settings_button = createButton("settings_button_baton");
        quit_button = createButton("quit_button_baton");

        // Настройка обработчиков событий для кнопок

        // Кнопка Play - переход на выбор сейва (не бибу как буду сейвы делать)
        play_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Play button clicked!");
                startGameScreenTransition();
            }
        });

        // Кнопка Settings - переход в меню настроек
        settings_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Settings button clicked!");
                startSettingsScreenTransition();
            }
        });

        // Кнопка Quit - выход из игры
        quit_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Quit button clicked!");
                Gdx.app.exit(); // Выход из приложения
            }
        });

        // Настройка Stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Создаем таблицу для организации UI
        Table table = new Table();
        table.setFillParent(true);

        // Добавляем кнопки в таблицу с отступами
        table.add(play_button).padBottom(20).width(600).height(150).row();
        table.add(settings_button).padBottom(20).width(600).height(150).row();
        table.add(quit_button).width(600).height(150);

        // Добавляем таблицу на stage
        stage.addActor(table);
    }

    // Метод для загрузки и настройки фоновой музыки
    private void loadBackgroundMusic() {
        try {
            // Загружаем музыку из файла
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/test_sound.mp3"));

            // Настраиваем музыку для непрерывного воспроизведения
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f); // Громкость от 0 до 1

            // Запускаем воспроизведение
            backgroundMusic.play();
        } catch (Exception e) {
            System.err.println("Не удалось загрузить фоновую музыку: " + e.getMessage());
        }
    }

    // Метод для начала перехода на игровой экран
    public void startGameScreenTransition() {
        // Блокируем кнопки меню во время перехода
        disableMenuButtons();

        // Создаем следующий экран, но пока не устанавливаем его
        nextScreen = new GameScreen(this);

        // Начинаем затемнение
        transition.start(ScreenTransition.TransitionType.FADE_OUT, () -> {
            // После затемнения переключаем экран
            setScreen(nextScreen);
            // Начинаем осветление
            transition.start(ScreenTransition.TransitionType.FADE_IN, null);
        });
    }

    // Метод для начала перехода на экран настроек
    public void startSettingsScreenTransition() {
        // Блокируем кнопки меню во время перехода
        disableMenuButtons();

        // Создаем следующий экран, но пока не устанавливаем его
        nextScreen = new SettingsScreen(this);

        // Начинаем затемнение
        transition.start(ScreenTransition.TransitionType.FADE_OUT, () -> {
            // После затемнения переключаем экран
            setScreen(nextScreen);
            // Начинаем осветление
            transition.start(ScreenTransition.TransitionType.FADE_IN, null);
        });
    }

    // Вспомогательный метод для отключения кнопок меню
    private void disableMenuButtons() {
        play_button.setDisabled(true);
        settings_button.setDisabled(true);
        quit_button.setDisabled(true);
    }

    // Вспомогательный метод для включения кнопок меню
    private void enableMenuButtons() {
        play_button.setDisabled(false);
        settings_button.setDisabled(false);
        quit_button.setDisabled(false);
    }

    // Метод для возврата в меню
    public void returnToMenu() {
        // Начинаем затемнение
        transition.start(ScreenTransition.TransitionType.FADE_OUT, () -> {
            // После затемнения переключаем на меню
            setScreen(null);
            // Восстанавливаем обработчик ввода для меню
            Gdx.input.setInputProcessor(stage);
            // Разблокируем кнопки меню
            enableMenuButtons();
            // Начинаем осветление
            transition.start(ScreenTransition.TransitionType.FADE_IN, null);
        });
    }

    @Override
    public void render() {
        // Очистка экрана
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Если есть активный экран, рендерим его
        if (getScreen() != null) {
            super.render();
        } else {
            // Иначе отображаем меню
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
        }

        // Рендерим переход поверх всего
        transition.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        // Обновляем размер viewport при изменении размера окна
        if (getScreen() != null) {
            getScreen().resize(width, height);
        } else {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        // Освобождаем ресурсы блеблебле
        if (getScreen() != null) {
            getScreen().dispose();
        }

        // Освобождаем ресурсы аудио
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }

        stage.dispose();
        skin.dispose();
        batch.dispose();
        generator.dispose();
        transition.dispose();
    }

    // Методы для управления музыкой в настройках
    public void setMusicVolume(float volume) {
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(volume);
        }
    }

    public float getMusicVolume() {
        return backgroundMusic != null ? backgroundMusic.getVolume() : 0f;
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

    public boolean isMusicPlaying() {
        return backgroundMusic != null && backgroundMusic.isPlaying();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Stage getStage() {
        return stage;
    }

    public BitmapFont getFont() {
        return skin.getFont("default-font");
    }

    public ScreenTransition getTransition() {
        return transition;
    }

    private TextButton createButton(String textureName) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("default-font");
        style.up = skin.getDrawable(textureName);
        style.down = skin.getDrawable(textureName);
        style.over = skin.getDrawable(textureName);

        return new TextButton("", style);
    }
}
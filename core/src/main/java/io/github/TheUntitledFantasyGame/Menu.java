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
    private Music backgroundMusic; // Добавляем переменную для фоновой музыки
    private BitmapFont font; // выносим шрифт на уровень класса
    private Texture backgroundTexture; // текстура фона
    private Texture logoTexture; // текстура логотипа

    @Override
    public void create() {


        // Устанавливаем полноэкранный режим
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        // Запрещаем изменять размер окна
        Gdx.graphics.setResizable(false);

        // Загружаем и устанавливаем кастомный курсор
        try {
            // Загружаем изображение курсора
            Pixmap originalCursor = new Pixmap(Gdx.files.internal("sprites/custom_cursor.png"));

            // Точный масштаб 4x как вы просили
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

        batch = new SpriteBatch();
        transition = new ScreenTransition(1.0f); // 1 секунда на переход

        // Загрузка фона и логотипа
        backgroundTexture = new Texture(Gdx.files.internal("sprites/Background.png"));
        logoTexture = new Texture(Gdx.files.internal("sprites/Logo.UTLL.png"));

        // Загрузка и настройка фоновой музыки
        loadBackgroundMusic();

        // Настройка skin
        skin = new Skin();

        // Загрузка шрифта с поддержкой кириллицы
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 20;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        font = generator.generateFont(parameter);
        skin.add("default-font", font);

        // Добавляем текстуры для разных состояний кнопок
        loadButtonTextures();

        // Создание кнопок с разными состояниями
        play_button = createButtonWithStates("play_button", "play_buttonH", "play_buttonP");
        settings_button = createButtonWithStates("settings_button", "settings_buttonH", "settings_buttonP");
        quit_button = createButtonWithStates("quit_button", "quit_buttonH", "quit_buttonP");

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
        resetStage();
    }

    // Метод для загрузки текстур кнопок для разных состояний
    private void loadButtonTextures() {
        // Нормальное состояние кнопок
        skin.add("play_button", new Texture("sprites/play_button.png"));
        skin.add("settings_button", new Texture("sprites/settings_button.png"));
        skin.add("quit_button", new Texture("sprites/quit_button.png"));

        // Состояние наведения (hover)
        skin.add("play_buttonH", new Texture("sprites/play_buttonH.png"));
        skin.add("settings_buttonH", new Texture("sprites/settings_buttonH.png"));
        skin.add("quit_buttonH", new Texture("sprites/quit_buttonH.png"));

        // Состояние нажатия (pressed)
        skin.add("play_buttonP", new Texture("sprites/play_buttonP.png"));
        skin.add("settings_buttonP", new Texture("sprites/settings_buttonP.png"));
        skin.add("quit_buttonP", new Texture("sprites/quit_buttonP.png"));
    }

    // Метод для пересоздания stage и его элементов
    public void resetStage() {
        // Если существующий stage есть, то освобождаем его
        if (stage != null) {
            stage.dispose();
        }

        // Создаем новый stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Добавляем фоновое изображение
        Image background = new Image(backgroundTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        // Создаем таблицу для организации UI
        Table mainTable = new Table();
        mainTable.setFillParent(true);


        Image logo = new Image(logoTexture);
        float logoWidth = Math.min(660, Gdx.graphics.getWidth() * 0.6f);
        float logoHeight = logoWidth * logoTexture.getHeight() / logoTexture.getWidth();

        // Создаем таблицу для логотипа
        Table logoTable = new Table();
        logoTable.add(logo).width(logoWidth).height(logoHeight);

        // Создаем таблицу для кнопок
        Table buttonTable = new Table();
        // Добавляем кнопки в таблицу с отступами (опускаем их ниже)
        buttonTable.add(play_button).padBottom(20).width(600).height(150).row();
        buttonTable.add(settings_button).padBottom(20).width(600).height(150).row();
        buttonTable.add(quit_button).width(600).height(150);

        // Добавляем логотип и кнопки в основную таблицу
        mainTable.add(logoTable).expandX().padBottom(0).row(); // Увеличиваем отступ между логотипом и кнопками
        mainTable.add(buttonTable).expand().align(Align.center).padBottom(100); // Увеличиваем отступ снизу

        // Добавляем таблицу на stage
        stage.addActor(mainTable);
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
            System.err.println("Failed to load background music: " + e.getMessage());
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

            // Пересоздаем stage и его компоненты
            resetStage();

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
        // Проверяем, разрешено ли изменение размера
        // Флаг для контроля возможности изменения разрешения
        boolean resizingEnabled = false;
        if (!resizingEnabled) {
            // Если изменение размера не разрешено, возвращаем прежний размер
            // либо игнорируем изменение - libGDX все равно вызовет этот метод
            System.out.println("Resized 0.0");
            stage.getViewport().update(width, height, true);
            System.out.println("Resized to: " + width + "x" + height);
            return;
        }

        // Обновляем размер viewport при изменении размера окна
        if (getScreen() != null) {
            getScreen().resize(width, height);
            System.out.println("Resized");
        } else {
            stage.getViewport().update(width, height, true);
            System.out.println("Resized 2.0");
        }
        System.out.println("Resized to: " + width + "x" + height);
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
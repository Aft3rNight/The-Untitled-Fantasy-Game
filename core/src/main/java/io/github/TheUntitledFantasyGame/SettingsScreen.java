package io.github.TheUntitledFantasyGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsScreen implements Screen {
    private final Menu game;
    private final Stage stage;
    private Skin skin;
    private final GameSettings gameSettings;

    // Добавляем текстуру для фона и SpriteBatch для её отрисовки
    private Texture backgroundTexture;
    private SpriteBatch batch;

    // Элементы управления для настроек
    private CheckBox musicCheckbox;
    private Slider volumeSlider;
    private Label volumeValueLabel;
    private CheckBox fullscreenCheckbox;
    private SelectBox<String> resolutionSelect;

    public SettingsScreen(Menu game) {
        this.game = game;

        // Инициализируем менеджер настроек
        this.gameSettings = new GameSettings(game);

        // Создаем новый Stage для этого экрана
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Инициализируем SpriteBatch для отрисовки фона
        batch = new SpriteBatch();

        // Загружаем текстуру фона
        backgroundTexture = new Texture(Gdx.files.internal("sprites/Background.png"));

        // Создаем собственный скин с настроенным шрифтом
        createCustomSkin();

        // Создаем UI экрана настроек
        createUI();
    }

    // Создаем собственный скин с правильными настройками
    private void createCustomSkin() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Используем существующий шрифт из Main
        BitmapFont font = game.getFont();
        if (font != null) {
            // Заменяем шрифт в скине на шрифт из Main
            skin.add("default-font", font, BitmapFont.class);

            // Обновляем стили, которые используют шрифт
            Label.LabelStyle labelStyle = skin.get(Label.LabelStyle.class);
            labelStyle.font = font;

            TextButton.TextButtonStyle buttonStyle = skin.get(TextButton.TextButtonStyle.class);
            buttonStyle.font = font;

            CheckBox.CheckBoxStyle checkBoxStyle = skin.get(CheckBox.CheckBoxStyle.class);
            checkBoxStyle.font = font;

            SelectBox.SelectBoxStyle selectBoxStyle = skin.get(SelectBox.SelectBoxStyle.class);
            selectBoxStyle.font = font;

            List.ListStyle listStyle = skin.get(List.ListStyle.class);
            listStyle.font = font;
        }
    }

    // Создаем UI экрана настроек
    private void createUI() {
        // Создаем основную таблицу для организации UI
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);

        // Заголовок экрана настроек
        Label titleLabel = new Label("Settings (Press ESC to return)", skin);
        titleLabel.setFontScale(1.5f);

        // Создаем галочку для музыки с правильным стилем
        musicCheckbox = new CheckBox("Turn on the music", skin);
        musicCheckbox.setChecked(gameSettings.isMusicEnabled());

        // Обработчик включения/выключения музыки
        musicCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameSettings.setMusicEnabled(musicCheckbox.isChecked());
                // Сразу применяем настройку музыки (без перезагрузки экрана)
                game.toggleMusic(musicCheckbox.isChecked());
            }
        });

        // Создаем слайдер для регулировки громкости
        Label volumeLabel = new Label("Music Volume:", skin);
        volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.setValue(gameSettings.getMusicVolume());
        volumeValueLabel = new Label(Math.round(gameSettings.getMusicVolume() * 100) + "%", skin);

        // Обработчик изменений слайдера громкости
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                gameSettings.setMusicVolume(volume);
                volumeValueLabel.setText(Math.round(volume * 100) + "%");

                // Применяем изменение громкости мгновенно
                game.setMusicVolume(volume);
            }
        });

        // Добавляем флажок для полноэкранного режима
        fullscreenCheckbox = new CheckBox("Full screen mode (resolution does not change if enabled)", skin);
        fullscreenCheckbox.setChecked(gameSettings.isFullscreen());

        // Обработчик изменения полноэкранного режима
        fullscreenCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameSettings.setFullscreen(fullscreenCheckbox.isChecked());
            }
        });

        // Создаем выпадающий список для выбора разрешения
        Label resolutionLabel = new Label("Screen resolution:", skin);

        // Возможные варианты разрешения
        String[] resolutions = {"1280x720", "1366x768", "1600x900", "1920x1080", "2560x1440", "3840x2160"};
        Array<String> resolutionItems = new Array<>(resolutions);

        resolutionSelect = new SelectBox<>(skin);
        resolutionSelect.setItems(resolutionItems);

        // Устанавливаем текущее разрешение
        String currentResolution = gameSettings.getResolutionString();

        // Ищем текущее разрешение в списке
        boolean foundResolution = false;
        for (String res : resolutions) {
            if (res.equals(currentResolution)) {
                resolutionSelect.setSelected(res);
                foundResolution = true;
                break;
            }
        }

        // Если текущее разрешение не найдено в списке, выбираем ближайшее
        if (!foundResolution) {
            resolutionSelect.setSelected("1920x1080"); // По умолчанию
            gameSettings.setResolution("1920x1080");
        }

        // Обработчик изменения разрешения
        resolutionSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedResolution = resolutionSelect.getSelected();
                gameSettings.setResolution(selectedResolution);
            }
        });

        // Создаем кнопку "Применить"
        TextButton applyButton = new TextButton("Apply", skin);

        // Обработчик кнопки "Применить" - теперь включает и сохранение, и применение
        applyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Сначала сохраняем настройки
                gameSettings.saveSettings();

                // Затем применяем их
                boolean screenChanged = gameSettings.applySettings();

                // Если изменились настройки экрана, нужно обновить viewport
                if (screenChanged) {
                    stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
                }

                System.out.println("Settings saved and applied");
            }
        });

        // Добавляем элементы в таблицу
        mainTable.add(titleLabel).colspan(3).pad(20).row();

        // Добавляем флажок музыки
        mainTable.add(musicCheckbox).colspan(3).left().padBottom(10).row();

        // Добавляем слайдер громкости
        mainTable.add(volumeLabel).left().padRight(10);
        mainTable.add(volumeSlider).width(300).fillX();
        mainTable.add(volumeValueLabel).width(50).row();

        mainTable.add().height(30).row(); // Пустая строка для разделения

        // Добавляем флажок полноэкранного режима
        mainTable.add(fullscreenCheckbox).colspan(3).left().padBottom(10).row();

        // Добавляем выбор разрешения
        mainTable.add(resolutionLabel).left().padRight(10);
        mainTable.add(resolutionSelect).width(300).fillX().colspan(2).row();

        mainTable.add().height(50).row(); // Пустая строка перед кнопкой

        // Добавляем кнопку "Применить"
        mainTable.add(applyButton).colspan(3).width(200).height(50).padTop(20);

        // Добавляем таблицу на stage
        stage.addActor(mainTable);
    }

    @Override
    public void show() {
        // Метод вызывается при отображении экрана
        Gdx.input.setInputProcessor(stage);
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

        // Отрисовка фона
        batch.begin();
        // Растягиваем изображение на весь экран
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Обновление и отрисовка stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Обновляем размер viewport при изменении размера окна
        stage.getViewport().update(width, height, true);
        System.out.println("Settings screen resized to: " + width + "x" + height);
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
        batch.dispose();
        backgroundTexture.dispose();
        // Не освобождаем skin здесь, поскольку он использует шрифты из Main
    }
}
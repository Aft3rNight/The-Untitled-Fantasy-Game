package io.github.TheUntitledFantasyGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private float volume;
    private String currentResolution;
    private boolean isFullscreen;
    private boolean musicEnabled;

    // Возможные варианты разрешения
    private final String[] resolutions = {"1280x720", "1366x768", "1600x900", "1920x1080", "2560x1440", "3840x2160"};

    public SettingsScreen(Main game) {
        this.game = game;

        // Получаем текущие настройки из Main
        volume = game.getMusicVolume();
        musicEnabled = game.isMusicPlaying();
        isFullscreen = Gdx.graphics.isFullscreen();

        // Определяем текущее разрешение
        int currentWidth = Gdx.graphics.getWidth();
        int currentHeight = Gdx.graphics.getHeight();
        currentResolution = currentWidth + "x" + currentHeight;

        // Создаем новый Stage для этого экрана
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Создаем собственный скин с настроенным шрифтом
        createCustomSkin();

        // Создаем основную таблицу для организации UI
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);

        // Заголовок экрана настроек
        Label titleLabel = new Label("Настройки (Нажмите ESC для возврата)", skin);
        titleLabel.setFontScale(1.5f);

        // Создаем галочку для музыки с правильным стилем
        final CheckBox musicCheckbox = new CheckBox(" Включить музыку", skin);
        musicCheckbox.setChecked(musicEnabled);

        // Обработчик включения/выключения музыки
        musicCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicEnabled = musicCheckbox.isChecked();
                game.toggleMusic(musicEnabled);
            }
        });

        // Создаем слайдер для регулировки громкости
        Label volumeLabel = new Label("Громкость музыки:", skin);
        final Slider volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.setValue(volume);
        final Label volumeValueLabel = new Label(Math.round(volume * 100) + "%", skin);

        // Обработчик изменений слайдера громкости
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                volume = volumeSlider.getValue();
                volumeValueLabel.setText(Math.round(volume * 100) + "%");

                // Применяем изменение громкости мгновенно
                game.setMusicVolume(volume);
            }
        });

        // Добавляем флажок для полноэкранного режима
        final CheckBox fullscreenCheckbox = new CheckBox(" Полноэкранный режим", skin);
        fullscreenCheckbox.setChecked(isFullscreen);

        // Обработчик изменения полноэкранного режима
        fullscreenCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isFullscreen = fullscreenCheckbox.isChecked();
            }
        });

        // Создаем выпадающий список для выбора разрешения
        Label resolutionLabel = new Label("Разрешение экрана:", skin);

        // Конвертируем массив строк в Array<String> для SelectBox
        Array<String> resolutionItems = new Array<>(resolutions);

        final SelectBox<String> resolutionSelect = new SelectBox<>(skin);
        resolutionSelect.setItems(resolutionItems);

        // Пытаемся найти текущее разрешение в списке
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
            currentResolution = "1920x1080";
        }

        // Обработчик изменения разрешения
        resolutionSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentResolution = resolutionSelect.getSelected();
            }
        });

        // Создаем кнопку применить с правильным стилем
        TextButton applyButton = new TextButton("Применить", skin);
        applyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applySettings();
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

    // Метод для применения настроек
    private void applySettings() {
        try {
            // Сохраняем настройки громкости и состояние музыки
            game.setMusicVolume(volume);
            game.toggleMusic(musicEnabled);

            boolean needsReset = false;

            // Проверяем нужно ли менять разрешение или режим экрана
            if (isFullscreen != Gdx.graphics.isFullscreen()) {
                needsReset = true;
            } else if (!isFullscreen) {
                // Если не в полноэкранном режиме, проверяем изменилось ли разрешение
                String[] dimensions = currentResolution.split("x");
                int width = Integer.parseInt(dimensions[0]);
                int height = Integer.parseInt(dimensions[1]);

                if (width != Gdx.graphics.getWidth() || height != Gdx.graphics.getHeight()) {
                    needsReset = true;
                }
            }

            // Применяем изменения если они нужны
            if (needsReset) {
                if (isFullscreen) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    String[] dimensions = currentResolution.split("x");
                    int width = Integer.parseInt(dimensions[0]);
                    int height = Integer.parseInt(dimensions[1]);
                    Gdx.graphics.setWindowedMode(width, height);
                }

                // После применения настроек нужно правильно обновить stage
                stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            }

            // Вывод сообщения в консоль для подтверждения применения настроек
            System.out.println("Настройки применены: громкость=" + volume +
                    ", музыка включена=" + musicEnabled +
                    ", разрешение=" + currentResolution +
                    ", полноэкранный режим=" + isFullscreen);

        } catch (Exception e) {
            System.err.println("Ошибка при применении настроек: " + e.getMessage());
            e.printStackTrace();
        }
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
        // Не освобождаем skin здесь, поскольку он использует шрифты из Main
    }
}
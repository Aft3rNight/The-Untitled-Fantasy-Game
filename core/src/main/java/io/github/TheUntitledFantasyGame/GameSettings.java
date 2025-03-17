package io.github.TheUntitledFantasyGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * GameSettings - класс для управления настройками игры.
 * Позволяет сохранять, загружать и применять настройки игры из файла game.settings.
 */
public class GameSettings {
    private static final String SETTINGS_FILE = "game.settings";

    private static final String KEY_MUSIC_ENABLED = "music_enabled";
    private static final String KEY_MUSIC_VOLUME = "music_volume";
    private static final String KEY_FULLSCREEN = "fullscreen";
    private static final String KEY_RESOLUTION_WIDTH = "resolution_width";
    private static final String KEY_RESOLUTION_HEIGHT = "resolution_height";

    private boolean musicEnabled;
    private float musicVolume;
    private boolean fullscreen;
    private int resolutionWidth;
    private int resolutionHeight;

    private final Menu game;
    private final Map<String, String> settingsMap;

    /**
     * Конструктор класса GameSettings.
     *
     * @param game экземпляр главного класса игры
     */
    public GameSettings(Menu game) {
        this.game = game;
        this.settingsMap = new HashMap<>();
        loadSettings();
    }

    /**
     * Загружает настройки из файла game.settings.
     */
    public void loadSettings() {
        // Устанавливаем значения по умолчанию
        musicEnabled = true;
        musicVolume = 0.5f;
        fullscreen = false;
        resolutionWidth = Gdx.graphics.getWidth();
        resolutionHeight = Gdx.graphics.getHeight();

        try {
            FileHandle file = Gdx.files.local(SETTINGS_FILE);

            // Проверяем существует ли файл настроек
            if (file.exists()) {
                settingsMap.clear();
                BufferedReader reader = file.reader(8192);
                String line;

                // Читаем файл построчно
                while ((line = reader.readLine()) != null) {
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            settingsMap.put(key, value);
                        }
                    }
                }
                reader.close();

                // Применяем загруженные настройки
                musicEnabled = Boolean.parseBoolean(settingsMap.getOrDefault(KEY_MUSIC_ENABLED, "true"));
                musicVolume = Float.parseFloat(settingsMap.getOrDefault(KEY_MUSIC_VOLUME, "0.5"));
                fullscreen = Boolean.parseBoolean(settingsMap.getOrDefault(KEY_FULLSCREEN, "false"));
                resolutionWidth = Integer.parseInt(settingsMap.getOrDefault(KEY_RESOLUTION_WIDTH, String.valueOf(resolutionWidth)));
                resolutionHeight = Integer.parseInt(settingsMap.getOrDefault(KEY_RESOLUTION_HEIGHT, String.valueOf(resolutionHeight)));

                System.out.println("Settings loaded from " + SETTINGS_FILE);
            } else {
                System.out.println("Settings file not found. Using default settings.");
                // Если файл не существует, создаем его с настройками по умолчанию
                saveSettings();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading settings: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Settings loaded: musicEnabled=" + musicEnabled +
                ", musicVolume=" + musicVolume +
                ", fullscreen=" + fullscreen +
                ", resolution=" + resolutionWidth + "x" + resolutionHeight);
    }

    /**
     * Сохраняет текущие настройки в файл game.settings.
     */
    public void saveSettings() {
        try {
            FileHandle file = Gdx.files.local(SETTINGS_FILE);
            BufferedWriter writer = new BufferedWriter(file.writer(false));

            // Обновляем карту настроек текущими значениями
            settingsMap.put(KEY_MUSIC_ENABLED, String.valueOf(musicEnabled));
            settingsMap.put(KEY_MUSIC_VOLUME, String.valueOf(musicVolume));
            settingsMap.put(KEY_FULLSCREEN, String.valueOf(fullscreen));
            settingsMap.put(KEY_RESOLUTION_WIDTH, String.valueOf(resolutionWidth));
            settingsMap.put(KEY_RESOLUTION_HEIGHT, String.valueOf(resolutionHeight));

            // Записываем настройки в файл
            for (Map.Entry<String, String> entry : settingsMap.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }

            writer.close();
            System.out.println("Settings saved to " + SETTINGS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Settings saved: musicEnabled=" + musicEnabled +
                ", musicVolume=" + musicVolume +
                ", fullscreen=" + fullscreen +
                ", resolution=" + resolutionWidth + "x" + resolutionHeight);
    }

    /**
     * Применяет настройки к игре.
     *
     * @return true если потребовалось изменение экрана, иначе false
     */
    public boolean applySettings() {
        boolean screenChanged = false;

        try {
            // Применяем настройки звука
            game.toggleMusic(musicEnabled);
            game.setMusicVolume(musicVolume);

            // Проверяем, нужно ли изменять настройки экрана
            boolean currentFullscreen = Gdx.graphics.isFullscreen();
            int currentWidth = Gdx.graphics.getWidth();
            int currentHeight = Gdx.graphics.getHeight();

            // Применяем настройки отображения, если они изменились
            if (fullscreen != currentFullscreen ||
                    (!fullscreen && (resolutionWidth != currentWidth || resolutionHeight != currentHeight))) {

                if (fullscreen) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(resolutionWidth, resolutionHeight);
                }
                screenChanged = true;
            }

            System.out.println("Settings applied: musicEnabled=" + musicEnabled +
                    ", musicVolume=" + musicVolume +
                    ", fullscreen=" + fullscreen +
                    ", resolution=" + resolutionWidth + "x" + resolutionHeight);
        } catch (Exception e) {
            System.err.println("Error applying settings: " + e.getMessage());
            e.printStackTrace();
        }

        return screenChanged;
    }

    /**
     * Устанавливает значение громкости музыки.
     *
     * @param volume значение громкости от 0.0 до 1.0
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        game.setMusicVolume(volume); // Применение громкости сразу
    }

    /**
     * Устанавливает режим воспроизведения музыки.
     *
     * @param enabled true - включить музыку, false - выключить
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        game.toggleMusic(enabled); // Применение настройки сразу
    }

    /**
     * Устанавливает режим отображения.
     *
     * @param fullscreen true - полноэкранный режим, false - оконный режим
     */
    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    /**
     * Устанавливает разрешение экрана.
     *
     * @param width ширина в пикселях
     * @param height высота в пикселях
     */
    public void setResolution(int width, int height) {
        this.resolutionWidth = width;
        this.resolutionHeight = height;
    }

    /**
     * Устанавливает разрешение из строкового представления.
     *
     * @param resolution строка формата "ШИРИНАxВЫСОТА", например "1920x1080"
     */
    public void setResolution(String resolution) {
        try {
            String[] dimensions = resolution.split("x");
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);
            setResolution(width, height);
        } catch (Exception e) {
            System.err.println("Error parsing resolution string: " + resolution);
        }
    }

    /**
     * Получает текущее разрешение в виде строки.
     *
     * @return строка с разрешением в формате "ШИРИНАxВЫСОТА"
     */
    public String getResolutionString() {
        return resolutionWidth + "x" + resolutionHeight;
    }

    /**
     * Возвращает текущую громкость музыки.
     *
     * @return значение громкости от 0.0 до 1.0
     */
    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Проверяет, включена ли музыка.
     *
     * @return true - если музыка включена, иначе false
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Проверяет, включен ли полноэкранный режим.
     *
     * @return true - если включен полноэкранный режим, иначе false
     */
    public boolean isFullscreen() {
        return fullscreen;
    }

    /**
     * Возвращает текущую ширину экрана.
     *
     * @return ширина в пикселях
     */
    public int getResolutionWidth() {
        return resolutionWidth;
    }

    /**
     * Возвращает текущую высоту экрана.
     *
     * @return высота в пикселях
     */
    public int getResolutionHeight() {
        return resolutionHeight;
    }
}
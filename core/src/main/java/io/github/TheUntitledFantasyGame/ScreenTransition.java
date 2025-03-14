package io.github.TheUntitledFantasyGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class ScreenTransition {
    // Типы переходов
    public enum TransitionType {
        FADE_IN,  // Осветление (из черного)
        FADE_OUT  // Затемнение (в черный)
    }

    private ShapeRenderer shapeRenderer;
    private float duration;     // Длительность перехода в секундах
    private float timer;        // Текущее время
    private TransitionType type;
    private boolean isActive;
    private Runnable onComplete;

    public ScreenTransition(float duration) {
        this.shapeRenderer = new ShapeRenderer();
        this.duration = duration;
        this.isActive = false;
    }

    // Начать переход
    public void start(TransitionType type, Runnable onComplete) {
        this.type = type;
        this.timer = 0;
        this.isActive = true;
        this.onComplete = onComplete;
    }

    // Обновление и рендеринг перехода
    public void update(float delta) {
        if (!isActive) return;

        timer += delta;

        // Проверка завершения
        if (timer >= duration) {
            isActive = false;
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        // Рассчитываем прозрачность
        float alpha = timer / duration;
        if (type == TransitionType.FADE_IN) {
            alpha = 1 - alpha; // Инвертируем для осветления
        }
        alpha = MathUtils.clamp(alpha, 0, 1);

        // Рисуем прямоугольник на весь экран с нужной прозрачностью
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, alpha);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public boolean isActive() {
        return isActive;
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}

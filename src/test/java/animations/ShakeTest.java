package animations;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import org.junit.jupiter.api.Test;

import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class ShakeTest extends ApplicationTest {

    @Test
    public void testShakeInitialization() {
        // Створюємо новий вузол для анімації
        Node node = new Text("Test");

        // Створюємо об'єкт Shake
        Shake shake = new Shake(node);

        // Перевіряємо налаштування TranslateTransition
        TranslateTransition tt = shake.getTranslateTransition();
        assertEquals(Duration.millis(70), tt.getDuration());
        assertEquals(0f, tt.getFromX());
        assertEquals(10f, tt.getByX());
        assertEquals(3, tt.getCycleCount());
        assertTrue(tt.isAutoReverse());
    }

    @Test
    public void testPlayAnim() {
        // Створюємо новий вузол для анімації
        Node node = new Text("Test");

        // Створюємо об'єкт Shake
        Shake shake = new Shake(node);

        // Виконуємо анімацію
        shake.playAnim();

        // Перевіряємо, що анімація почалась
        assertSame(Animation.Status.RUNNING, shake.getTranslateTransition().getStatus());
    }
}
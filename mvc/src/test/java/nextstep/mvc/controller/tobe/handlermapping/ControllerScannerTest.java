package nextstep.mvc.controller.tobe.handlermapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import samples.TestAnnotationController;

class ControllerScannerTest {

    @Test
    void getControllers() {
        ControllerScanner controllerScanner = new ControllerScanner(new Object[]{"samples"});

        Map<Class<?>, Object> controllers = controllerScanner.getControllers();

        assertThat(controllers.keySet()).containsExactly(TestAnnotationController.class);
    }
}

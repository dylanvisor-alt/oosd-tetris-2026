package tetris;

import org.junit.jupiter.api.Test;
import tetris.controller.ConfigController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigControllerTest {

    @Test
    void storesAllConfigurationSelections() {
        ConfigController controller = new ConfigController();

        controller.setFieldSize("12 x 24");
        controller.setLevel(6);
        controller.setMusicEnabled(false);
        controller.setSoundEffectsEnabled(false);
        controller.setAiPlayEnabled(true);
        controller.setExtendedModeEnabled(true);

        assertEquals("12 x 24", controller.getFieldSize());
        assertEquals(6, controller.getLevel());
        assertFalse(controller.isMusicEnabled());
        assertFalse(controller.isSoundEffectsEnabled());
        assertTrue(controller.isAiPlayEnabled());
        assertTrue(controller.isExtendedModeEnabled());
    }
}

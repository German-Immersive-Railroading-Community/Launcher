package eu.girc.launcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResourceTests {
    @Test
    public void testResolveNullInputThrowsException() {
        Assertions.assertThrows(NullPointerException.class, () -> Resources.resolve(null));
    }

    @Test
    public void testResolveInput() {
        Assertions.assertEquals("/eu/girc/launcher/test", Resources.resolve("test"));
        Assertions.assertEquals("/test", Resources.resolve("/test"));
    }

    @Test
    public void testGetResource() {
        Assertions.assertThrows(NullPointerException.class, () -> Resources.getResource("test"));
        Assertions.assertThrows(NullPointerException.class, () -> Resources.getResource("/test"));
        Assertions.assertThrows(NullPointerException.class, () -> Resources.getResource(null));
        Assertions.assertNotNull(Resources.getResource("TestResource.txt"));
    }

    @Test
    public void failingTest() {
        Assertions.assertNotNull(null);
    }
}

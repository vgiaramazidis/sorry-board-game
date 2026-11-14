package view;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PlaybookResourcesTest {
    @Test
    public void everyPlaybookImageIsPackaged() {
        String[] resources = {
                "/image/playbook/board-overview.png",
                "/image/playbook/start-yellow.png",
                "/image/playbook/start-red.png",
                "/image/playbook/slide-short.png",
                "/image/playbook/slide-long.png",
                "/image/playbook/safety-red.png",
                "/image/playbook/safety-yellow.png",
                "/image/playbook/home-yellow.png",
                "/image/playbook/home-red.png",
                "/image/playbook/card-1.jpg",
                "/image/playbook/card-2.jpg",
                "/image/playbook/card-3.jpg",
                "/image/playbook/card-4.jpg",
                "/image/playbook/card-5.jpg",
                "/image/playbook/card-7.jpg",
                "/image/playbook/card-8.jpg",
                "/image/playbook/card-10.jpg",
                "/image/playbook/card-11.jpg",
                "/image/playbook/card-12.jpg",
                "/image/playbook/card-sorry.jpg",
                "/image/redPawn1.png",
                "/image/yellowPawn1.png"
        };

        for (String resource : resources) {
            assertNotNull("Missing resource " + resource,
                    PlaybookResourcesTest.class.getResource(resource));
        }
    }
}

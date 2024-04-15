package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IndexControllerTest {
    @Test
    public void testGetIndex() {
        IndexController controller = new IndexController();
        String result = controller.getIndex();
        assertEquals("index", result);
    }

}
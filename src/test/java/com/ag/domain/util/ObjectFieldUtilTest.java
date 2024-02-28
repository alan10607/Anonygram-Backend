package com.ag.domain.util;

import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectFieldUtilTest {

    @Test
    void overwritePublicFields() {
        // arrange
        TestObject target = new TestObject(10, null);
        TestObject source = new TestObject(20, "newValue");

        // action
        ObjectFieldUtil.overwritePublicFields(target, source);

        // assert
        assertEquals(20, target.id);
        assertEquals("newValue", target.name);
    }


    @Data
    static class TestObject {
        public int id;
        public String name;

        public TestObject(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

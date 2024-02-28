package com.ag.domain.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectFieldUtilTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class TestObject {
        public int id;
        public String name;
        public String title;
    }

    @Test
    void overwritePublicFields() {
        // arrange
        TestObject target = new TestObject(10, null, "title");
        TestObject source = new TestObject(20, "newValue", "title");

        // action
        ObjectFieldUtil.overwritePublicFields(target, source);

        // assert
        assertEquals(20, target.id);
        assertEquals("newValue", target.name);
        assertEquals("title", target.title);
    }

    @Test
    void retainFields() {
        // arrange
        TestObject originalObject = new TestObject(1, "OriginalName", "title");

        // action
        TestObject retainedObject = ObjectFieldUtil.retainFields(originalObject, "title");

        // assert
        assertNotNull(retainedObject);
        assertEquals(0, retainedObject.getId());
        assertNull(retainedObject.getName());
        assertEquals("title", retainedObject.getTitle());
    }

}

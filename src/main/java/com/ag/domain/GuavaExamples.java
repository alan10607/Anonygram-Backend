package com.ag.domain;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.math.IntMath;
import com.google.common.net.HostAndPort;
import com.google.common.io.Files;
import com.google.common.util.concurrent.ListenableFutureTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaExamples {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        // 1. 字符串工具
        String input = "   Hello, world!   ";
        String trimmed = Strings.nullToEmpty(input).trim();
        System.out.println(trimmed); // Output: "Hello, world!"

        // 2. 集合工具
        List<String> names = Lists.newArrayList("Alice", "Bob", "Charlie");
        List<String> filteredNames = Lists.newArrayListWithCapacity(names.size());
        for (String name : names) {
            if (!Strings.isNullOrEmpty(name)) {
                filteredNames.add(name);
            }
        }
        System.out.println(filteredNames); // Output: ["Alice", "Bob", "Charlie"]

        // 3. 缓存工具
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
        cache.put("key", "value");
        System.out.println(cache.getIfPresent("key")); // Output: "value"

        // 4. 事件总线
        EventBus eventBus = new EventBus();
        eventBus.register(new EventListener());
        eventBus.post(new TestEvent("Hello, Guava!"));

        // 5. 并发工具
        ListenableFuture<String> future = ListenableFutureTask.create(() -> "Hello, future!");
        System.out.println(future.get()); // Output: "Hello, future!"

        // 6. 数学工具
        int result = IntMath.checkedAdd(2, 3);
        System.out.println(result); // Output: 5

        // 7. 网络工具
        HostAndPort hostAndPort = HostAndPort.fromString("localhost:8080");
        System.out.println(hostAndPort.getHost()); // Output: "localhost"

        // 8. I/O 工具
        File file = new File("test.txt");
        Files.write("Hello, Guava!", file, com.google.common.base.Charsets.UTF_8);
        System.out.println(Files.toString(file, com.google.common.base.Charsets.UTF_8)); // Output: "Hello, Guava!"
    }

    static class EventListener {
        @com.google.common.eventbus.Subscribe
        public void listen(TestEvent event) {
            System.out.println(event.getMessage());
        }
    }

    static class TestEvent {
        private final String message;

        TestEvent(String message) {
            this.message = message;
        }

        String getMessage() {
            return message;
        }
    }
}

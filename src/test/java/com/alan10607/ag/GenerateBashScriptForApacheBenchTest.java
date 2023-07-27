package com.alan10607.ag;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class GenerateBashScriptForApacheBenchTest {
    private static final String BASH_FILE_PATH = "src/test/ab/";
    private static final String OUTPUT_FILE_PATH = "src/test/ab/out";
    private static final String BODY_FILE_PATH = "src/test/ab/body";
    private static final String COMMON_FILE_NAME = "ab_common.sh";
    private static final String TOKEN_FILE_NAME = "token.txt";

    private static final String GET_CONCURRENCY = "10000";
    private static final String GET_NUMBER = "100";
    private static final String POST_CONCURRENCY = "1000";
    private static final String POST_NUMBER = "10";
    private static final String TIME = "60";

    private static final String $HOST = "$HOST";
    private static final String $BEARER = "$BEARER";
    private static final String $NOW = "$NOW";
    private static final String $ID = "$ID";
    private static final String $NO = "$NO";

    private static final Map<String, String> VARIABLE_MAPPING = Map.of(
            "\\{id\\}", "\\" + $ID,
            "\\{no\\}", "\\" + $NO
    );

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    public void generateBashScriptForApacheBench() {
        mkdirFolder(BASH_FILE_PATH, OUTPUT_FILE_PATH, BODY_FILE_PATH);
        generateCommonFile();

         Map<RequestMethod, List<String>> methodToPath = getControllerPaths("/forum");
        for(Map.Entry<RequestMethod, List<String>> entry : methodToPath.entrySet()){
            for(String path : entry.getValue()){
                generateEachMethodBash(entry.getKey(), path);
            }
        }
    }

    private Map<RequestMethod, List<String>> getControllerPaths(String prefix){
        Map<RequestMethod, List<String>> methodToPath = new HashMap<>();
        Set<RequestMappingInfo> mappings = handlerMapping.getHandlerMethods().keySet();

        for (RequestMappingInfo mappingInfo : mappings) {
            List<String> paths = mappingInfo.getPathPatternsCondition().getPatterns().stream()
                    .map(pathPattern -> pathPattern.toString())
                    .filter(path -> path.startsWith(prefix))
                    .collect(Collectors.toList());
            Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();

            for (RequestMethod method : methods) {
                methodToPath.computeIfAbsent(method, k -> new ArrayList<>()).addAll(paths);
            }
        }
        return methodToPath;
    }

    private void mkdirFolder(String... folderPaths){
        for(String folderPath : folderPaths){
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
    }

    private void generateCommonFile() {
        String commonFilePath = BASH_FILE_PATH + COMMON_FILE_NAME;
        File commonFile = new File(commonFilePath);
        if(commonFile.exists()) return;

        StringBuffer script = new StringBuffer("#!/bin/bash\n\n")
                .append("clear\n")
                .append("HOST=https://localhost\n")
                .append("NOW=$(date +\"%Y-%m-%d.%H:%M:%S\")\n")
                .append("read -r BEARER < \"" + TOKEN_FILE_NAME + "\"\n")
                .append("ID=\n")
                .append("NO=0\n");

        writeFile(commonFilePath, script.toString());
    }

    private void generateEachMethodBash(RequestMethod method, String path) {
        String fileName = getFileName(method, path);
        String filePath = BASH_FILE_PATH + fileName + ".sh";
        String script = getScript(method, path, fileName);
        writeFile(filePath, script);
    }

    private void writeFile(String filePath, String data){
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath);
            fileWriter.write(data);
            log.info("\n{} ->\n{}", filePath, data);
        } catch (IOException e) {
            log.error("", e);
        }finally {
            if(fileWriter != null){
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
    }

    private String formatPath(String path){
        for(Map.Entry<String, String> entry : VARIABLE_MAPPING.entrySet()){
            path = path.replaceAll(entry.getKey(), entry.getValue());
        }
        return path;
    }

    private String getFileName(RequestMethod method, String path){
        return method.name() + ":" + path.replaceAll("/", "_");
    }

    private String getScript(RequestMethod method, String path, String fileName){
        StringBuffer script = new StringBuffer("#!/bin/bash\n\n")
                .append("source ").append(COMMON_FILE_NAME).append("\n\n")
                .append(getApacheBenchScript(method, path, fileName))
                .append(" 2>&1 | tee \"out/")
                .append(fileName)
                .append("." + $NOW + ".txt\"");
        return script.toString();
    }

    private String getApacheBenchScript(RequestMethod method, String path, String fileName){
        String[] ab = new String[0];
        path = formatPath(path);
        switch(method) {
            case GET:
                ab = new String[]{ "ab", "-n", GET_NUMBER,
                        "-c", GET_CONCURRENCY,
                        "-t", TIME,
                        "-H", "\"Authorization: Bearer " + $BEARER + "\"",
                        $HOST + path };
                break;
            case POST:
                ab = new String[]{ "ab", "-n", POST_NUMBER,
                        "-c", POST_CONCURRENCY,
                        "-t", TIME,
                        "-H", "\"Authorization: Bearer " + $BEARER + "\"",
                        "-T", "application/json",
                        "-p", "body/" + fileName + ".json",
                        $HOST + path };
                break;
        }

        return String.join(" ", ab);
    }


}
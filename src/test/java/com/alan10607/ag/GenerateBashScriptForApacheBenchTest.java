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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class GenerateBashScriptForApacheBenchTest {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    private static final String CONTROLLER_PATH = "/forum";
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

    private Map<String, String> variableMap = new HashMap<>();

    @Test
    public void generateBashScriptForApacheBench() {
        mkdirFolder(BASH_FILE_PATH, OUTPUT_FILE_PATH, BODY_FILE_PATH);

        Map<RequestMethod, List<String>> methodToPath = getControllerPaths(CONTROLLER_PATH);
        for(Map.Entry<RequestMethod, List<String>> entry : methodToPath.entrySet()){
            for(String path : entry.getValue()){
                generateEachMethodBash(entry.getKey(), path);
            }
        }

        generateCommonFile();
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
                .append("read -r BEARER < \"" + TOKEN_FILE_NAME + "\"\n");

        variableMap.put("HOST", "https://localhost");
        variableMap.put("NOW", "$(date +\"%Y-%m-%d.%H:%M:%S\")");
        variableMap.forEach((k, v) -> script.append(k).append("=").append(v).append("\n"));

        writeFile(commonFilePath, script.toString());
    }

    private void generateEachMethodBash(RequestMethod method, String path) {
        String fileName = getFileName(method, path);
        String script = getScript(method, path, fileName);
        String filePath = BASH_FILE_PATH + fileName + ".sh";
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

    private String getFileName(RequestMethod method, String path){
        return method.name() + ":" + path.replaceAll("/", "_");
    }

    private String getScript(RequestMethod method, String path, String fileName){
        StringBuffer script = new StringBuffer("#!/bin/bash\n\n")
                .append(getSourceScript()).append("\n\n")
                .append(getApacheBenchScript(method, path, fileName)).append(" \n")
                .append(getTeeScript(fileName));
        return script.toString();
    }

    private String getSourceScript(){
        return String.format("source %s", COMMON_FILE_NAME);
    }

    private String getTeeScript(String fileName){
        return String.format("2>&1 | tee \"out/%s.$NOW.txt\"", fileName);
    }

    private String getApacheBenchScript(RequestMethod method, String path, String fileName){
        String formattedPath = formatPath(path);
        switch(method) {
            case GET:
                return String.format("ab -n %s -c %s -t %s -H \"Authorization: Bearer $BEARER\" $HOST%s",
                        GET_NUMBER, GET_CONCURRENCY, TIME, formattedPath);
            case POST:
            case PATCH:
                return String.format("ab -n %s -c %s -t %s -H \"Authorization: Bearer $BEARER\" -T application/json -p \"body/%s.json\" $HOST%s",
                        POST_NUMBER, POST_CONCURRENCY, TIME, fileName, formattedPath);
            case PUT:
                return String.format("ab -n %s -c %s -t %s -H \"Authorization: Bearer $BEARER\" -T application/json -u \"body/%s.json\" $HOST%s",
                        POST_NUMBER, POST_CONCURRENCY, TIME, fileName, formattedPath);
            default:
                return "exit #Not support";
        }
    }

    private String formatPath(String path){
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(path);
        StringBuffer formattedPath = new StringBuffer();
        while (matcher.find()) {
            String variable = matcher.group(1);
            matcher.appendReplacement(formattedPath, "\\$" + variable);
            variableMap.put(variable, "");
        }
        matcher.appendTail(formattedPath);

        return formattedPath.toString();
    }

}
package com.ssafy.codingtest.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Scanner;

@Service
public class CodeServiceImpl implements CodeService {

    @Override
    public void makeFile(String code) {
        String filePath = "Solution.java";
        try {
            Files.write(Paths.get(filePath), code.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean evaluate() {
        String outputFilePath = "output.txt";
        try {
            // output.txt 파일 읽기
            List<String> outputLines = Files.readAllLines(Paths.get(outputFilePath));

            // Docker 컨테이너 command
            String containerName = "codingtest-container";
            String dockerBuildCommand = "docker build -t codingtest-image .";
            String dockerRunCommand = "docker run --name " + containerName + " -d codingtest-image";
            String dockerCopyResultCommand = "docker cp " + containerName + ":/usr/src/app/result.txt ./result.txt";
            String dockerStopCommand = "docker stop " + containerName;
            String dockerRemoveCommand = "docker rm " + containerName;

            // 기존 컨테이너 중지 및 삭제 : container conflict 방지
            executeCommand(dockerStopCommand);
            executeCommand(dockerRemoveCommand);

            executeCommand(dockerBuildCommand);
            executeCommand(dockerRunCommand);

            // copy 전에 컨테이너가 종료되지 않도록 wait 걸기
            waitForContainer(containerName);

            // result.txt가 존재한다면 삭제
            Files.deleteIfExists(Paths.get("result.txt"));

            executeCommand(dockerCopyResultCommand);
            executeCommand(dockerStopCommand);
            executeCommand(dockerRemoveCommand);

            // 예상 출력과 결과 비교
            List<String> actualResults = Files.readAllLines(Paths.get("result.txt"));
            for (int i = 0; i < outputLines.size(); i++) {
                if (!outputLines.get(i).equals(actualResults.get(i))) {
                    return false; // 결과가 예상 출력과 다르면 실패
                }
            }
            return true; // 모든 결과가 예상 출력과 일치하면 성공

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 쉘 명령어 실행 메서드
    private void executeCommand(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        try (Scanner scanner = new Scanner(process.getInputStream())) {
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
    }

    // 컨테이너가 종료될 때까지 대기하는 메서드
    private void waitForContainer(String containerName) throws IOException {
        String command = "docker wait " + containerName;
        executeCommand(command);
    }
}

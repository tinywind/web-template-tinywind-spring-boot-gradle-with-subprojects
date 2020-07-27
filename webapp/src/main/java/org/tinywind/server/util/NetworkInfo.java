package org.tinywind.server.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * MAC Address를 알아낸다. JDK 1.6 version에서는 MAC Address를 구하는 메소드를 제공하지만 그 전의 버전에서는
 * 메소드를 제공하지 않으므로 따로 만들어주어야 한다. 여기에서는 리눅스와 윈도우 환경에서 MAC Address를 구하는 방법을 제공한다.
 * 참고 소스 (http://forums.sun.com/thread.jspa?threadID=245711)
 * 출처:http://haneulnoon.tistory.com/40 [haneulnoon's Diary]
 *
 * @author haneulnoon
 * @since 2009-06-16
 */
public final class NetworkInfo {
    /**
     * 현재 컴퓨터의 맥 주소를 리턴한다.(맥 주소 중에 '-'를 제거한다.)
     */
    public static String getShortMacAddress() {
        String value = "";

        try {
            value = getMacAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        value = value.replaceAll("-", "");

        return value;
    }

    /**
     * 현재 컴퓨터의 맥 주소를 리턴한다.
     */
    public static String getMacAddress() throws IOException {
        String os = System.getProperty("os.name");

        if (os.startsWith("Windows")) {
            return ParseMacAddress(windowsRunIpConfigCommand());
        } else if (os.startsWith("Linux")) {
            return ParseMacAddress(linuxRunIfConfigCommand());
        } else {
            throw new IOException("unknown operating system: " + os);
        }
    }

    /**
     * Linux 에 있는 네트워크 설정 값들을 문자열로 불러온다.
     */
    private static String linuxRunIfConfigCommand() throws IOException {
        Process p = Runtime.getRuntime().exec("ifconfig");
        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

        StringBuilder buffer = new StringBuilder();
        for (; ; ) {
            int c = stdoutStream.read();
            if (c == -1)
                break;
            buffer.append((char) c);
        }
        String outputText = buffer.toString();

        stdoutStream.close();

        return outputText;
    }

    /**
     * Windows에 있는 네트워크 설정값들을 문자열로 가져온다.
     */
    private static String windowsRunIpConfigCommand() throws IOException {
        Process p = Runtime.getRuntime().exec("ipconfig /all");
        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

        StringBuilder buffer = new StringBuilder();
        for (; ; ) {
            int c = stdoutStream.read();
            if (c == -1)
                break;
            buffer.append((char) c);
        }
        String outputText = buffer.toString();

        stdoutStream.close();

        return outputText;
    }

    /**
     * 문자열에서  패턴에 맞는 문자열 즉 맥주소를 뽑아낸다.
     *
     * @param text 검사할 문자열
     * @return 맥 주소
     */
    public static String ParseMacAddress(String text) {
        String result = null;
        String[] list = text.split("\\p{XDigit}{2}(-\\p{XDigit}{2}){5}");
        int index = 0;
        for (String str : list) {
            if (str.length() < text.length()) {
                index = str.length();
                result = text.substring(index, index + 17);
                if (!result.equals("00-00-00-00-00-00")) {
                    break;
                }
                text = text.substring(index + 17);

            }
        }
        return result;
    }
}



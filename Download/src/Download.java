import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class Download {
    public static String getDownloadUrl(String versionName) {
        //網址
        String apiurl = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
        try {
            //下載JSON字串
            HttpURLConnection api = (HttpURLConnection) new URL(apiurl).openConnection();
            api.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(api.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();
            //解析
            JSONObject root = new JSONObject(jsonBuilder.toString());
            JSONArray versions = root.getJSONArray("versions");
            String versionUrl = null;

            // 找到用戶輸入的版本
            for (int i = 0; i < versions.length(); i++) {
                JSONObject obj = versions.getJSONObject(i);
                if (obj.getString("id").equals(versionName)) {
                    versionUrl = obj.getString("url");
                    break;
                }
            }

            if (versionUrl == null) {
                System.out.println("找不到指定版本！");
                return null;
            }

            // 再取得該版本的詳細資訊
            HttpURLConnection verConn = (HttpURLConnection) new URL(versionUrl).openConnection();
            verConn.setRequestMethod("GET");
            BufferedReader verReader = new BufferedReader(new InputStreamReader(verConn.getInputStream()));
            StringBuilder verBuilder = new StringBuilder();
            while ((line = verReader.readLine()) != null) {
                verBuilder.append(line);
            }
            verReader.close();

            JSONObject verJson = new JSONObject(verBuilder.toString());
            JSONObject downloads = verJson.getJSONObject("downloads");
            JSONObject server = downloads.getJSONObject("server");
            return server.getString("url");

        } catch (Exception e){
            System.out.println("錯誤：" + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("請輸入你要下載的 Minecraft sever 版本(例:1.20.0):");
        String version = scanner.nextLine();

        String downloadUrl = getDownloadUrl(version);
        if (downloadUrl == null) {
            System.out.println("未取得下載連結！");
            return;
        }

        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            int responseCode = httpConn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("連線失敗，回傳碼：" + responseCode);
            } else {
                float contentLength = httpConn.getContentLength();
                //避免過大的備用變數
                int spare = (int)contentLength;
                int flag = 0;
                while (contentLength >= 1024){
                    contentLength /= 1024;
                    flag += 1;
                }
                String s = String.valueOf(contentLength);
                if (s.charAt(s.length() - 1) == '0') {
                    contentLength = (int)contentLength;
                }
                if (flag == 0){
                    System.out.println("檔案大小: " + contentLength + " bytes");
                } else if (flag == 1) {
                    System.out.println("檔案大小: " + contentLength + "KB");
                } else if (flag == 2) {
                    System.out.println("檔案大小: " + contentLength + "MB");
                } else if (flag == 3) {
                    System.out.println("檔案大小: " + contentLength + "GB");
                } else if (flag == 4) {
                    System.out.println("檔案大小: " + contentLength + "TB");
                }else {
                    System.out.println("檔案大小: " + spare + " bytes");
                }
                File folder = new File("server");
                if (!folder.exists()) {
                    folder.mkdir();
                }

                FileOutputStream outputStream = new FileOutputStream("server/server.jar");
                InputStream inputStream = httpConn.getInputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                System.out.println("開始下載檔案...");

                while((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                System.out.println("下載完成，檔案存於 server/server.jar");
            }
            httpConn.disconnect();
        } catch (IOException e) {
            System.out.println("下載過程發生錯誤：" + e.getMessage());
        }
    }
}
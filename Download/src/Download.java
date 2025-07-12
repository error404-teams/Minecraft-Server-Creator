import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class Download {
    public static void get_url(String[] args) {
        String apiurl = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
        try {
            HttpURLConnection api = (HttpURLConnection) new URL(apiurl).openConnection();
            api.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(api.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            // 將 JSON 字串轉為記憶體中的物件
            JSONbject json = new JSONObject(jsonBuilder.toString());
        }catch (Exception e){
            System.out.println("錯誤：" + e.getMessage());
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("請輸入你要下載的 Minecraft 版本（例如 1.20.6）：");
        String version = scanner.nextLine();

        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            int responseCode = httpConn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("連線失敗，回傳碼：" + responseCode);
            } else {
                int contentLength = httpConn.getContentLength();
                System.out.println("檔案大小: " + contentLength + " bytes");
                File folder = new File("server");
                if (!folder.exists()) {
                    folder.mkdir();
                }

                FileOutputStream outputStream = new FileOutputStream("server/server.jar");
                InputStream inputStream = httpConn.getInputStream();
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
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
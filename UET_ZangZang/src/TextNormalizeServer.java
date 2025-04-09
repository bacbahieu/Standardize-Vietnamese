import java.io.*;
import java.net.*;

public class TextNormalizeServer {
    public static String normalizeText(String input) {
        // Danh sách tên riêng phổ biến cần viết hoa
        String[] properNouns = {"việt nam", "hà nội", "đà nẵng", "hồ chí minh", "thủ đô", "châu á"};

        // Bước 1: Xóa khoảng trắng đầu/cuối và thay khoảng trắng thừa giữa
        input = input.trim().replaceAll("\\s+", " ");

        // Bước 2: Chuẩn hóa dấu câu (xóa khoảng trắng trước dấu, thêm khoảng trắng sau dấu nếu cần)
        input = input.replaceAll("\\s*([.,!?])\\s*", "$1 ");
        input = input.replaceAll("\\s+", " ");

        // Bước 3: Thêm dấu chấm cuối nếu thiếu
        if (!input.matches(".*[.!?]$")) {
            input += ".";
        }

        // Bước 4: Tách câu và viết hoa chữ đầu mỗi câu
        String[] sentences = input.split("(?<=[.!?])\\s+");
        StringBuilder result = new StringBuilder();
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (!sentence.isEmpty()) {
                result.append(Character.toUpperCase(sentence.charAt(0)));
                if (sentence.length() > 1) {
                    result.append(sentence.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }

        // Bước 5: Viết hoa tên riêng đã biết
        String finalResult = result.toString().trim();
        for (String name : properNouns) {
            String capitalized = "";
            for (String word : name.split(" ")) {
                capitalized += Character.toUpperCase(word.charAt(0)) + word.substring(1) + " ";
            }
            capitalized = capitalized.trim();
            finalResult = finalResult.replaceAll("(?i)\\b" + name + "\\b", capitalized);
        }

        return finalResult.trim();
    }


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Server đang chạy...");

        while (true) {
            Socket socket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            String inputText = in.readLine();
            System.out.println("Nhận từ client: " + inputText);

            String normalized = normalizeText(inputText);
            out.write(normalized);
            out.newLine();
            out.flush();

            socket.close();
        }
    }
}

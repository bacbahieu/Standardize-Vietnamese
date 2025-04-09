import java.io.*;
import java.net.*;
import java.util.regex.Pattern;

public class TextNormalizeServer {

    // Hàm chuẩn hóa văn bản nhận đầu vào là một chuỗi
    public static String normalizeText(String input) {
        // Danh sách tên riêng phổ biến cần viết hoa
        String[] properNouns = {"việt nam", "hà nội", "đà nẵng", "hồ chí minh", "thủ đô", "châu á"};

        // Bước 1: Loại bỏ khoảng trắng ở đầu và cuối chuỗi, sau đó thay thế các khoảng trắng liên tiếp bằng một khoảng trắng đơn
        input = input.trim().replaceAll("\\s+", " ");

        // Bước 2: Chuẩn hóa dấu câu:
        // - Xóa khoảng trắng trước và sau các ký tự dấu câu (dấu chấm, dấu phẩy, dấu chấm than, dấu hỏi)
        // - Thêm khoảng trắng sau dấu câu nếu cần
        input = input.replaceAll("\\s*([.,!?])\\s*", "$1 ");
        // Đảm bảo rằng không còn khoảng trắng thừa sau khi xử lý dấu câu
        input = input.replaceAll("\\s+", " ");

        // Bước 3: Cắt bỏ khoảng trắng dư thừa cuối chuỗi và kiểm tra dấu kết thúc
        input = input.trim();
        if (!input.matches(".*[.!?]$")) {
            input += ".";
        }

        // Bước 4: Tách văn bản thành các câu dựa trên dấu câu kết thúc (chấm, dấu chấm than, dấu hỏi)
        String[] sentences = input.split("(?<=[.!?])\\s+");
        StringBuilder result = new StringBuilder();
        for (String sentence : sentences) {
            // Loại bỏ khoảng trắng thừa ở đầu và cuối câu
            sentence = sentence.trim();
            if (!sentence.isEmpty()) {
                // Viết hoa chữ cái đầu của câu
                result.append(Character.toUpperCase(sentence.charAt(0)));
                // Chuyển các ký tự còn lại của câu về chữ thường
                if (sentence.length() > 1) {
                    result.append(sentence.substring(1).toLowerCase());
                }
                // Thêm một khoảng trắng sau câu để ngăn cách các câu khi ghép lại
                result.append(" ");
            }
        }

        // Bước 5: Viết hoa các tên riêng đã biết trong danh sách properNouns
        String finalResult = result.toString().trim();
        for (String name : properNouns) {
            // Tách tên riêng thành các từ và viết hoa chữ cái đầu của từng từ
            String capitalized = "";
            for (String word : name.split(" ")) {
                capitalized += Character.toUpperCase(word.charAt(0)) + word.substring(1) + " ";
            }
            // Loại bỏ khoảng trắng cuối của chuỗi đã được viết hoa
            capitalized = capitalized.trim();
            // Sử dụng regex để thay thế các từ khớp chính xác (không phân biệt chữ hoa chữ thường) với tên gốc
            finalResult = finalResult.replaceAll("(?i)\\b" + Pattern.quote(name) + "\\b", capitalized);
        }

        // Trả về chuỗi đã được chuẩn hóa hoàn chỉnh
        return finalResult.trim();
    }

    public static void main(String[] args) throws IOException {
        // Khởi tạo ServerSocket chạy tại cổng 9999
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Server đang chạy...");

        // Vòng lặp vô hạn để server luôn sẵn sàng chấp nhận kết nối từ client
        while (true) {
            // Chờ và chấp nhận kết nối từ client (lệnh này sẽ chặn cho đến khi có client kết nối)
            Socket socket = serverSocket.accept();

            // Tạo luồng đọc (BufferedReader) để nhận dữ liệu gửi từ client, sử dụng UTF-8 cho hỗ trợ tiếng Việt
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            // Tạo luồng ghi (BufferedWriter) để gửi dữ liệu về client, cũng sử dụng UTF-8
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            // Đọc một dòng dữ liệu từ client
            String inputText = in.readLine();
            System.out.println("Nhận từ client: " + inputText);

            // Gọi hàm normalizeText để chuẩn hóa văn bản nhận được
            String normalized = normalizeText(inputText);

            // Gửi kết quả chuẩn hóa về cho client
            out.write(normalized);
            out.newLine();
            out.flush();

            // Sau khi gửi kết quả xong, đóng kết nối với client hiện tại
            socket.close();
        }
    }
}

import java.io.*;
import java.net.*;

public class TextNormalizeClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9999);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
        System.out.print("Nhập đoạn văn bản tiếng Việt: ");
        String text = userInput.readLine();

        out.write(text);
        out.newLine();
        out.flush();

        String response = in.readLine();
        System.out.println("Văn bản sau khi chuẩn hóa: ");
        System.out.println(response);

        socket.close();
    }
}

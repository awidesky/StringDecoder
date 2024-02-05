package stringDecoder;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
	
	public static final List<Charset> charsets = List.of(StandardCharsets.UTF_8,
														 StandardCharsets.UTF_16,
														 StandardCharsets.UTF_16BE,
														 StandardCharsets.UTF_16LE,
														 StandardCharsets.US_ASCII,
														 Charset.forName("EUC-KR")
														 );

	public static void main(String[] args) throws IOException {
		if(args.length != 0) {
			
		}
		 
		File file = File.createTempFile("StringDecoder", "" + System.currentTimeMillis());
		System.out.println("Write the binry/hexadecimal string, save the file, and press enter in console.");
		Desktop.getDesktop().open(file);
		try(Scanner s = new Scanner(System.in)) { s.nextLine(); }
		
		String str = Files.lines(file.toPath()).collect(Collectors.joining());
		String whiteSpaceDeleted = str.replaceAll("\\s", "");
		file.delete();
		file.deleteOnExit();
		
		IntStream unicodes = null;
		byte[] bytes = null;
		
		if(whiteSpaceDeleted.matches("[01]{1,}")) {
			System.out.println("Input is a binary string...");
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			for(int i = 0; i+8 < whiteSpaceDeleted.length(); i+= 8) {
				String arr = whiteSpaceDeleted.substring(i, i + 8);
				bao.write((byte)Integer.parseInt(arr, 2));
			}
			bytes = bao.toByteArray();
			if(Arrays.stream(str.split("\\s")).noneMatch(s -> s.length() > 32)) {
				unicodes = Arrays.stream(str.split("\\s")).mapToInt(s -> Integer.parseInt(s, 2));
			}
		} else if(whiteSpaceDeleted.matches("\\p{XDigit}+")) {
			System.out.println("Input is a hexadecimal string...");
			bytes = HexFormat.of().parseHex(whiteSpaceDeleted);
			if(Arrays.stream(str.split("\\s")).noneMatch(s -> s.length() > 32)) {
				unicodes = Arrays.stream(str.split("\\s")).mapToInt(s -> Integer.parseInt(s, 16));
			}
		} else {
			System.out.println("Invalid input!");
		}
		
		final byte[] b1 = bytes;
		final IntStream unicodes1 = unicodes;
		charsets.forEach(ch -> {
			System.out.println(ch.name() + " : \"" + new String(b1, ch) + "\"");
		});
		System.out.println("Unicode codepoints : \"" + unicodes1.mapToObj(Character::toChars).map(String::valueOf).collect(Collectors.joining()) + "\"");
		//l.stream().map(Main::parse);
	}

}

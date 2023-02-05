package chucknorris;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static final int BITS_PER_CHAR = 7;

    private static String binaryValue(char c) {
        var binaryRepresentation = Integer.toBinaryString(c);
        return String.format("%s%s",
                "0000000".substring(0, BITS_PER_CHAR - binaryRepresentation.length()),
                binaryRepresentation);
    }

    private static String binaryValue(String s) {
        var output = new StringBuilder();
        for (char ch : s.toCharArray()) {
            output.append(binaryValue(ch));
        }
        return output.toString();
    }

    public static String encodeChuckNorris(String s) {
        if (s == null || s.isEmpty()) return "";
        var binaryString = binaryValue(s).toCharArray();
        var output = new StringBuilder();
        output.append(binaryString[0] == '1' ? "0 0" : "00 0");
        for (int i = 1; i < binaryString.length; i++) {
            boolean digitChange = binaryString[i - 1] != binaryString[i];
            if (digitChange) {
                output.append(binaryString[i] == '1' ? " 0 0" : " 00 0");
            } else {
                output.append("0");
            }
        }
        return output.toString();
    }

    private static String decodeChuckNorrisToBinary(String s) {
        if (s.isEmpty()) {
            return "";
        }
        final var regex = "\\b(?<prefix>0|(00))\\s(?<message>0+)\\s?";
        if (!s.matches("(%s)+".formatted(regex))) {
            throw new IllegalArgumentException(
                    "String must be in the Chuck Norris format: \"((0|00) 0+)+\"");
        }
        final var pattern = Pattern.compile(regex);
        final var matcher = pattern.matcher(s);
        final var sb = new StringBuilder();
        // for every match of 0 or 00 followed by any number of 0s
        while (matcher.find()) {
            final var prefix = matcher.group("prefix");
            final var message = matcher.group("message");
            // If the prefix is 0, we have ones, else we have zeroes.
            // How many? As many as there are 0s in the message group.
            sb.append("0".equals(prefix) ?
                    "1".repeat(message.length()) :
                    "0".repeat(message.length()));
        }
        return sb.toString();
    }

    private static String decodeBinaryToString(String binaryRepresentation) {
        if (binaryRepresentation.length() % BITS_PER_CHAR != 0) {
            throw new IllegalArgumentException("Binary representation must contain [a multiple of 7] digits.");
        }
        final var pattern = Pattern.compile("[01]{%d}".formatted(BITS_PER_CHAR));
        final var matcher = pattern.matcher(binaryRepresentation);
        final var sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(Character.toString(Integer.parseInt(matcher.group(), 2)));
        }
        return sb.toString();
    }


    public static String decodeChuckNorris(String s) {
        return decodeBinaryToString(decodeChuckNorrisToBinary(s));
    }

    public static void main(String[] args) {
        final var scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please input operation (encode/decode/exit):");
            final var operation = scanner.nextLine();
            switch (operation) {
                case "encode" -> {
                    System.out.println("Input string:");
                    final var stringToEncode = scanner.nextLine();
                    final var encodedString = encodeChuckNorris(stringToEncode);
                    System.out.println("Encoded string:");
                    System.out.println(encodedString);
                    System.out.println();
                }
                case "decode" -> {
                    System.out.println("Input encoded string:");
                    final var stringToDecode = scanner.nextLine();
                    try {
                        final var decodedString = decodeChuckNorris(stringToDecode);
                        System.out.println("Decoded string:");
                        System.out.println(decodedString);
                    } catch (IllegalArgumentException iae) {
                        System.out.println("Encoded string is not valid.");
                    } finally {
                        System.out.println();
                    }
                }
                case "exit" -> {
                    System.out.println("Bye!");
                    System.exit(0);
                }
                default -> {
                    System.out.printf("There is no '%s' operation\n", operation);
                    System.out.println();
                }
            }
        }
    }
}
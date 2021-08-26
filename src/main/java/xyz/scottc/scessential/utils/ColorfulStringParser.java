package xyz.scottc.scessential.utils;



import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Parse the string to an IFormattableTextComponent with proper formats.
 */
public class ColorfulStringParser {

    public static final char COLOR_CHAR = '§';

    private final TextComponent text = new TextComponent("");

    public ColorfulStringParser(String rawString) {
        this(Collections.singletonList(rawString));
    }

    public ColorfulStringParser(List<? extends String> rawStrings) {
        for (String raw : rawStrings) {
            TextComponent formatted = new TextComponent("");
            char[] chars = raw.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char c = chars[j];
                List<ChatFormatting> formatters = new ArrayList<>(5);
                // Get formatters
                while (c == '&' || c == COLOR_CHAR) {
                    formatters.add(fromFormattingCode(chars[j + 1]));
                    j += 2;
                    c = chars[j];
                }
                // Format raw
                int index = j;
                do {
                    index++;
                    if (index == chars.length) break;
                    c = chars[index];
                } while (c != '&' && c != COLOR_CHAR);
                formatted.append(new TextComponent(raw.substring(j, index)).withStyle(this.toArray(formatters)));
                j = index - 1;
            }
            this.text.append(formatted);
            if (rawStrings.size() > 1) {
                this.text.append("\n");
            }
        }
    }

    private ChatFormatting[] toArray(List<ChatFormatting> target) {
        ChatFormatting[] result = new ChatFormatting[target.size()];
        for (int i = 0; i < target.size(); i++) {
            result[i] = target.get(i);
        }
        return result;
    }

    public TextComponent getText() {
        return this.text;
    }

    // Sorry, I dont know the convenient way to get ChatFormatting from formatting code,
    // because that method is private.
    public static ChatFormatting fromFormattingCode(char formattingCode) {
        char c = Character.toString(formattingCode).toLowerCase(Locale.ROOT).charAt(0);
        switch (c) {
            case '0':
                return ChatFormatting.BLACK;
            case '1':
                return ChatFormatting.DARK_BLUE;
            case '2':
                return ChatFormatting.DARK_GREEN;
            case '3':
                return ChatFormatting.DARK_AQUA;
            case '4':
                return ChatFormatting.DARK_RED;
            case '5':
                return ChatFormatting.DARK_PURPLE;
            case '6':
                return ChatFormatting.GOLD;
            case '7':
                return ChatFormatting.GRAY;
            case '8':
                return ChatFormatting.DARK_GRAY;
            case '9':
                return ChatFormatting.BLUE;
            case 'a':
                return ChatFormatting.GREEN;
            case 'b':
                return ChatFormatting.AQUA;
            case 'c':
                return ChatFormatting.RED;
            case 'd':
                return ChatFormatting.LIGHT_PURPLE;
            case 'e':
                return ChatFormatting.YELLOW;
            case 'f':
                return ChatFormatting.WHITE;
            case 'k':
                return ChatFormatting.OBFUSCATED;
            case 'l':
                return ChatFormatting.BOLD;
            case 'm':
                return ChatFormatting.STRIKETHROUGH;
            case 'n':
                return ChatFormatting.UNDERLINE;
            case 'o':
                return ChatFormatting.ITALIC;
            case 'r':
                return ChatFormatting.RESET;
            default:
                return null;
        }
    }

}

package xyz.scottc.scessential.events.motd;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Parser {

    public static final char COLOR_CHAR = '§';

    private final IFormattableTextComponent description = new StringTextComponent("");

    public Parser(List<? extends String> rawStrings) {
        for (String raw : rawStrings) {
            StringTextComponent formatted = new StringTextComponent("");
            char[] chars = raw.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char c = chars[j];
                List<TextFormatting> formatters = new ArrayList<>(5);
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
                formatted.append(new StringTextComponent(raw.substring(j, index)).mergeStyle(this.toArray(formatters)));
                j = index - 1;
            }
            this.description.append(formatted).appendString("\n");
        }
    }

    private TextFormatting[] toArray(List<TextFormatting> target) {
        TextFormatting[] result = new TextFormatting[target.size()];
        for (int i = 0; i < target.size(); i++) {
            result[i] = target.get(i);
        }
        return result;
    }

    public IFormattableTextComponent getDescription() {
        return this.description;
    }

    // Sorry, I dont know the convenient way to get TextFormatting from formatting code,
    // because that method is private.
    public static TextFormatting fromFormattingCode(char formattingCode) {
        char c = Character.toString(formattingCode).toLowerCase(Locale.ROOT).charAt(0);
        switch (c) {
            case '0':
                return TextFormatting.BLACK;
            case '1':
                return TextFormatting.DARK_BLUE;
            case '2':
                return TextFormatting.DARK_GREEN;
            case '3':
                return TextFormatting.DARK_AQUA;
            case '4':
                return TextFormatting.DARK_RED;
            case '5':
                return TextFormatting.DARK_PURPLE;
            case '6':
                return TextFormatting.GOLD;
            case '7':
                return TextFormatting.GRAY;
            case '8':
                return TextFormatting.DARK_GRAY;
            case '9':
                return TextFormatting.BLUE;
            case 'a':
                return TextFormatting.GREEN;
            case 'b':
                return TextFormatting.AQUA;
            case 'c':
                return TextFormatting.RED;
            case 'd':
                return TextFormatting.LIGHT_PURPLE;
            case 'e':
                return TextFormatting.YELLOW;
            case 'f':
                return TextFormatting.WHITE;
            case 'k':
                return TextFormatting.OBFUSCATED;
            case 'l':
                return TextFormatting.BOLD;
            case 'm':
                return TextFormatting.STRIKETHROUGH;
            case 'n':
                return TextFormatting.UNDERLINE;
            case 'o':
                return TextFormatting.ITALIC;
            case 'r':
                return TextFormatting.RESET;
            default:
                return null;
        }
    }

}

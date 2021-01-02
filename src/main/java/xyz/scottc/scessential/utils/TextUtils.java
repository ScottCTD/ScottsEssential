package xyz.scottc.scessential.utils;

import net.minecraft.util.text.*;
import xyz.scottc.scessential.Main;

public class TextUtils {

    public static String getTranslationKey(String beforeModid, String afterModid) {
        beforeModid = beforeModid.endsWith(".") ? beforeModid : beforeModid + ".";
        afterModid = afterModid.startsWith(".") ? afterModid : "." + afterModid;
        return beforeModid + Main.MODID + afterModid;
    }

    public static IFormattableTextComponent getColoredTextFromI18n(Color color, boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return new TranslationTextComponent(translationKey, parameters)
                .setStyle(Style.EMPTY
                        .setColor(color)
                        .setBold(bold)
                        .setUnderlined(underline)
                        .setItalic(italic));
    }

    public static IFormattableTextComponent getWhiteTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(Color.fromTextFormatting(TextFormatting.WHITE), bold, underline, italic, translationKey, parameters);
    }

    public static IFormattableTextComponent getGreenTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(Color.fromTextFormatting(TextFormatting.GREEN), bold, underline, italic, translationKey, parameters);
    }

    public static IFormattableTextComponent getRedTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(Color.fromTextFormatting(TextFormatting.RED), bold, underline, italic, translationKey, parameters);
    }

    public static IFormattableTextComponent getYellowTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(Color.fromTextFormatting(TextFormatting.YELLOW), bold, underline, italic, translationKey, parameters);
    }

    public static IFormattableTextComponent getColoredTextFromString(Color color, boolean bold, boolean underline, boolean italic, String text) {
        return new StringTextComponent(text)
                .setStyle(Style.EMPTY
                        .setColor(color)
                        .setBold(bold)
                        .setUnderlined(underline)
                        .setItalic(italic));
    }

    public static IFormattableTextComponent getGreenTextFromString(boolean bold, boolean underline, boolean italic, String text) {
        return getColoredTextFromString(Color.fromTextFormatting(TextFormatting.GREEN), bold, underline, italic, text);
    }

    public static IFormattableTextComponent getYellowTextFromString(boolean bold, boolean underline, boolean italic, String text) {
        return getColoredTextFromString(Color.fromTextFormatting(TextFormatting.YELLOW), bold, underline, italic, text);
    }

    public static IFormattableTextComponent getWhiteTextFromString(boolean bold, boolean underline, boolean italic, String text) {
        return getColoredTextFromString(Color.fromTextFormatting(TextFormatting.WHITE), bold, underline, italic, text);
    }

    public static String getSeparator(String pattern, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(pattern);
        }
        return builder.toString();
    }

}

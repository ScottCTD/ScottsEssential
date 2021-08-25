package xyz.scottc.scessential.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import xyz.scottc.scessential.Main;

public class TextUtils {

    public static final int TITLE_COLOR = 0x404040;

    private TextUtils() {}

    public static String getTranslationKey(String beforeModid, String afterModid) {
        beforeModid = beforeModid.endsWith(".") ? beforeModid : beforeModid + ".";
        afterModid = afterModid.startsWith(".") ? afterModid : "." + afterModid;
        return beforeModid + Main.MOD_ID + afterModid;
    }

    public static Component getColoredTextFromI18n(TextColor color, boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return new TranslatableComponent(translationKey, parameters)
                .setStyle(Style.EMPTY
                        .withColor(color)
                        .withBold(bold)
                        .setUnderlined(underline)
                        .withItalic(italic));
    }

    public static Component getWhiteTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.fromLegacyFormat(ChatFormatting.WHITE), bold, underline, italic, translationKey, parameters);
    }

    public static Component getGrayTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.fromLegacyFormat(ChatFormatting.GRAY), bold, underline, italic, translationKey, parameters);
    }

    public static Component getContainerNameTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.parseColor(String.valueOf(TITLE_COLOR)), bold, underline, italic, translationKey, parameters);
    }

    public static Component getGreenTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.fromLegacyFormat(ChatFormatting.GREEN), bold, underline, italic, translationKey, parameters);
    }

    public static Component getRedTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.fromLegacyFormat(ChatFormatting.RED), bold, underline, italic, translationKey, parameters);
    }

    public static Component getYellowTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.fromLegacyFormat(ChatFormatting.YELLOW), bold, underline, italic, translationKey, parameters);
    }

    public static Component getColoredTextFromString(TextColor color, boolean bold, boolean underline, boolean italic, String text) {
        return new TextComponent(text)
                .setStyle(Style.EMPTY
                        .withColor(color)
                        .withBold(bold)
                        .setUnderlined(underline)
                        .withItalic(italic));
    }

    public static Component getGreenTextFromString(boolean bold, boolean underline, boolean italic, String text) {
        return getColoredTextFromString(TextColor.fromLegacyFormat(ChatFormatting.GREEN), bold, underline, italic, text);
    }

    public static Component getYellowTextFromString(boolean bold, boolean underline, boolean italic, String text) {
        return getColoredTextFromString(TextColor.fromLegacyFormat(ChatFormatting.YELLOW), bold, underline, italic, text);
    }

    public static Component getWhiteTextFromString(boolean bold, boolean underline, boolean italic, String text) {
        return getColoredTextFromString(TextColor.fromLegacyFormat(ChatFormatting.WHITE), bold, underline, italic, text);
    }

    public static String getSeparator(String pattern, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(pattern);
        }
        return builder.toString();
    }

}

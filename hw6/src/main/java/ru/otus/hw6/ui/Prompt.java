package ru.otus.hw6.ui;

import lombok.RequiredArgsConstructor;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;
import ru.otus.hw6.HwException;

@Component
@RequiredArgsConstructor
public class Prompt implements PromptProvider {
    private final ShellState state;

    @Override
    public AttributedString getPrompt() {
        switch (state.getState()) {
            case ROOT: return new AttributedStringBuilder()
                    .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN).bold())
                    .append("root: ")
                    .toAttributedString();
            case NEW_BOOK: return new AttributedStringBuilder()
                    .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW).bold())
                    .append("root>new-book: ")
                    .toAttributedString();
            case UPDATE_BOOK: return new AttributedStringBuilder()
                    .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA).bold())
                    .append("root>update-book: ")
                    .toAttributedString();
            default: throw new HwException("unexpected getPrompt() switch statement case: " + state);
        }
    }
}

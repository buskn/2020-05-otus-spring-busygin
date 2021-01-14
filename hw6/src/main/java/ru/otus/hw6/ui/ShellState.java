package ru.otus.hw6.ui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShellState {
    public enum State {ROOT, NEW_BOOK, UPDATE_BOOK }

    @Getter
    private State state = State.ROOT;

    @Getter
    private OperationManagement operationCommands;

    public void setState(State state, OperationManagement commands) {
        assert state == State.ROOT || commands != null;
        this.state = state;
        this.operationCommands = commands;
    }
}

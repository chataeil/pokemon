package org.koreait.board.exceptions;

import org.koreait.global.exceptions.scripts.AlertException;
import org.springframework.http.HttpStatus;

public class BoardNotFoundException extends AlertException {
    public BoardNotFoundException() {
        super("NotFound.board", HttpStatus.NOT_FOUND);
        setErrorCode(true);
    }
}

package com.freerider.chimpcode.potatoonmars.Artificial;

import android.text.Editable;
import android.text.Html;
import android.widget.TextView;

/**
 * Created by Bregy on 2/18/17.
 */

public class DebugConsole {

    public TextView board;

    public DebugConsole(TextView board) {
        this.board = board;
        this.board.setText("");
        this.board.setTextSize((float)10.0);
        this.board.setMaxLines(33);
    }

    public void printLogMessage(String ttl, String message) {
        if (this.board.getLineCount() > this.board.getMaxLines()) {
            this.board.setText("");
        }

        String title = "<font color='#EE0000'>" + ttl + ":</font> ";
        this.board.append(Html.fromHtml(title + message, 0));
        this.board.append("\n");

    }

    public void printLogMessageWithDelimiter(String ttl, String message) {
        if (this.board.getLineCount() > this.board.getMaxLines()) {
            this.board.setText("");
        }

        String title = "<font color='#EE0000'>" + ttl + ":</font> ";
        String delimiter = "<font color='#0098AA'>------------------------</font>";
        this.board.append(Html.fromHtml(delimiter, 0));
        this.board.append("\n");
        this.board.append(Html.fromHtml(title + message, 0));
        this.board.append("\n");
        this.board.append(Html.fromHtml(delimiter, 0));
        this.board.append("\n");

    }
}

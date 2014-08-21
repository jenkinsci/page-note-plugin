package org.jenkinsci.plugins.pagenote;

import java.io.IOException;

/**
 * Data object that defines the persistence format.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Comment {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public abstract void save() throws IOException;
}

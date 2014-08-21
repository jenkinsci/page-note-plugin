package org.jenkinsci.plugins.pagenote;

/**
 * Data object that defines the persistence format.
 *
 * @author Kohsuke Kawaguchi
 */
public class Comment {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void save() {
        throw new UnsupportedOperationException();
    }
}

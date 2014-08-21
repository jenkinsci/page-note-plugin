package org.jenkinsci.plugins.pagenote;

import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class CommentStorage {
    /**
     * Loads the comment for the specific key.
     *
     * If the comment doesn't yet exist for the given key, an empty object will be returned.
     */
    public abstract Comment getComment(String key) throws IOException;
}

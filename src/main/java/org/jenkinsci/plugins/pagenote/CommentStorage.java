package org.jenkinsci.plugins.pagenote;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class CommentStorage {
    public abstract Comment getComment(String key);
}

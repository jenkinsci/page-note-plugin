package org.jenkinsci.plugins.pagenote;

import com.google.inject.ImplementedBy;
import org.jenkinsci.plugins.pagenote.impl.FileStorage;

import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
@ImplementedBy(FileStorage.class)   // TODO: until the day we add impl pluggability
public abstract class NoteStorage {
    /**
     * Loads the comment for the specific key.
     *
     * If the comment doesn't yet exist for the given key, an empty object will be returned.
     */
    public abstract Note getComment(String key) throws IOException;
}

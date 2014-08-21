package org.jenkinsci.plugins.pagenote.impl;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.XmlFile;
import hudson.util.XStream2;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.pagenote.Comment;
import org.jenkinsci.plugins.pagenote.CommentStorage;

import java.io.File;
import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class FileStorage extends CommentStorage {
    private final File rootDir;

    @Inject
    public FileStorage(Jenkins j) {
        rootDir = new File(j.getRootDir(),"comments");
    }

    @Override
    public Comment getComment(String key) throws IOException {
        XmlFile f = getFileFor(key);
        CommentImpl c = new CommentImpl(this, key);
        if (f.exists()) {
            f.unmarshal(c);
        }

        return c;
    }

    private XmlFile getFileFor(String key) {
        return new XmlFile(XSTREAM,new File(rootDir,key+".xml"));
    }

    public static class CommentImpl extends Comment {
        private transient final FileStorage storage;
        private transient final String key;

        public CommentImpl(FileStorage storage, String key) {
            this.storage = storage;
            this.key = key;
        }

        @Override
        public void save() throws IOException {
            storage.getFileFor(key).write(this);
        }
    }

    private static final XStream2 XSTREAM = new XStream2();
    static {
        XSTREAM.alias("comment",CommentImpl.class);
    }
}

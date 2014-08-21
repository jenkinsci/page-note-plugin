package org.jenkinsci.plugins.pagenote.impl;

import com.google.inject.Inject;
import hudson.XmlFile;
import hudson.util.XStream2;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.pagenote.Comment;
import org.jenkinsci.plugins.pagenote.CommentStorage;

import java.io.File;
import java.io.IOException;

/**
 * {@link CommentStorage} implementation backed by a directory full of files.
 *
 * @author Kohsuke Kawaguchi
 */
public class FileStorage extends CommentStorage {
    private final File rootDir;

    @Inject
    public FileStorage(Jenkins j) {
        rootDir = new File(j.getRootDir(),"comments");
    }

    @Override
    public CommentImpl getComment(String key) throws IOException {
        XmlFile f = getFileFor(key);
        CommentImpl c = new CommentImpl(this, key);
        if (f.exists() && c.canRead()) {
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
            checkWrite();
            getXmlFile().write(this);
        }

        public XmlFile getXmlFile() {
            return storage.getFileFor(key);
        }
    }

    private static final XStream2 XSTREAM = new XStream2();
    static {
        XSTREAM.alias("comment",CommentImpl.class);
    }
}

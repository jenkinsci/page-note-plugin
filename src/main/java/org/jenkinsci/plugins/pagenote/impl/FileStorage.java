package org.jenkinsci.plugins.pagenote.impl;

import com.google.inject.Inject;
import hudson.XmlFile;
import hudson.util.XStream2;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.pagenote.Note;
import org.jenkinsci.plugins.pagenote.NoteStorage;

import java.io.File;
import java.io.IOException;

/**
 * {@link NoteStorage} implementation backed by a directory full of files.
 *
 * @author Kohsuke Kawaguchi
 */
public class FileStorage extends NoteStorage {
    private final File rootDir;

    @Inject
    public FileStorage(Jenkins j) {
        rootDir = new File(j.getRootDir(),"comments");
    }

    @Override
    public NoteImpl getComment(String key) throws IOException {
        XmlFile f = getFileFor(key);
        NoteImpl c = new NoteImpl(this, key);
        if (f.exists() && c.canRead()) {
            f.unmarshal(c);
        }

        return c;
    }

    private XmlFile getFileFor(String key) {
        return new XmlFile(XSTREAM,new File(rootDir,key+".xml"));
    }

    public static class NoteImpl extends Note {
        private transient final FileStorage storage;
        private transient final String key;

        public NoteImpl(FileStorage storage, String key) {
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
        XSTREAM.alias("comment",NoteImpl.class);
    }
}

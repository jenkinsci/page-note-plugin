package org.jenkinsci.plugins.pagenote.impl;

import org.jenkinsci.plugins.pagenote.impl.FileStorage.NoteImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * @author Kohsuke Kawaguchi
 */
public class FileStorageTest extends Assert {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    private FileStorage storage;

    @Before
    public void setUp() {
        storage = new FileStorage(j.jenkins);
    }

    @Test
    public void loadSave() throws Exception {
        NoteImpl c = storage.getComment("abc");
        c.setText("Hello world");
        c.save();

        System.out.println(c.getXmlFile().asString());

        c = storage.getComment("abc");
        assertEquals("Hello world",c.getText());
    }
}

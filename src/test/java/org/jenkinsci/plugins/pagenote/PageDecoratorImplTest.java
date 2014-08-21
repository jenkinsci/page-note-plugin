package org.jenkinsci.plugins.pagenote;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.inject.Inject;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import hudson.model.PageDecorator;
import hudson.model.User;
import jenkins.model.Jenkins;
import junit.framework.Assert;
import org.jenkinsci.plugins.pagenote.impl.FileStorage;
import org.jenkinsci.plugins.pagenote.impl.FileStorage.NoteImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.DummySecurityRealm;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.TestExtension;

import java.io.IOException;
import java.util.concurrent.Callable;

public class PageDecoratorImplTest extends Assert {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Inject
    PageDecoratorImpl pageDecorator;

    @Before
    public void setUp() {
        j.jenkins.getInjector().injectMembers(this);
    }

    @Test
    public void key() throws Exception {
        WebClient wc = j.createWebClient();
        FreeStyleProject foo = j.createFreeStyleProject("foo");
        FreeStyleBuild b1 = foo.scheduleBuild2(0).get();
        FreeStyleBuild b2 = foo.scheduleBuild2(0).get();

        // basic objects should have different keys
        assertNotEquals( pageKeyOf(wc.getPage(b1)), pageKeyOf(wc.getPage(b2)));
        assertNotEquals(pageKeyOf(wc.getPage(b1)), pageKeyOf(wc.getPage(foo)));

        ListView v1 = addView(foo, "v1");
        ListView v2 = addView(foo, "v2");

        // the same job in different views should have the same key
        assertEquals(pageKeyOf(wc.getPage(v1, "job/foo")), pageKeyOf(wc.getPage(v2, "job/foo")));

        // same build under different permalinks should have the same key
        assertEquals(pageKeyOf(wc.getPage(b2)), pageKeyOf(wc.getPage(foo, "lastSuccessfulBuild")));
    }

    private ListView addView(FreeStyleProject foo, String name) throws IOException {
        ListView v = new ListView(name);
        j.jenkins.addView(v);
        v.add(foo);
        return v;
    }

    private void assertNotEquals(String s1, String s2) {
        assertTrue("Expected to be unequal but "+s1, !s1.equals(s2));
    }

    private String pageKeyOf(HtmlPage page) {
        String c = page.getElementById("key").getTextContent();
        assertTrue(!c.trim().isEmpty());
        return c;
    }

    /**
     * Encode the key to the page to test its value.
     */
    @TestExtension
    public static class KeyDecoratorImpl extends PageDecorator {
        @Inject
        PageDecoratorImpl subject;

        public String getKey() {
            return subject.key();
        }
    }

    @Test
    public void author() throws Exception {
        DummySecurityRealm s = j.createDummySecurityRealm();
        j.jenkins.setSecurityRealm(s);
        WebClient wc = j.createWebClient();

        final FileStorage f = new FileStorage(j.jenkins);

        wc.login("alice");
        wc.executeOnServer(new Callable<Void>() {
            public Void call() throws Exception {
                NoteImpl c = f.getComment("abc");
                c.setText("Hello world");
                c.save();
                return null;
            }
        });

        wc.login("bob");
        wc.executeOnServer(new Callable<Void>() {
            public Void call() throws Exception {
                NoteImpl c = f.getComment("abc");
                assertEquals(User.get("alice"),c.getAuthor());
                return null;
            }
        });
    }
}
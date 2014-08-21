package org.jenkinsci.plugins.pagenote;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.Util;
import hudson.model.Item;
import hudson.model.PageDecorator;
import hudson.model.Run;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class PageDecoratorImpl extends PageDecorator {
    @Inject
    NoteStorage storage;

    /**
     * Returns the comment for the current page.
     *
     * This method is for UI binding, so suppress errors if any is encountered.
     */
    public Note getComment() {
        String k = key();
        try {
            return storage.getComment(k);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load comment for " + k, e);
            return new Note() {
                @Override
                public void save() throws IOException {
                    // do nothing
                }
            };
        }
    }


    /**
     * Given the current page requested, turn that into a handle that identify the page uniquely
     * even when the page is shown under different view, different permalink, etc.
     *
     * <p>
     * This involves some heuristics and is done on best-effort basis.
     */
    public String key() {
        StaplerRequest req = Stapler.getCurrentRequest();

        // Run can be uniquely identified via Job
        Ancestor a = req.findAncestor(Run.class);
        if (a!=null) {
            Run r = (Run)a.getObject();
            return toId(r.getParent().getFullName(), r.getNumber(), a.getRestOfUrl());
        }

        // Job can be uniquely identified via its full name
        a = req.findAncestor(Item.class);
        if (a!=null) {
            Item i = (Item)a.getObject();
            return toId(i.getFullName(), a.getRestOfUrl());
        }

        // we don't have anything to contextualize this
        return toId(req.getAncestors().get(0).getRestOfUrl());
    }

    private String toId(Object... tokens) {
        return Util.getDigestOf(StringUtils.join(tokens,'\0'));
    }

    private static final Logger LOGGER = Logger.getLogger(PageDecoratorImpl.class.getName());
}

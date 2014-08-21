package org.jenkinsci.plugins.pagenote;

import hudson.model.User;
import hudson.security.Permission;
import hudson.security.PermissionGroup;
import hudson.security.PermissionScope;
import jenkins.model.Jenkins;

import java.io.IOException;

/**
 * Data object that defines the persistence format.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Note {
    private String text;
    private String author;

    public String getText() {
        if (canRead())
            return text;
        else
            return null;
    }

    public User getAuthor() {
        return User.get(author!=null ? author : Jenkins.ANONYMOUS.getName());
    }

    public void setText(String text) {
        this.author = User.current().getId();
        this.text = text;
    }

    public abstract void save() throws IOException;

    public boolean canWrite() {
        return Jenkins.getInstance().hasPermission(WRITE);
    }

    public void checkWrite() {
        Jenkins.getInstance().checkPermission(WRITE);
    }

    public boolean canRead() {
        return Jenkins.getInstance().hasPermission(READ);
    }

    public static final PermissionGroup PERMISSIONS = new PermissionGroup(Note.class, Messages._Note_Permissions_Title());
    public static final Permission READ = new Permission(PERMISSIONS,"Read", Messages._Note_ReadPermission_Description(), Permission.READ, PermissionScope.JENKINS);
    public static final Permission WRITE = new Permission(PERMISSIONS,"Write", Messages._Note_WritePermission_Description(), Jenkins.ADMINISTER, PermissionScope.JENKINS);
}

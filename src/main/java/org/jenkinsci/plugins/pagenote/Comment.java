package org.jenkinsci.plugins.pagenote;

import hudson.model.Messages;
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
public abstract class Comment {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
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

    public static final PermissionGroup PERMISSIONS = new PermissionGroup(Comment.class, Messages._Computer_Permissions_Title());
    public static final Permission READ = new Permission(PERMISSIONS,"Read", Messages._Computer_ConfigurePermission_Description(), Permission.READ, PermissionScope.JENKINS);
    public static final Permission WRITE = new Permission(PERMISSIONS,"Write", Messages._Computer_ExtendedReadPermission_Description(), Jenkins.ADMINISTER, PermissionScope.JENKINS);
}

package io.apicurio.bot.actions;

import org.jboss.logging.Logger;

class TriagePullRequest {

    private static final Logger LOG = Logger.getLogger(TriagePullRequest.class);

    private static final String BACKPORTS_BRANCH = "-backports-";

    /**
     * We cannot add more than 100 labels and we have some other automatic labels such as kind/bug.
     */
    private static final int LABEL_SIZE_LIMIT = 95;


}

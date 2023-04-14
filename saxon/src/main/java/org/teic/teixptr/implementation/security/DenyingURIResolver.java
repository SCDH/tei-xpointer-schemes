package org.teic.teixptr.implementation.security;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import net.sf.saxon.Configuration;
import net.sf.saxon.trans.NonDelegatingURIResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@link javax.xml.transform.URIResolver} that exits on all
 * resolutions with an {@link TransformerException}. It can be used to
 * disallow a certain resolution in a general way. It is marked as a
 * {@link NonDelegatingURIResolver} since we do not want other
 * resolvers to take over.
 */
public class DenyingURIResolver implements NonDelegatingURIResolver {

    private static final Logger LOG = LoggerFactory.getLogger(DenyingURIResolver.class);


    public DenyingURIResolver() {
    }

    /**
     * Saxon may try to pass in a configuration.
     */
    public DenyingURIResolver(Configuration config) {
    }

    /**
     * Allways throws an exception.
     */
    @Override
    public Source resolve(String href, String base) throws TransformerException {
	LOG.warn("a transformation tries to resolve {} on the base of {}! Denying", href, base);
	throw new TransformerException("URI not allowed: " + href + "resolved in " + base);
    }

}

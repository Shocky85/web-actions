package org.wm.xml.transform;

import net.sf.saxon.StandardURIResolver;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.trans.XPathException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Caching implementation of JAXP transformer factory.
 * This implementation caches templates that were loaded from local files
 * so that consequent calls to local stylesheets require stylesheet reparsing
 * only if stylesheet was changed.
 * If you wish to use caching transformer factory in your web application,
 * simply copy classes directory to WEB-INF of your application. This will
 * force JAXP to use transformer factory class specified in
 * classes/META-INF/java.xml.transform.TransformerFactory.
 */
public class CachingTransformerFactory extends TransformerFactoryImpl {
  /**
   * File scheme string.
   */
  private static final String FILE_SCHEME = "file";

  /**
   * Map to hold templates cache.
   */
  private static Map templatesCache = Collections.synchronizedMap(new HashMap());

  /**
   * Map to hold template drafts cache.
   */
  private static Map templateDraftsCache =
    Collections.synchronizedMap(new HashMap());

  /**
   * Factory logger.
   */
  protected static Log logger =
    LogFactory.getLog(CachingTransformerFactory.class);

  /**
   * Assigned URI resolver.
   */
  public URIResolver uriResolver;

  /**
   * Interception caching resolver proxy.
   */
  public URIResolver uriResolverProxy = new CachingURIResolver();

  /** Standard URIResolver. */
  //private URIResolver standardURIResolver = new StandardURIResolver();

  /**
   * Caching transformer factory constructor. Calls inherited constructor and
   * sets URIResolver to the intercepting proxy resolver.
   *
   * @see URIResolver
   * @see #setURIResolver
   * @see #getURIResolver
   */
  public CachingTransformerFactory() {
    // Call super constructor
    super();
    // Set intercepting URI resolver
    super.setURIResolver(uriResolverProxy);
  }

  /**
   * Process the source into a Transformer object. If source is a StreamSource
   * with <code>systemID</code> pointing to a file, transformer is produced
   * from a cached templates object. Cache is done in soft references; cached
   * objects are reloaded, when file's date of last modification changes.
   *
   * @param source An object that holds a URI, input stream, etc.
   * @return A Transformer object that may be used to perform a transformation
   *         in a single thread, never null.
   * @throws TransformerConfigurationException
   *          - May throw this during the
   *          parse when it is constructing the Templates object and fails.
   */
  public Transformer newTransformer(final Source source)
    throws TransformerConfigurationException {
    // Check that source is a StreamSource
    if (source instanceof StreamSource)
      try {
        if (null!=source.getSystemId()) {
	  // Create URI of the source
	  final URI uri = new URI(source.getSystemId());
	  // If URI points to a file, load transformer from the file
	  // (or from the cache)
	  if (FILE_SCHEME.equalsIgnoreCase(uri.getScheme()))
	    return getTemplatesCacheEntry(new File(uri)).
	      templates.newTransformer();
        }
      }
      catch (URISyntaxException urise) {
	throw new TransformerConfigurationException(urise);
      }
    return super.newTransformer(source);
  }

  /**
   * Returns a templates cache entry for the specified file.
   *
   * @param file transformer's file.
   * @return Templates cache entry for the given file.
   * @throws TransformerConfigurationException
   *
   */
  private TemplatesCacheEntry getTemplatesCacheEntry(final File file)
    throws TransformerConfigurationException {
    // Get file's absolute path
    final String absoluteFilePath = file.getAbsolutePath();

    // Search the cache for the templates entry
    TemplatesCacheEntry templatesCacheEntry =
      (TemplatesCacheEntry) templatesCache.get(absoluteFilePath);

    // If entry found
    if (templatesCacheEntry != null) {
      // Check, if entry was modified
      if (templatesCacheEntry.isModified()) {
	// If it was, set it to null
	templatesCacheEntry = null;
	// And remove from the cache
	templatesCache.remove(absoluteFilePath);
      }
    }

    // If no templatesEntry is found or this entry was obsolete
    if (templatesCacheEntry == null) {
      logger.debug("Loading transformation [" + file.getAbsolutePath() + "].");
      // If this file does not exists, throw the exception
      if (!file.exists()) {
	throw new TransformerConfigurationException(
	  "Requested transformation [" + file.getAbsolutePath() +
	    "] does not exist.");
      }
      // Create new cache entry
      templatesCacheEntry = new TemplatesCacheEntry(file);
    } else
      logger.debug("Using cached transformation [" + file.getAbsolutePath() + "].");
    return templatesCacheEntry;
  }

  /**
   * Private class to hold templates cache entry.
   */
  private class TemplatesCacheEntry {
    /**
     * When was the cached entry last modified.
     */
    private long lastModified;

    /**
     * Cached templates object.
     */
    private Templates templates;

    /**
     * Templates file object.
     */
    private File templatesFile;

    /**
     * List of imported or included stylesheets (set of cache entries).
     */
    private Set importsSet = new HashSet();

    /**
     * Constructs a new cache entry.
     *
     * @param templatesFile file, from which this transformer is loaded.
     * @throws TransformerConfigurationException
     *          Thrown, if templates could not
     *          be constructed from the given file.
     */
    private TemplatesCacheEntry(final File templatesFile)
      throws TransformerConfigurationException {
      // Assign file
      this.templatesFile = templatesFile;
      // Get timestamp of last modififcation
      this.lastModified = templatesFile.lastModified();
      // Get absolute file path
      final String absoluteFilePath = templatesFile.getAbsolutePath();
      // Temporarily put this entry to a drafts cache
      templateDraftsCache.put(absoluteFilePath, this);
      try {
	// Try loading templates
	templates = newTemplates(new StreamSource(templatesFile));
	// If succeded, put entry to the cache
	templatesCache.put(absoluteFilePath, this);
      }
      finally {
	// Remove entry from the drafts cache
	templateDraftsCache.remove(absoluteFilePath);
      }
      // Evaluate a transitive closure of the imported templates
      final Set importsClosureSet = new HashSet();
      // Iterate over imported templates
      for (final Iterator importsSetIterator = importsSet.iterator();
           importsSetIterator.hasNext();) {
	// Get current cache entry
	final TemplatesCacheEntry templatesCacheEntry =
	  (TemplatesCacheEntry) importsSetIterator.next();
	// Include all templates imported by this entry into the
	// transitive closure
	importsClosureSet.addAll(templatesCacheEntry.importsSet);
      }
      // Add transitive closure to the set of templates imported by this
      // template
      importsSet.addAll(importsClosureSet);
    }

    /**
     * Checks, if templates file was modified.
     *
     * @return <code>true</code> iff templates file was modified.
     */
    public boolean isTemplatesModified() {
      // Compare timesrtamps
      return lastModified < templatesFile.lastModified();
    }

    /**
     * Checks, if templates cache entry was modified. This includes both
     * file and imports check.
     *
     * @return <code>true</code> iff templates cache entry was modified and
     *         should be reloaded.
     */
    public boolean isModified() {
      // Check, if templates were modified
      if (isTemplatesModified())
	return true;
	// Check if imports were modified
      else {
	// Modification flag
	boolean isImportModified = false;
	// Iterate over imports
	for (final Iterator importsSetIterator = importsSet.iterator();
	     !isImportModified && importsSetIterator.hasNext();) {
	  // Get current entry
	  final TemplatesCacheEntry templatesCacheEntry =
	    (TemplatesCacheEntry) importsSetIterator.next();
	  // Check, if it was modified
	  isImportModified |= templatesCacheEntry.isTemplatesModified();
	  logger.debug("Imported template [" +
	    templatesCacheEntry.templatesFile.getAbsolutePath() +
	    "]" + (templatesCacheEntry.isTemplatesModified() ?
	    " was modified." : " was not modified."));
	}
	// Return the flag
	return isImportModified;
      }
    }
  }

  /**
   * Assigns URIresolver. Note that parent implementation receives an
   * instance of intercepting proxy for thjis resolver.
   *
   * @param uriResolver URIResolver to be used for URI resolution.
   */
  public void setURIResolver(final URIResolver uriResolver) {
    this.uriResolver = uriResolver;
  }

  /**
   * Returns resolver used for URI resolution. This implementation returns an
   * instance of intercepting resolver proxy.
   *
   * @return An instance of intercepting resolver proxy
   */
  public URIResolver getURIResolver() {
    return uriResolverProxy;
  }

  /**
   * This URI resolver caches sources that it resolves.
   *
   * @see URIResolver
   */
  private class CachingURIResolver extends StandardURIResolver implements URIResolver {
    /**
     * Resolves <code>href</code> relatively to <code>base</code>. If both
     * imported and base sources are files, this implementation tries to load
     * both of them into the templates cache.
     *
     * @param href Target URI.
     * @param base Base URI.
     * @return <code>null</code>, if no resolver was assigned, or result of URI
     *         resolution using the assigned resolver.
     * @throws XPathException May be thrown by assigned URI resolver.
     *                              throws TransformerException
     */
    public Source resolve(final String href, final String base) throws XPathException {
      logger.debug("Resolving href [" + href + "] relatively to base [" + base + "].");
      // If both href and base are not null
      if (href != null && base != null) {
	try {
	  // Instantiate and resolver URIs
	  final URI baseURI = new URI(base);
	  final URI hrefURI = new URI(href);
	  final URI importURI = baseURI.resolve(hrefURI);
	  // In case both base and href point to files
	  if (FILE_SCHEME.equals(baseURI.getScheme()) &&
	    FILE_SCHEME.equals(importURI.getScheme())) {
	    // Create file instances and get absolute file names
	    final File baseFile = new File(baseURI);
	    final String absoluteBaseFileName = baseFile.getAbsolutePath();
	    final File importFile = new File(importURI);
	    final String absoluteImportFileName = importFile.getAbsolutePath();

	    // Try loading base entry from temporal cache
	    TemplatesCacheEntry baseTemplatesCacheEntry = (TemplatesCacheEntry)
	      templateDraftsCache.get(absoluteBaseFileName);
	    // Try loading base entry from cache
	    if (null == baseTemplatesCacheEntry)
	      try {
		baseTemplatesCacheEntry =
		  getTemplatesCacheEntry(baseFile);
	      }
	      catch (TransformerConfigurationException tcex) {
		logger.warn("Could not load transformer from [" +
		  absoluteBaseFileName + "].", tcex);
	      }

	    // Try loading import entry from temporal cache
	    TemplatesCacheEntry importTemplatesCacheEntry = (TemplatesCacheEntry)
	      templateDraftsCache.get(absoluteImportFileName);
	    // Try loading import entry from cache
	    if (null == importTemplatesCacheEntry)
	      try {
		importTemplatesCacheEntry =
		  getTemplatesCacheEntry(importFile);
	      }
	      catch (TransformerConfigurationException tcex) {
		logger.warn("Could not load transformer from [" +
		  absoluteImportFileName + "].", tcex);
	      }
	    // If both entries were loaded/retrieved from cache add import entry
	    // into the import list of the base entry
	    if (null != baseTemplatesCacheEntry && null != importTemplatesCacheEntry)
	      baseTemplatesCacheEntry.importsSet.add(importTemplatesCacheEntry);
	  }
	}
	catch (URISyntaxException usex) {
	  logger.warn("Bad URI syntax.", usex);
	}
      }
      Source result = null;

      // Try resolving the URI
      if (null != uriResolver) {
        try {
          result = uriResolver.resolve(href, base);
        } catch (TransformerException e) {
          throw (XPathException)e;
        }
      }

      if (result == null) result = super.resolve(href, base);

      return result;
    }
  }
}
/*
 * The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * See the License for the specific language governing rights and limitations under the License.
 *
 * The Original Code is: all this file.
 *
 * The Initial Developer of the Original Code is
 * Aleksei Valikov of Forschungszentrum Informatik (valikov@fzi.de).
 *
 * Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
 *
 * Contributor(s): none.
 */
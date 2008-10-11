import org.exist.storage.DBBroker;
import org.exist.xmldb.XQueryService;
import org.xmldb.api.modules.CollectionManagementService;
import org.exist.util.serializer.SAXSerializer;
import org.exist.util.serializer.SerializerPool;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XUpdateQueryService;
import org.xmldb.api.base.*;
import org.apache.log4j.Logger;

import javax.script.ScriptContext;
import javax.xml.transform.OutputKeys;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.util.Properties;
import java.util.List;
import java.io.StringWriter;

import flex.messaging.messages.RemotingMessage;

/**
 * eXist XML database adapter.
 *
 * @author Ivan Latysh
 * @since 10-Oct-2008 3:45:03 PM
 */
public class EXistAdapter {

  protected static String URI = "xmldb:exist://";

  /** Component logger */
  protected static Logger logger = Logger.getLogger(EXistAdapter.class.getName());

  /** Output properties */
  public static Properties outputProperties = new Properties();
  static {
    outputProperties.setProperty( OutputKeys.INDENT, "yes" );
  }

  /**
   * Action entry point
   *
   * @param context script context
   * @return action result
   */
  public static Object eval(ScriptContext context) {
    // get the original message
    RemotingMessage message = (RemotingMessage) context.getAttribute("message", ScriptContext.ENGINE_SCOPE);
    if (null==message) {
      logger.error("RemotingMessage is null.");
      return null;
    }
    // get response
    HttpServletResponse response = (HttpServletResponse) context.getAttribute("response", ScriptContext.ENGINE_SCOPE);
    // set content type
    response.setContentType("application/xml;UTF-8");
    // get output stream
    ServletOutputStream outputStream = null;
    try {
      outputStream = response.getOutputStream();
      final String operation = message.getOperation();
      final List params = message.getParameters();
      // execute requested operation
      if ("query".equals(operation)) {
        // get the query, if any
        if (params.size()==1) {
          return query(String.valueOf(params.get(0)));
        } else {
          logger.error("Invalid number of arguments, expecting 1, when got {"+params.size()+"}.");
        }
      } else if ("save".equals(operation)) {
        // get parameters
        if (params.size()==3) {
          save(String.valueOf(params.get(0)), String.valueOf(params.get(1)), String.valueOf(params.get(2)));
        } else {
          logger.error("Invalid number of arguments, expecting 3, when got {"+params.size()+"}.");
        }
      } else if ("update".equals(operation)) {
        // get parameters
        if (params.size()==2) {
          update(String.valueOf(params.get(0)), String.valueOf(params.get(1)));
        } else {
          logger.error("Invalid number of arguments, expecting 3, when got {"+params.size()+"}.");
        }
      } else {
        logger.error("Unknown operation {"+operation+"}.");
      }
    } catch (Exception e) {
      logger.error("Error while executing an action.", e);
      throw new RuntimeException(e);
    }

    return null;
  }

  /**
   * Execute given XQuery
   *
   * @param xquery xquery to execute
   * @return serialized result
   */
  public static String query(String xquery) throws Exception {
    Collection col = null;
    StringWriter out = new StringWriter(1024);
    try {
      // get root-collection
      col = getCollection(null);
      // get query-service
      XQueryService service = (XQueryService) col.getService( "XQueryService", "1.0" );
      // set pretty-printing on
      service.setProperty( OutputKeys.INDENT, "yes" );
      service.setProperty( OutputKeys.ENCODING, "UTF-8" );
      // compile
      CompiledExpression compiled = service.compile( xquery );

      long start = System.currentTimeMillis();
      // execute query and get results in ResourceSet
      ResourceSet result = service.execute( compiled );
      long queryTime = System.currentTimeMillis() - start;

      start = System.currentTimeMillis();

      SAXSerializer serializer = null;
      try {
        serializer = (SAXSerializer) SerializerPool.getInstance().borrowObject(SAXSerializer.class);
        serializer.setOutput( out, outputProperties );
        for ( int i = 0; i < (int) result.getSize(); i++ ) {
            XMLResource resource = (XMLResource) result.getResource( (long) i );
            resource.getContentAsSAX(serializer);
        }
      } finally {
        // return serialized
        if (null!=serializer) SerializerPool.getInstance().returnObject(serializer);
      }

      long rtime = System.currentTimeMillis() - start;
      if (logger.isDebugEnabled()) {
        logger.debug("Query \n{" + xquery + "} " +
            "\nResult count=" + result.getSize() +
            "\nQuery time=" + queryTime + "ms." +
            "\nRetrieve time="+rtime);
      }
    } catch (Exception e) {
      logger.error("Error while executing XQuery.", e);
      throw e;
    } finally {
      if (null!=col) col.close();
    }
    return out.toString();
  }

  /**
   * Add given document as a new collection
   *
   * @param path new collection name
   * @param documentID document ID or null for default ID
   * @param document document to save
   */
  public static void save(String path, String documentID, String document) throws Exception {
    Collection col = null;
    try {
      // mark start time
      long start = System.currentTimeMillis();
      // sanity check
      if (null==path) throw new Exception("Document path must not be null.");
      // strip leading `/`
      if (path.startsWith("/")) path = path.substring(1, path.length());
      // get collection
      col = getCollection(path);
      // in collection is not found, create it
      if (null==col) {
        // get root collection
        col = getCollection(null);
        // get collection manageent service collection
        CollectionManagementService mgtService = (CollectionManagementService)col.getService("CollectionManagementService", "1.0");
        // create a new collection
        col = mgtService.createCollection(DBBroker.ROOT_COLLECTION + "/" + path);
      }
      // create new XMLResource
      XMLResource xmlResoruce = (XMLResource)col.createResource(documentID, "XMLResource");
      // set content
      xmlResoruce.setContent(document);
      // store it
      col.storeResource(xmlResoruce);
      // mark completion time
      long saveTime = System.currentTimeMillis() - start;

      if (logger.isDebugEnabled()) {
        logger.debug("Document {" + xmlResoruce.getDocumentId() + "} stored in a collection {"+path+"}" +
            "\nSave time=" + saveTime + "ms.");
      }

    } catch (Exception e) {
      logger.error("Error while storing document.", e);
      throw e;
    } finally {
      if (null!=col) col.close();
    }
  }

  /**
   * Execute XUpdate command
   *
   * @param path collection path
   * @param xupdate update command
   */
  public static void update(String path, String xupdate) throws Exception {
    Collection col = null;
    try {
      // mark start time
      long start = System.currentTimeMillis();
      // sanity check
      if (null==path) throw new Exception("Document path must not be null.");
      // strip leading `/`
      if (path.startsWith("/")) path = path.substring(1, path.length());
      // get collection
      col = getCollection(path);
      // in collection is not found, create it
      if (null==col) {
        throw new Exception("Path {"+path+"} is not valid.");
      }
      // get XUpdate service
      XUpdateQueryService service = (XUpdateQueryService) col.getService("XUpdateQueryService", "1.0");      
      service.update(xupdate);

      // mark completion time
      long executionTime = System.currentTimeMillis() - start;

      if (logger.isDebugEnabled()) {
        logger.debug("XUpdate {" + xupdate + "} command" +
            "\nExecution time=" + executionTime + "ms.");
      }

    } catch (Exception e) {
      logger.error("Error while executing XUpdate command {"+xupdate+"}.", e);
      throw e;
    } finally {
      if (null!=col) col.close();
    }
  }

  /**
   * Lookup and return a collection
   *
   * @return collection
   * @param path relative path
   */
  public static Collection getCollection(String path) throws XMLDBException {
    return DatabaseManager.getCollection(URI + DBBroker.ROOT_COLLECTION + (null!=path ?path :""));
  }

}

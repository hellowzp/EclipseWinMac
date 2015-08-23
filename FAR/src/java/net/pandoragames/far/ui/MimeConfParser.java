package net.pandoragames.far.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pandoragames.far.ui.model.FileType;
import net.pandoragames.far.ui.model.FileType.BUILDIN;
import net.pandoragames.far.ui.model.MimeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
/**
 * Parser-formater for mime type definition xml. It follows the dtd below:
 * <p>
 * &lt;!ELEMENT FileTypes (Binary,Text)&gt;<br>
 * &lt;!ELEMENT Binary (Archive,Document,Mime*)&gt;<br>
 * &lt;!ATTLIST Binary encoding CDATA #FIXED X-CP437&gt;<br>
 * &lt;!ELEMENT Archive (Mime*)&gt;<br>
 * &lt;!ELEMENT Document (Mime*)&gt;<br>
 * <p>
 * &lt;!ELEMENT Text (Source,Table,SGML,Mime*)&gt;<br>
 * &lt;!ATTLIST Text encoding CDATA #IMPLIED&gt;<br>
 * &lt;!ELEMENT Source (Mime*)&gt;<br>
 * &lt;!ELEMENT Table (Mime*)&gt;<br>
 * &lt;!ELEMENT SGML (XML,Mime*)&gt;<br>
 *<p>
 * &lt;!ELEMENT Mime (Mime*)&gt;<br>
 * &lt;!ATTLIST Mime name CDATA #IMPLIED&gt;<br>
 * &lt;!ATTLIST Mime encoding CDATA #IMPLIED&gt;<br>
 * &lt;!ATTLIST Mime extensions CDATA #IMPLIED&gt;<br>
 *
 * @author Olivier Wehner
 * <!--
 *  FAR - Find And Replace
 *  Copyright (C) 2009,  Olivier Wehner

 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  -->
 */
public class MimeConfParser {

	/** Namespace for MimeType XML. */
	protected static final String XMLNS = "http://findandreplace.sourceforge.net/xml/mime/v1";
	/** Name of root node is "FileTypes". */
	protected static final String ROOT_NODE_NAME = "FileTypes";
	/** Name of mime type node is "Mime". */
	protected static final String MIME_NODE_NAME = "Mime";
	
	/**
	 * Read the mime type definition from xml.
	 * @param input xml to be read
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse(InputStream input) throws SAXException, IOException {
		Reader reader = new InputStreamReader( input  );		
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		InputSource xmlInput = new InputSource( reader );
		xmlReader.setContentHandler(new SAXHandler());
		xmlReader.parse(xmlInput);
	}
	
	/**
	 * Format the current mime definitions as xml.
	 * 
	 * @param output where to write to.
	 * @throws IOException
	 */
	public void format(OutputStream output) throws IOException {
		Writer writer = new OutputStreamWriter( output, "UTF-8");
		Map<String,MimeTreeNode> nodeSet = new HashMap<String,MimeTreeNode>();
		MimeTreeNode rootNode = null;
		for( BUILDIN fileTypeID : BUILDIN.values() ) {
			FileType fileType = FileType.getType( fileTypeID.name() );
			MimeTreeNode node = new MimeTreeNode( fileType );
			if( fileTypeID == BUILDIN.FILE ) {
				rootNode = node;
			} else {
				nodeSet.get( fileType.getParentType().getName() ).getChildren().add( node );
			}
			nodeSet.put(node.getName(), node);
		}
		List<MimeType> mimelist = MimeType.MimeRegistry.listAll();
		Collections.sort(mimelist, new Comparator<MimeType>() {
			public int compare(MimeType a, MimeType b) {
				return a.getName().compareTo(b.getName());
			}			
		});
		for( MimeType mimeType : mimelist ) {
			addMimeType( mimeType, nodeSet );
		}	
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		writer.append('\n');
		writeNode( rootNode, writer, 0 );
		writer.flush();
	}
	
	private void addMimeType(FileType mimeType, Map<String,MimeTreeNode> nodeSet) {
		MimeTreeNode node = new MimeTreeNode( mimeType );
		if( ! nodeSet.containsKey( mimeType.getParentType().getName() )) {
			addMimeType( mimeType.getParentType(), nodeSet );
		}
		nodeSet.get( mimeType.getParentType().getName() ).getChildren().add( node );
		nodeSet.put(node.getName(), node);
	}
	
	private void writeNode(MimeTreeNode node, Writer writer, int indent) throws IOException {
		String indention = "";
		if( indent > 0) {
			StringBuilder buffer = new StringBuilder();
			for(int i = 0; i < indent; i++) buffer.append('\t');
			indention = buffer.toString();
		}
		writer.append(indention).append('<').append(node.getNodeName());
		if( node.isMimeType() ) writer.append(" name='").append(node.getName()).append("'");
		if( node.getEncoding() != null ) writer.append(" encoding='").append( node.getEncoding().name() ).append("'");
		if( FileType.BUILDIN.FILE.name().equals( node.getName() ) ) writer.append(" xmlns='").append(XMLNS).append("'");
		// file extensions
		if( node.isMimeType() && node.getFileExtensions().size() > 0 ) {
			writer.append(" extensions='");
			for(int i = 0; i < node.getFileExtensions().size(); i++ ) {
				if( i > 0 ) writer.append(' ');
				writer.append( node.getFileExtensions().get(i));
			}
			writer.append("'");
		}
		// children
		if( node.getChildren().size() > 0) {
			writer.append('>');
			if(indent >= 0) writer.append('\n');
			for( MimeTreeNode child : node.getChildren() ) {
				writeNode( child, writer, indent >= 0 ? indent+1 : indent );
			}
			writer.append(indention).append("</").append(node.getNodeName()).append('>');
		} else {
			writer.append("/>");			
		}
		if(indent >= 0) writer.append('\n');
	}
}

class MimeTreeNode {
	private boolean isMimeType;
	private String name;
	private String nodeName;
	private Charset encoding;
	private List<String> fileExtensions; 
	private List<MimeTreeNode> children = new ArrayList<MimeTreeNode>();
	public MimeTreeNode(FileType type) {
		isMimeType = (type instanceof MimeType);
		name = type.getName();
		if( isMimeType ) {
			nodeName = MimeConfParser.MIME_NODE_NAME;
		} else 	if( BUILDIN.FILE.name().equals( name ) ) {
			nodeName = MimeConfParser.ROOT_NODE_NAME;
		} else if( BUILDIN.SGML.name().equals( name ) || BUILDIN.XML.name().equals( name ) ) {
			nodeName = name;
		} else {
			nodeName = name.substring(0, 1) + name.substring(1).toLowerCase();
		}
		encoding = type.isCharsetDefined() ? type.getCharacterset() : null;
		fileExtensions = isMimeType ? ((MimeType)type).listFileExtensions() : Collections.EMPTY_LIST;
	}
	public List<MimeTreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<MimeTreeNode> children) {
		this.children = children;
	}
	public boolean isMimeType() {
		return isMimeType;
	}
	public String getName() {
		return name;
	}
	public Charset getEncoding() {
		return encoding;
	}
	public List<String> getFileExtensions() {
		return fileExtensions;
	}
	public String getNodeName() {
		return nodeName;
	}
	public boolean equals(Object o) {
		if( o == null ) return false;
		try {
			MimeTreeNode other = (MimeTreeNode) o;
			return name.equals(other.name);
		} catch(ClassCastException ccx) {
			return false;
		}
	}
	public int hashCode() {
		return name.hashCode();
	}
}

class SAXHandler implements ContentHandler {
	
	private Log logger = LogFactory.getLog(this.getClass());

	private List<FileType> typeStack = new ArrayList<FileType>();
	private MimeType currentMime = null;
	
	private int skipCurrentBranch = 0;
	
	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void startElement(String uri, String localName, String qname,
			Attributes atts) throws SAXException {
		if( skipCurrentBranch > 0 ) {
			skipCurrentBranch++;
			return;
		}
		String tagName = tagName(localName, qname);
		if( MimeConfParser.ROOT_NODE_NAME.equals( tagName )) {
			typeStack.add( FileType.FILE );
		} else if( typeStack.size() == 0) {
			throw new SAXException("Illegal root element '" + tagName + "'");
		} else if( MimeConfParser.MIME_NODE_NAME.equals( tagName )) {
			String typeName = atts.getValue("", "name");
			if( MimeType.isValidMimeIdentifier(typeName) ) {
				MimeType oldMime = (MimeType) MimeType.getType( typeName );
				if( oldMime != null ) {
					MimeType.MimeRegistry.remove( oldMime );
					currentMime = new MimeType(typeName, oldMime.getParentType(), MimeType.MimeRegistry);
					currentMime.setPredefined( oldMime.isPredefined() );
				} else {
					currentMime = new MimeType(typeName, typeStack.get( typeStack.size() - 1), MimeType.MimeRegistry);
				}
				typeStack.add( currentMime );
				String encoding = atts.getValue("", "encoding"); 
				if( encoding != null ) {
					try {
						if( Charset.isSupported( encoding )) {
							currentMime.setCharacterset( Charset.forName( encoding ));
						} else {
							logger.warn("Unsupported character set '"+encoding+"' specified for mime type " + typeName);
						}
					} catch( IllegalCharsetNameException icnx) {
						logger.warn("Illegal character set '"+encoding+"' specified for mime type " + typeName);
					}
				}
				String extensionList = atts.getValue("", "extensions");
				if( extensionList != null ) {
					String[] extensions = extensionList.split("\\s");
					for(String ext : extensions) currentMime.addExtension(ext);
				}
			} else {
				skipCurrentBranch = 1;
				logger.warn("Invalid mime type name: '" + typeName + "': skipping");
				return;
			}
		} else {
			String typeName = tagName.toUpperCase();
			FileType type = FileType.getType( typeName );
			if( type == null ) {
				skipCurrentBranch = 1;
				logger.warn("Unknown tag name '" + tagName + "' encountered: skipping");
				return;
			}
			typeStack.add( type );
		}
	}

	public void endElement(String uri, String localName, String qname)
			throws SAXException {
		if( skipCurrentBranch > 0 ) {
			skipCurrentBranch--;			
		} else {
			typeStack.remove( typeStack.size() - 1 );
			if( typeStack.size() > 0 ) {
				FileType current = typeStack.get( typeStack.size() - 1 );
				if( current instanceof MimeType ) {
					currentMime = (MimeType) current;
				} else {
					currentMime = null;
				}
			}
		}
	}

	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		// if( skipCurrentBranch > 0 ) return;
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
	}

	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
	}

	public void setDocumentLocator(Locator arg0) {
	}

	public void skippedEntity(String arg0) throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		if( ! MimeConfParser.XMLNS.equals(uri) ) {
			logger.warn("Unexpected prefix decleration: " + prefix + "=" + uri);
		}
	}
	
	private String tagName(String localName, String qname) {
		if( localName != null && ! localName.isEmpty() ) {
			return localName;
		} else if( qname != null && !qname.isEmpty() ) {
			int colon = qname.indexOf(':'); 
			if( colon < 0 ) {
				return qname;
			} else {
				return qname.substring(colon+1);
			}
		} else {
			throw new IllegalStateException("Tag without a name");
		}
	}
}

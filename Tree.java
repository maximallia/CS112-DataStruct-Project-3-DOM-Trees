package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 *
 */

public class Tree {

	/**
	 * Root node
	 */
	TagNode root=null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 *
	 * @param sc Scanner for input HTML file
	 */

	public Tree(Scanner sc) {
		this.sc = sc ;
		root = null ;
	}

	private boolean isStart( String str ) {
		int len = str.length() ;
		return (str.charAt( 0 ) == '<') && (str.charAt( len - 1 ) == '>') && (str.charAt( 1 ) != '/') ;
	}

	private boolean isEnd( String str ) {
		int len = str.length() ;
		return (str.charAt( 0 ) == '<') && (str.charAt( len - 1 ) == '>') && (str.charAt( 1 ) == '/') ;
	}

	private String startTag( String str ) {
		return str.substring( 1, str.length() - 1 ) ;
	}

	private void addNode( TagNode parent, TagNode theNode ) {
		if ( parent == null || theNode == null ) return ;
		if ( parent.firstChild == null ) {
			parent.firstChild = theNode ;
		} else {
			TagNode prevSib = parent.firstChild ;
			while ( prevSib.sibling != null ) prevSib = prevSib.sibling ;
			prevSib.sibling = theNode ;
		}
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object.
	 *
	 * The root of the tree that is built is referenced by the root field of this object.
	 */

	public void build() {
		/** COMPLETE THIS METHOD **/

		Stack< TagNode > stk = new Stack< TagNode > () ;
		String line = sc.nextLine() ;
		String tag = startTag( line ) ;

		TagNode theNode = new TagNode( tag, null, null ) ;
		root = theNode ;


		stk.push( theNode ) ;

		while ( !stk.isEmpty() ) {
			TagNode current = stk.peek();

			line = sc.nextLine() ;
			if ( !isEnd( line ) ) {
				if ( isStart( line ) ) {
					tag = startTag( line ) ;
				} else {
					tag = line ;
				}

				theNode = new TagNode( tag, null, null ) ;

				if( isStart( line) ) {
					stk.push( theNode ) ;
				}

				addNode( current, theNode ) ;

			} else {
				stk.pop() ;
			}
		}
	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 *
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */

	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/

		Stack< TagNode > stk = new Stack< TagNode > () ;
		stk.push( root ) ;

		while ( !stk.isEmpty() ) {
			TagNode current = stk.peek() ;
			stk.pop() ;

			if ( current.tag.equals( oldTag ) ) {
				current.tag = newTag ;
			}

			if ( current.sibling != null ) {
				stk.push( current.sibling ) ;
			}

			if ( current.firstChild != null ) {
				stk.push( current.firstChild ) ;
			}
		}
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 *
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/

		Stack< TagNode > stk = new Stack< TagNode > () ;
		stk.push( root ) ;

		int cRow = 0 ;
		while ( !stk.isEmpty() ) {
			TagNode current = stk.peek() ;
			stk.pop() ;

			if( current.tag.equals( "tr" ) ) {
				cRow++ ;
			}

			if (current.tag.equals( "td" ) && cRow == row ) {
				TagNode nNode = new TagNode( "b", current.firstChild, null ) ;
				current.firstChild = nNode ;
			}

			if ( current.sibling != null ) {
				stk.push( current.sibling ) ;
			}

			if ( current.firstChild != null ) {
				if ( current.firstChild.tag.equals( "tr" ) ) cRow = 0 ;
				stk.push( current.firstChild ) ;
			}
		}
	}

	private void removeList( TagNode nNode ) {
		TagNode last = null ;
		for( TagNode current = nNode.firstChild; current != null; current = current.sibling ) {
			if ( current.tag.equals( "li" ) ) {
				current.tag = "p" ;
			}
			if ( current.sibling == null ) {
				last = current ;
			}
		}

		last.sibling = nNode.sibling ;
	}

	private void removeChildrenTag( TagNode theNode, String tag ) {
		TagNode nNode;
		if ( (theNode.sibling != null) && (theNode.sibling.tag.equals(tag) ) ) {
			 nNode = theNode.sibling ;
		} else {
			nNode = null ;
		}
		if ( nNode != null ) {
			removeList( nNode ) ;
			theNode.sibling = nNode.firstChild ;
		}

		if ( (theNode.firstChild != null) && (theNode.firstChild.tag.equals( tag ) ) ) {
			nNode = theNode.firstChild ;
		} else {
			nNode = null ;
		}

		if ( nNode != null ) {
			removeList( nNode ) ;
			theNode.firstChild = nNode.firstChild ;
		}

	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and,
	 * in addition, all the li tags immediately under the removed tag are converted to p tags.
	 *
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */

	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		Stack< TagNode > stk = new Stack< TagNode > () ;
		stk.push( root ) ;

		while ( !stk.isEmpty() ) {
			TagNode current = stk.peek() ;
			stk.pop();
			removeChildrenTag( current, tag ) ;

			if ( current.sibling != null ) {
				stk.push( current.sibling ) ;
			}
			if ( current.firstChild != null ) {
				stk.push(current.firstChild ) ;
			}
		}
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 *
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */

	private boolean checkChar( char c ) {
		return ( ( c == '.' )  || ( c == ',' ) || ( c == '?' ) || ( c == '!' ) || ( c == ':' ) || ( c == ';' ) ) ;
	}

	private boolean tagCheck( String tag ) {
		return (tag.equals( "em" ) || (tag.equals("b") ) )  ;
	}

	private void addTag2(String word, String tag, TagNode current){
		if (current == null ) return ;

		String nTag = current.tag.toLowerCase() ; // case insensitive match
		String nWord = word.toLowerCase() ; // case insensitive match


		if ( nTag.contains( nWord ) ) {
			if ( nTag.equals( word ) ) {
				String tempWord = current.tag ;
				current.tag = tag ;
				current.firstChild = new TagNode( tempWord, current.firstChild, null ) ;
			} else {
				TagNode ptrSibling = current.sibling ;
				int idx = nTag.indexOf( nWord ) ;
				int wordEnd = idx + word.length();
				int nTagEnd = nTag.length();

				String wordsTag = current.tag.substring( 0, idx ) ;
				String targetWord = current.tag.substring( idx, wordEnd ) ;
				String betwWordTag = current.tag.substring( wordEnd, nTagEnd ) ;
				String afterTag = current.tag.substring( nTagEnd ) ;


				if( betwWordTag.length() > 1 ) {

					// checkChar( betwWordTag.charAt( 0 ) ) checks if word after ,.:;?!
					// checkChar( betwWordTag.charAt( 1 ) ) checks if word after 2nd ,.:;?!


					if( (checkChar( betwWordTag.charAt( 0 ) ) )  && ( !checkChar( betwWordTag.charAt( 1 ) ) ) ) {
						afterTag = "" + betwWordTag.charAt( 0 ) ;
						betwWordTag = betwWordTag.substring( 1 ) ;

					}
				}

				if( ( betwWordTag.length() == 0 ) || ( betwWordTag.length() >= 1 &&
						( betwWordTag.charAt( 0 ) == ' ' || checkChar( betwWordTag.charAt( 0 ) ) ) ) ) {

					if( ( betwWordTag.length() == 1 ) && ( checkChar( betwWordTag.charAt( 0 ) ) ) ) {
						targetWord = targetWord + betwWordTag ;
						betwWordTag = "" ;
					}
					
					String newWord = targetWord + afterTag ;
					current.tag = wordsTag ;
					current.sibling = new TagNode( tag, new TagNode( newWord, null, null ), null ) ;

					if ( betwWordTag.length() > 0 ) {

						if ( ptrSibling != null ) {
							
							current.sibling.sibling = new TagNode( tag, null, ptrSibling ) ;

						}

						else {
							current.sibling.sibling = new TagNode( betwWordTag, null, null ) ;

						}

					} else if (ptrSibling != null ) {
						current.sibling.sibling = ptrSibling ;
					}
				}
			}

			if ( current.sibling != null ) {
				addTag2( word, tag, current.sibling.sibling) ;
			}

		} else {
			addTag2( word, tag, current.firstChild ) ;
			addTag2( word, tag, current.sibling ) ;

		}
	}
	
	public void addTag(String word, String tag) {
		if(tagCheck(tag)) {
			addTag2(word, tag, root ) ;
		}
	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 *
	 * @return HTML string, including new lines.
	 */


	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");
			}
		}
	}

	/**
	 * Prints the DOM tree.
	 *
	 */
	public void print() {
		print(root, 1);
	}

	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}

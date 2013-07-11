/**
 * 
 */
package edu.psu.citeseerx.myciteseer.acl;

/**
 * Exception used when an expected method is not found in within the domain 
 * object class.
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev$$ $$Date$$
 */
public class MethodNotImplemented extends RuntimeException {

	private static final long serialVersionUID = 5467461315334235816L;

	public MethodNotImplemented(String message) {
		super(message);
	} //- MethodNotImplemented
} //- class MethodNotImplemented

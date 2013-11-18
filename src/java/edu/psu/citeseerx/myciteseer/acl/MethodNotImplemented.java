/**
 * 
 */
package edu.psu.citeseerx.myciteseer.acl;

/**
 * Exception used when an expected method is not found in within the domain 
 * object class.
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev: 191 $$ $$Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $$
 */
public class MethodNotImplemented extends RuntimeException {

	private static final long serialVersionUID = 5467461315334235816L;

	public MethodNotImplemented(String message) {
		super(message);
	} //- MethodNotImplemented
} //- class MethodNotImplemented

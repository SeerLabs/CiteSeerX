package edu.psu.citeseerx.domain;

/*
 * Document Properties - various properties of
 * a document through this class including state
 * and in the future security, completeness etc.
 */

/*
 * @author Pradeep Teregowda, 2010
 */

public class DocumentProperties {
	/* Right now we only use the embedded state */
	private int state;
	
	public static final int LOGICAL_DELETE = 0;
	public static final int IS_PUBLIC = 1;
	public static final int IS_DMCA = 2;
	public static final int IS_RESTRICTED = 3;
	public static final int IS_TRANSIENT = 4;
	public static final int IS_PDFREDIRECT = 5;
	public static final int IS_REMOVED = 6;
	
    	
	protected static final int[] states = 
	{
		IS_PUBLIC, LOGICAL_DELETE, IS_DMCA, IS_RESTRICTED,
		IS_TRANSIENT, IS_PDFREDIRECT, IS_REMOVED
	};

	public boolean isPublic() {
		if(this.state == IS_PUBLIC) {
			return true;
		}
		else { return false; }
	}
	
	public void setPublic(boolean sPublic) {
		if(sPublic) {
			this.state = IS_PUBLIC;
		}
		else {
			// Legacy behaviour should actually be
			// transient
			this.state = LOGICAL_DELETE;
		}
	}
	
	public boolean isDeleted() {
		if(this.state == LOGICAL_DELETE) {
			return true;
		}
		else { return false; }
	}
	
	public void setDeleted() {
		this.state = LOGICAL_DELETE;
	}
	
	public boolean isDMCA() {
		if(this.state == IS_DMCA) {
			return true;
		}
		else { return false; }
	}
	
	public void setDMCA() {
		this.state = IS_DMCA;
	}
	
        public boolean isRemoved() {
                if(this.state == IS_REMOVED) {
                        return true;
                }
                else { return false; }
        }
        public void setRemoved() {
                this.state = IS_REMOVED;
        }
   
	public boolean isRestricted() {
		if(this.state == IS_RESTRICTED ) {
			return true;
		}
		else { return false; }
	}
	
	public void setRestricted() {
		this.state = IS_RESTRICTED;
	}
	
	public boolean isTransient() {
		if (this.state == IS_TRANSIENT) {
			return true;
		}
		else { return false; }
	}
	
	public boolean isPDFRedirect() {
		if (this.state == IS_PDFREDIRECT) {
			return true;
		}
		return false;
	}
	
	public void setPDFRedirect() {
		this.state = IS_PDFREDIRECT;
	}
	
	public void setTransient() {
		this.state = IS_TRANSIENT;
	}
	
	public void setState(int toSet) {
		this.state = toSet;
	}
	
	public int getState() {
		return this.state;
	}
}

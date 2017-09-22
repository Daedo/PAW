package gui.preferenceTree;

import java.util.Optional;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;


/**
 * A Node for the {@link PreferenceTree}, can't have children.
 * Represents a simple object.
 * 
 * @author Dominik
 */
public class PreferenceTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	public PreferenceTreeNode(PreferenceNodeObject obj) {
		super(obj);
	}

	public PreferenceTreeNode(int objectNumber,String displayName) {
		this(new PreferenceNodeObject(objectNumber, displayName));
	}

	public PreferenceTreeNode(int objectNumber) {
		this(objectNumber,null);		
	}

	public int getObjectNumber() {
		Integer obj = ((PreferenceNodeObject) getUserObject()).number;
		return obj.intValue();
	}

	/**
	 * Produces all {@link PreferenceTreeNode} Objects from 1 to max
	 * @param max the upper bound. Has to be >=0
	 * @return an array ob objects
	 */
	public static PreferenceTreeNode[] generateRange(int max) {
		assert max>=0;

		PreferenceTreeNode[] out = new PreferenceTreeNode[max];
		for(int i=0;i<max;i++) {
			out[i] = new PreferenceTreeNode(i);
		}
		return out;
	}

	@Override
	public String toString() {
		PreferenceNodeObject obj = (PreferenceNodeObject) getUserObject();
		if(obj.name.isPresent()) {
			return obj.name.get();
		}

		return "Object "+getObjectNumber();
	}

	@Override
	public void setUserObject(Object userObject) {
		if(userObject!=null && userObject instanceof PreferenceNodeObject) {
			super.setUserObject(userObject);
		}
	}

	@Override
	public void add(MutableTreeNode newChild) {
		throw new IllegalStateException("PreferenceTreeNodes can't have children");
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	/**
	 * Helper Class.
	 * The object held by {@link PreferenceTreeNode}s.
	 * Containing an object number and an optional text.
	 * @author Dominik
	 *
	 */
	public static class PreferenceNodeObject {
		public Optional<String> name;
		public Integer number;
		public PreferenceNodeObject(Integer n,String s) {
			number = n;
			if(s!=null) {
				name = Optional.of(s);
			} else {
				name = Optional.empty();
			}
		}

		public PreferenceNodeObject(int n,String s) {
			this(new Integer(n),s);
		}
	}
}
